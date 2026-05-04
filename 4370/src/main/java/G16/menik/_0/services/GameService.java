package G16.menik._0.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import G16.menik._0.models.Game;

/**
 * Service class for retrieving NBA game data from the database.
 * Provides methods to fetch games by various criteria including date range, team, and game ID.
 */
@Service
public class GameService {

    private final DataSource dataSource;

    /**
     * Constructs GameService with the specified DataSource.
     *
     * @param dataSource the data source for database connections
     */
    @Autowired
    public GameService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Retrieves all games from the database, limited to the 100 most recent.
     *
     * @return a list of Game objects ordered by game date descending
     * @throws SQLException if a database error occurs
     */
    public List<Game> getAllGames() throws SQLException {
        String sql = "SELECT * FROM game ORDER BY game_date DESC LIMIT 100";
        List<Game> games = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                games.add(mapRow(rs));
            }
        }
        return games;
    }

    /**
     * Retrieves the most recent games up to the specified limit.
     *
     * @param limit the maximum number of games to retrieve
     * @return a list of Game objects ordered by game date descending
     * @throws SQLException if a database error occurs
     */
    public List<Game> getRecentGames(int limit) throws SQLException {
        String sql = "SELECT * FROM game ORDER BY game_date DESC LIMIT ?";
        List<Game> games = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    games.add(mapRow(rs));
                }
            }
        }
        return games;
    }

    /**
     * Retrieves a specific game by its unique game ID.
     *
     * @param gameId the unique identifier for the game
     * @return the Game object if found, or {@code null} if not found
     * @throws SQLException if a database error occurs
     */
    public Game getGameById(String gameId) throws SQLException {
        String sql = "SELECT * FROM game WHERE game_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, gameId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all games involving a specific team.
     * Includes games where the team is either home or away.
     *
     * @param teamAbbreviation the team's NBA abbreviation (e.g., "LAL", "BOS")
     * @return a list of Game objects involving the specified team, ordered by date descending
     * @throws SQLException if a database error occurs
     */
    public List<Game> getGamesByTeam(String teamAbbreviation) throws SQLException {
        String sql = "SELECT * FROM game WHERE team_abbreviation_home = ? OR team_abbreviation_away = ? ORDER BY game_date DESC";
        List<Game> games = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, teamAbbreviation);
            pstmt.setString(2, teamAbbreviation);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    games.add(mapRow(rs));
                }
            }
        }
        return games;
    }

    /**
     * Retrieves all games within a specified date range.
     *
     * @param startDate the start date for the range (format: YYYY-MM-DD)
     * @param endDate the end date for the range (format: YYYY-MM-DD)
     * @return a list of Game objects within the date range, ordered by date descending
     * @throws SQLException if a database error occurs
     */
    public List<Game> getGamesByDateRange(String startDate, String endDate) throws SQLException {
        String sql = "SELECT * FROM game WHERE game_date BETWEEN ? AND ? ORDER BY game_date DESC";
        List<Game> games = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    games.add(mapRow(rs));
                }
            }
        }
        return games;
    }

    public List<TeamOption> getAllTeams() throws SQLException {
        String sql = """
                SELECT team_abbreviation_home AS abbreviation,
                       MAX(team_name_home) AS team_name
                FROM game
                GROUP BY team_abbreviation_home
                ORDER BY team_name
                """;
        List<TeamOption> teamOptions = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String abbreviation = rs.getString("abbreviation");
                String teamName = rs.getString("team_name");
                teamOptions.add(new TeamOption(abbreviation, teamName + " (" + abbreviation + ")"));
            }
        }
        return teamOptions;
    }

    public record TeamOption(String abbreviation, String displayName) {
    }

    private Game mapRow(ResultSet rs) throws SQLException {
        Game game = new Game();
        game.setSeasonId(rs.getString("season_id"));
        game.setTeamIdHome(rs.getString("team_id_home"));
        game.setTeamAbbreviationHome(rs.getString("team_abbreviation_home"));
        game.setTeamNameHome(rs.getString("team_name_home"));
        game.setGameId(rs.getString("game_id"));
        game.setGameDate(rs.getTimestamp("game_date"));
        game.setMatchupHome(rs.getString("matchup_home"));
        game.setWlHome(rs.getString("wl_home"));
        game.setMin(rs.getInt("min"));
        game.setFgmHome(rs.getDouble("fgm_home"));
        game.setFgaHome(rs.getDouble("fga_home"));
        game.setFgPctHome(rs.getDouble("fg_pct_home"));
        game.setFg3mHome(rs.getDouble("fg3m_home"));
        game.setFg3aHome(rs.getDouble("fg3a_home"));
        game.setFg3PctHome(rs.getDouble("fg3_pct_home"));
        game.setFtmHome(rs.getDouble("ftm_home"));
        game.setFtaHome(rs.getDouble("fta_home"));
        game.setFtPctHome(rs.getDouble("ft_pct_home"));
        game.setOrebHome(rs.getDouble("oreb_home"));
        game.setDrebHome(rs.getDouble("dreb_home"));
        game.setRebHome(rs.getDouble("reb_home"));
        game.setAstHome(rs.getDouble("ast_home"));
        game.setStlHome(rs.getDouble("stl_home"));
        game.setBlkHome(rs.getDouble("blk_home"));
        game.setTovHome(rs.getDouble("tov_home"));
        game.setPfHome(rs.getDouble("pf_home"));
        game.setPtsHome(rs.getDouble("pts_home"));
        game.setPlusMinusHome(rs.getInt("plus_minus_home"));
        game.setVideoAvailableHome(rs.getInt("video_available_home"));
        game.setTeamIdAway(rs.getString("team_id_away"));
        game.setTeamAbbreviationAway(rs.getString("team_abbreviation_away"));
        game.setTeamNameAway(rs.getString("team_name_away"));
        game.setMatchupAway(rs.getString("matchup_away"));
        game.setWlAway(rs.getString("wl_away"));
        game.setFgmAway(rs.getDouble("fgm_away"));
        game.setFgaAway(rs.getDouble("fga_away"));
        game.setFgPctAway(rs.getDouble("fg_pct_away"));
        game.setFg3mAway(rs.getDouble("fg3m_away"));
        game.setFg3aAway(rs.getDouble("fg3a_away"));
        game.setFg3PctAway(rs.getDouble("fg3_pct_away"));
        game.setFtmAway(rs.getDouble("ftm_away"));
        game.setFtaAway(rs.getDouble("fta_away"));
        game.setFtPctAway(rs.getDouble("ft_pct_away"));
        game.setOrebAway(rs.getDouble("oreb_away"));
        game.setDrebAway(rs.getDouble("dreb_away"));
        game.setRebAway(rs.getDouble("reb_away"));
        game.setAstAway(rs.getDouble("ast_away"));
        game.setStlAway(rs.getDouble("stl_away"));
        game.setBlkAway(rs.getDouble("blk_away"));
        game.setTovAway(rs.getDouble("tov_away"));
        game.setPfAway(rs.getDouble("pf_away"));
        game.setPtsAway(rs.getDouble("pts_away"));
        game.setPlusMinusAway(rs.getInt("plus_minus_away"));
        game.setVideoAvailableAway(rs.getInt("video_available_away"));
        game.setSeasonType(rs.getString("season_type"));
        return game;
    }
}
