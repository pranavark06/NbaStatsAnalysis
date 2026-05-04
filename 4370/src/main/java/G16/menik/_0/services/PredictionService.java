package G16.menik._0.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.stereotype.Service;

@Service
public class PredictionService {

    private static final String SEASON_ID = "22018";
    private static final String SEASON_TYPE = "Regular Season";
    private final DataSource dataSource;

    public PredictionService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<TeamOption> getTeams() throws SQLException {
        return new ArrayList<>(loadTeamStats().values()).stream()
                .map(team -> new TeamOption(team.abbreviation(), team.teamName() + " (" + team.abbreviation() + ")"))
                .toList();
    }

    public PredictionResult predict(String homeAbbreviation, String awayAbbreviation) throws SQLException {
        Map<String, TeamStats> stats = loadTeamStats();
        TeamStats home = stats.get(homeAbbreviation);
        TeamStats away = stats.get(awayAbbreviation);
        if (home == null || away == null) {
            throw new SQLException("Unable to find both teams for prediction.");
        }

        List<GameOutcome> outcomes = loadGameOutcomes();
        double[] weights = trainLogisticModel(outcomes, stats);
        double probability = clamp(sigmoid(dot(weights, features(home, away))), 0.05, 0.95);
        double awayProbability = 1.0 - probability;
        double projectedMargin = clamp(Math.log(probability / awayProbability) * 6.5, -30.0, 30.0);
        double edge = Math.abs(probability - 0.5);

        String winner = probability >= 0.5 ? home.teamName() : away.teamName();
        String confidence = edge >= 0.20 ? "High" : edge >= 0.10 ? "Medium" : "Low";
        String recommendation = edge < 0.06
                ? "This matchup is a toss-up between both tams."
                : winner + " is favored by the model.";

        return new PredictionResult(
                home.teamName(),
                away.teamName(),
                home.abbreviation(),
                away.abbreviation(),
                formatRecord(home),
                formatRecord(away),
                formatPercent(probability),
                formatPercent(awayProbability),
                formatPercentWidth(probability),
                formatPercentWidth(awayProbability),
                winner,
                formatMargin(projectedMargin, home.teamName(), away.teamName()),
                confidence,
                recommendation,
                outcomes.size(),
                formatDecimal(home.winPct()),
                formatDecimal(away.winPct()),
                formatSigned(home.avgPointDiff()),
                formatSigned(away.avgPointDiff()),
                formatDecimal(home.avgPointsFor()),
                formatDecimal(away.avgPointsFor()),
                formatDecimal(home.avgPointsAgainst()),
                formatDecimal(away.avgPointsAgainst())
        );
    }

