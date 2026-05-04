package G16.menik._0.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for handling NBA draft history exploration.
 * Provides endpoints for searching draft picks by year, player name, and team.
 */
@Controller
public class DraftController {

    private final DataSource dataSource;

    /**
     * Constructs DraftController with the specified DataSource.
     *
     * @param dataSource the data source for database connections
     */
    public DraftController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Handles requests to the draft explorer page.
     * Searches draft history based on season year and player/team query parameters.
     *
     * @param season the draft season year filter (optional, empty string if not provided)
     * @param query the search query for player name or team (optional, empty string if not provided)
     * @param model the Spring model to pass data to the template
     * @return the view name "draft" for rendering draft explorer page
     */
    @GetMapping("/draft")
    public String draft(@RequestParam(value = "season", defaultValue = "") String season,
                        @RequestParam(value = "query", defaultValue = "") String query,
                        Model model) {
        String trimmedSeason = season.trim();
        String trimmedQuery = query.trim();
        boolean hasSearch = !trimmedSeason.isEmpty() || !trimmedQuery.isEmpty();

        model.addAttribute("title", "Draft Explorer");
        model.addAttribute("pageTitle", "Draft Explorer");
        model.addAttribute("pageSubtitle", "Search by draft year, player, or team to see matching draft picks.");
        model.addAttribute("season", trimmedSeason);
        model.addAttribute("query", trimmedQuery);
        model.addAttribute("hasSearch", hasSearch);
        List<DraftPickView> picks = hasSearch ? findPicks(trimmedSeason, trimmedQuery.toLowerCase()) : List.of();
        model.addAttribute("picks", picks);
        model.addAttribute("pickCount", picks.size());
        return "draft";
    }

    /**
     * Searches for draft picks matching the specified criteria.
     * Filters by season and player/team name using case-insensitive matching.
     *
     * @param season the draft season year to filter by (empty string for no filter)
     * @param query the search query for player or team names (empty string for no filter)
     * @return a list of DraftPickView objects matching the search criteria
     */
    private List<DraftPickView> findPicks(String season, String query) {
        String sql = """
                SELECT distinct dh.season,
                       dh.round_number,
                       dh.round_pick,
                       dh.overall_pick,
                       dh.player_name,
                       dh.team_city,
                       dh.team_name,
                       dh.team_abbreviation,
                       COALESCE(NULLIF(cpi.position, ''), NULLIF(dcs.position, '')) AS position,
                       COALESCE(NULLIF(cpi.school, ''), NULLIF(dh.organization, '')) AS school,
                       cpi.country
                FROM draft_history dh
                LEFT JOIN common_player_info cpi
                    ON cpi.person_id = dh.person_id
                    OR LOWER(cpi.display_first_last) = LOWER(dh.player_name)
                LEFT JOIN draft_combine_stats dcs
                    ON dcs.player_id = dh.person_id
                    OR LOWER(dcs.player_name) = LOWER(dh.player_name)
                WHERE (? = '' OR dh.season = ?)
                  AND (? = '' OR LOWER(dh.player_name) LIKE ?
                       OR LOWER(dh.team_name) LIKE ?
                       OR LOWER(dh.team_city) LIKE ?
                       OR LOWER(dh.team_abbreviation) = ?)
                ORDER BY CAST(dh.season AS UNSIGNED) DESC, dh.overall_pick ASC
                """;

        List<DraftPickView> picks = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String likeQuery = "%" + query + "%";
            pstmt.setString(1, season);
            pstmt.setString(2, season);
            pstmt.setString(3, query);
            pstmt.setString(4, likeQuery);
            pstmt.setString(5, likeQuery);
            pstmt.setString(6, likeQuery);
            pstmt.setString(7, query);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    picks.add(new DraftPickView(
                            rs.getString("season"),
                            rs.getInt("overall_pick"),
                            rs.getInt("round_number"),
                            rs.getInt("round_pick"),
                            rs.getString("player_name"),
                            rs.getString("team_city") + " " + rs.getString("team_name"),
                            rs.getString("team_abbreviation"),
                            blankFallback(rs.getString("position")),
                            blankFallback(rs.getString("school")),
                            blankFallback(rs.getString("country"))
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load draft picks", e);
        }
        return picks;
    }

    /**
     * Returns a fallback display value when the input is null or blank.
     *
     * @param value the input string to evaluate
     * @return {@code "Unknown"} when the input is null/blank; otherwise the original value
     */
    private String blankFallback(String value) {
        return value == null || value.isBlank() ? "Unknown" : value;
    }

    /**
     * View model representing a single NBA draft pick row for the draft explorer page.
     *
     * @param season the draft season year
     * @param overallPick the overall pick number in the draft
     * @param roundNumber the draft round number
     * @param roundPick the pick number within the round
     * @param playerName the drafted player's full name
     * @param teamName the drafting team display name
     * @param teamAbbreviation the drafting team abbreviation
     * @param position the player's listed position
     * @param school the player's school or organization
     * @param country the player's country
     */
    public record DraftPickView(String season, int overallPick, int roundNumber, int roundPick,
                                String playerName, String teamName, String teamAbbreviation,
                                String position, String school, String country) {
    }
}
