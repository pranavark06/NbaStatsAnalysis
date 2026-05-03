package G16.menik._0.controllers;

import java.math.BigDecimal;
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
public class RankingsController {

    private final DataSource dataSource;

    public RankingsController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/rankings")
    public String rankings(@RequestParam(value = "season", defaultValue = "") String season,
                           @RequestParam(value = "minGames", defaultValue = "10") int minGames,
                           Model model) {
        List<String> seasons = findSeasons();
        String selectedSeason = season.trim();
        if (selectedSeason.isEmpty() && !seasons.isEmpty()) {
            selectedSeason = seasons.get(0);
        }

        int safeMinGames = Math.max(1, Math.min(minGames, 82));

        model.addAttribute("title", "Team Rankings");
        model.addAttribute("pageTitle", "Team Rankings");
        model.addAttribute("pageSubtitle", "Rank NBA teams by regular season wins and scoring margin.");
        final String currentSeason = selectedSeason;
        model.addAttribute("seasons", seasons.stream()
                .map(value -> new SeasonOption(value, value.equals(currentSeason)))
                .toList());
        model.addAttribute("selectedSeason", selectedSeason);
        model.addAttribute("minGames", safeMinGames);
        model.addAttribute("rankings", selectedSeason.isEmpty()
                ? List.of()
                : findRankings(selectedSeason, safeMinGames));
        return "rankings";
    }

    private List<String> findSeasons() {
        String sql = """
                SELECT DISTINCT season_id
                FROM game
                WHERE season_type = ?
                ORDER BY season_id DESC
                """;

        List<String> seasons = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "Regular Season");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    seasons.add(rs.getString("season_id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load seasons", e);
        }
        return seasons;
    }

    private List<TeamRankingView> findRankings(String season, int minGames) {
        String sql = """
                WITH team_games AS (
                    SELECT season_id,
                           team_id_home AS team_id,
                           team_name_home AS team_name,
                           team_abbreviation_home AS abbreviation,
                           CASE WHEN wl_home = 'W' THEN 1 ELSE 0 END AS win,
                           pts_home AS points_for,
                           pts_away AS points_against,
                           plus_minus_home AS point_diff
                    FROM game
                    WHERE season_type = ? AND season_id = ?
                    UNION ALL
                    SELECT season_id,
                           team_id_away AS team_id,
                           team_name_away AS team_name,
                           team_abbreviation_away AS abbreviation,
                           CASE WHEN wl_away = 'W' THEN 1 ELSE 0 END AS win,
                           pts_away AS points_for,
                           pts_home AS points_against,
                           plus_minus_away AS point_diff
                    FROM game
                    WHERE season_type = ? AND season_id = ?
                ),
                team_totals AS (
                    SELECT tg.team_id,
                           COALESCE(t.full_name, tg.team_name) AS team_name,
                           tg.abbreviation,
                           COUNT(*) AS games_played,
                           SUM(tg.win) AS wins,
                           COUNT(*) - SUM(tg.win) AS losses,
                           ROUND(SUM(tg.win) / COUNT(*), 3) AS win_pct,
                           ROUND(AVG(tg.points_for), 1) AS points_for,
                           ROUND(AVG(tg.points_against), 1) AS points_against,
                           ROUND(AVG(tg.point_diff), 1) AS point_diff
                    FROM team_games tg
                    LEFT JOIN team t ON t.id = tg.team_id
                    GROUP BY tg.team_id, COALESCE(t.full_name, tg.team_name), tg.abbreviation
                    HAVING COUNT(*) >= ?
                )
                SELECT ROW_NUMBER() OVER (
                           ORDER BY win_pct DESC, wins DESC, point_diff DESC, points_for DESC
                       ) AS rank_number,
                       team_name,
                       abbreviation,
                       games_played,
                       wins,
                       losses,
                       win_pct,
                       points_for,
                       points_against,
                       point_diff
                FROM team_totals
                ORDER BY rank_number
                """;

        List<TeamRankingView> rankings = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "Regular Season");
            pstmt.setString(2, season);
            pstmt.setString(3, "Regular Season");
            pstmt.setString(4, season);
            pstmt.setInt(5, minGames);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    rankings.add(new TeamRankingView(
                            rs.getInt("rank_number"),
                            rs.getString("team_name"),
                            rs.getString("abbreviation"),
                            rs.getInt("games_played"),
                            rs.getInt("wins"),
                            rs.getInt("losses"),
                            rs.getBigDecimal("win_pct"),
                            rs.getBigDecimal("points_for"),
                            rs.getBigDecimal("points_against"),
                            rs.getBigDecimal("point_diff")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load team rankings", e);
        }
        return rankings;
    }

    public record TeamRankingView(int rankNumber, String teamName, String abbreviation, int gamesPlayed, int wins, int losses, BigDecimal winPct, BigDecimal pointsFor, BigDecimal pointsAgainst, BigDecimal pointDiff) {
    }

    public record SeasonOption(String value, boolean selected) {
    }
}
