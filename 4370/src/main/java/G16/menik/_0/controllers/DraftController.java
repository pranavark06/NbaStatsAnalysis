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

@Controller
public class DraftController {

    private final DataSource dataSource;

    public DraftController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/draft")
    public String draft(@RequestParam(value = "season", defaultValue = "") String season,
                        @RequestParam(value = "query", defaultValue = "") String query,
                        Model model) {
        String trimmedSeason = season.trim();
        String trimmedQuery = query.trim();
        boolean hasSearch = !trimmedSeason.isEmpty() || !trimmedQuery.isEmpty();

        model.addAttribute("title", "Draft Explorer");
        model.addAttribute("pageTitle", "Draft Explorer");
        model.addAttribute("pageSubtitle", "Browse draft picks by year, player, or team.");
        model.addAttribute("season", trimmedSeason);
        model.addAttribute("query", trimmedQuery);
        model.addAttribute("hasSearch", hasSearch);
        model.addAttribute("picks", hasSearch ? findPicks(trimmedSeason, trimmedQuery.toLowerCase()) : List.of());
        return "draft";
    }

    private List<DraftPickView> findPicks(String season, String query) {
        String sql = """
                SELECT dh.season,
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

    private String blankFallback(String value) {
        return value == null || value.isBlank() ? "Unknown" : value;
    }

    public record DraftPickView(String season, int overallPick, int roundNumber, int roundPick,
                                String playerName, String teamName, String teamAbbreviation,
                                String position, String school, String country) {
    }
}