    private Map<String, TeamStats> loadTeamStats() throws SQLException {
        String sql = """
                WITH team_games AS (
                    SELECT team_abbreviation_home AS abbreviation,
                           team_name_home AS team_name,
                           CASE WHEN wl_home = 'W' THEN 1 ELSE 0 END AS win,
                           pts_home AS points_for,
                           pts_away AS points_against,
                           plus_minus_home AS point_diff
                    FROM game
                    WHERE season_id = ? AND season_type = ?
                    UNION ALL
                    SELECT team_abbreviation_away AS abbreviation,
                           team_name_away AS team_name,
                           CASE WHEN wl_away = 'W' THEN 1 ELSE 0 END AS win,
                           pts_away AS points_for,
                           pts_home AS points_against,
                           plus_minus_away AS point_diff
                    FROM game
                    WHERE season_id = ? AND season_type = ?
                )
                SELECT abbreviation,
                       MAX(team_name) AS team_name,
                       COUNT(*) AS games_played,
                       SUM(win) AS wins,
                       COUNT(*) - SUM(win) AS losses,
                       SUM(win) / COUNT(*) AS win_pct,
                       AVG(points_for) AS avg_points_for,
                       AVG(points_against) AS avg_points_against,
                       AVG(point_diff) AS avg_point_diff
                FROM team_games
                GROUP BY abbreviation
                ORDER BY team_name
                """;

        Map<String, TeamStats> stats = new LinkedHashMap<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, SEASON_ID);
            pstmt.setString(2, SEASON_TYPE);
            pstmt.setString(3, SEASON_ID);
            pstmt.setString(4, SEASON_TYPE);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    TeamStats team = new TeamStats(
                            rs.getString("abbreviation"),
                            rs.getString("team_name"),
                            rs.getInt("games_played"),
                            rs.getInt("wins"),
                            rs.getInt("losses"),
                            rs.getDouble("win_pct"),
                            rs.getDouble("avg_points_for"),
                            rs.getDouble("avg_points_against"),
                            rs.getDouble("avg_point_diff")
                    );
                    stats.put(team.abbreviation(), team);
                }
            }
        }
        return stats;
    }

    private List<GameOutcome> loadGameOutcomes() throws SQLException {
        String sql = """
                SELECT team_abbreviation_home AS home_abbreviation,
                       team_abbreviation_away AS away_abbreviation,
                       CASE WHEN wl_home = 'W' THEN 1 ELSE 0 END AS home_win
                FROM game
                WHERE season_id = ? AND season_type = ?
                """;

        List<GameOutcome> outcomes = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, SEASON_ID);
            pstmt.setString(2, SEASON_TYPE);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    outcomes.add(new GameOutcome(
                            rs.getString("home_abbreviation"),
                            rs.getString("away_abbreviation"),
                            rs.getInt("home_win")
                    ));
                }
            }
        }
        return outcomes;
    }

    private double[] trainLogisticModel(List<GameOutcome> outcomes, Map<String, TeamStats> stats) {
        double[] weights = new double[4];
        double learningRate = 0.08;
        int usableSamples = Math.max(1, outcomes.size());

        for (int iteration = 0; iteration < 1200; iteration++) {
            double[] gradient = new double[weights.length];
            for (GameOutcome outcome : outcomes) {
                TeamStats home = stats.get(outcome.homeAbbreviation());
                TeamStats away = stats.get(outcome.awayAbbreviation());
                if (home == null || away == null) {
                    continue;
                }

                double[] x = features(home, away);
                double prediction = sigmoid(dot(weights, x));
                double error = prediction - outcome.homeWin();
                for (int j = 0; j < weights.length; j++) {
                    gradient[j] += error * x[j];
                }
            }

            for (int j = 0; j < weights.length; j++) {
                weights[j] -= learningRate * gradient[j] / usableSamples;
            }
        }
        return weights;
    }

    private double[] features(TeamStats home, TeamStats away) {
        double scoringEdge = (home.avgPointsFor() - away.avgPointsAgainst())
                - (away.avgPointsFor() - home.avgPointsAgainst());
        return new double[] {
                1.0,
                home.winPct() - away.winPct(),
                (home.avgPointDiff() - away.avgPointDiff()) / 10.0,
                scoringEdge / 10.0
        };
    }

    private double dot(double[] weights, double[] features) {
        double total = 0.0;
        for (int i = 0; i < weights.length; i++) {
            total += weights[i] * features[i];
        }
        return total;
    }

    private double sigmoid(double value) {
        if (value > 35.0) {
            return 1.0;
        }
        if (value < -35.0) {
            return 0.0;
        }
        return 1.0 / (1.0 + Math.exp(-value));
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private String formatRecord(TeamStats team) {
        return team.wins() + "-" + team.losses();
    }

    private String formatPercent(double value) {
        return String.format(Locale.US, "%.1f%%", value * 100.0);
    }

    private String formatPercentWidth(double value) {
        return String.format(Locale.US, "%.1f%%", value * 100.0);
    }

    private String formatDecimal(double value) {
        return String.format(Locale.US, "%.1f", value);
    }

    private String formatSigned(double value) {
        return String.format(Locale.US, "%+.1f", value);
    }

    private String formatMargin(double margin, String homeTeam, String awayTeam) {
        String team = margin >= 0 ? homeTeam : awayTeam;
        return String.format(Locale.US, "%s by %.1f", team, Math.abs(margin));
    }

    private record TeamStats(String abbreviation, String teamName, int gamesPlayed, int wins, int losses,
                             double winPct, double avgPointsFor, double avgPointsAgainst,
                             double avgPointDiff) {
    }

    private record GameOutcome(String homeAbbreviation, String awayAbbreviation, int homeWin) {
    }

    public record TeamOption(String abbreviation, String displayName) {
    }

    public record PredictionResult(String homeTeamName, String awayTeamName,
                                   String homeAbbreviation, String awayAbbreviation,
                                   String homeRecord, String awayRecord,
                                   String homeWinProbability, String awayWinProbability,
                                   String homeProbabilityWidth, String awayProbabilityWidth,
                                   String projectedWinner, String projectedMargin,
                                   String confidenceLabel, String recommendation,
                                   int trainingGames,
                                   String homeWinPct, String awayWinPct,
                                   String homePointDiff, String awayPointDiff,
                                   String homePointsFor, String awayPointsFor,
                                   String homePointsAgainst, String awayPointsAgainst) {
    }
}
