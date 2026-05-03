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

import G16.menik._0.models.PlayerGameStats;

@Service
public class PlayerGameStatsService {

    private final DataSource dataSource;

    @Autowired
    public PlayerGameStatsService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<PlayerGameStats> getStatsByGameId(String gameId) throws SQLException {
        //String sql = "SELECT * FROM player_game_stats WHERE GAME_ID = ? ORDER BY TEAM_ABBREVIATION, CAST(PTS AS UNSIGNED) DESC";
        String sql = """
				SELECT pgs.GAME_ID, pgs.TEAM_ID, pgs.TEAM_ABBREVIATION, pgs.TEAM_CITY,
						pgs.PLAYER_ID,
						COALESCE(p.FULL_NAME, pgs.PLAYER_NAME) AS PLAYER_NAME,
						pgs.NICKNAME, pgs.START_POSITION,
						pgs.COMMENT, pgs.MIN, pgs.FGM, pgs.FGA, pgs.FG_PCT, pgs.FG3M,
						pgs.FG3A, pgs.FG3_PCT, pgs.FTM, pgs.FTA, pgs.FT_PCT, pgs.OREB, pgs.DREB,
						pgs.REB, pgs.AST, pgs.STL, pgs.BLK, pgs.TTO, pgs.PF, pgs.PTS, pgs.PLUS_MINUS
				FROM player_game_stats pgs
				LEFT JOIN player p
					ON pgs.PLAYER_ID = p.id
				WHERE pgs.GAME_ID = ?
				ORDER BY pgs.TEAM_ABBREVIATION, CAST(COALESCE(NULLIF(pgs.PTS, ''), '0') AS UNSIGNED) DESC
				""";
        System.out.println("getStatsByGameId:" + sql);
        System.out.println("GameId:" + gameId);
        List<PlayerGameStats> statsList = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, gameId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    statsList.add(mapRow(rs));
                }
            }
        }
        return statsList;
    }

    public List<PlayerGameStats> getStatsByGameAndTeam(String gameId, String teamId) throws SQLException {
        String sql = """
                SELECT
                    pgs.GAME_ID,
                    pgs.TEAM_ID,
                    pgs.TEAM_ABBREVIATION,
                    pgs.TEAM_CITY,
                    pgs.PLAYER_ID,
                    COALESCE(p.FULL_NAME, pgs.PLAYER_NAME, 'Unknown') AS PLAYER_NAME,
                    COALESCE(pgs.NICKNAME, '') AS NICKNAME,
                    COALESCE(NULLIF(pgs.START_POSITION, ''), NULL) AS START_POSITION,
                    COALESCE(pgs.COMMENT, '') AS COMMENT,
                    COALESCE(NULLIF(pgs.MIN, ''), '0') AS MIN,
                    COALESCE(NULLIF(pgs.FGM, ''), '0') AS FGM,
                    COALESCE(NULLIF(pgs.FGA, ''), '0') AS FGA,
                    COALESCE(NULLIF(pgs.FG_PCT, ''), '0') AS FG_PCT,
                    COALESCE(NULLIF(pgs.FG3M, ''), '0') AS FG3M,
                    COALESCE(NULLIF(pgs.FG3A, ''), '0') AS FG3A,
                    COALESCE(NULLIF(pgs.FG3_PCT, ''), '0') AS FG3_PCT,
                    COALESCE(NULLIF(pgs.FTM, ''), '0') AS FTM,
                    COALESCE(NULLIF(pgs.FTA, ''), '0') AS FTA,
                    COALESCE(NULLIF(pgs.FT_PCT, ''), '0') AS FT_PCT,
                    COALESCE(NULLIF(pgs.OREB, ''), '0') AS OREB,
                    COALESCE(NULLIF(pgs.DREB, ''), '0') AS DREB,
                    COALESCE(NULLIF(pgs.REB, ''), '0') AS REB,
                    COALESCE(NULLIF(pgs.AST, ''), '0') AS AST,
                    COALESCE(NULLIF(pgs.STL, ''), '0') AS STL,
                    COALESCE(NULLIF(pgs.BLK, ''), '0') AS BLK,
                    COALESCE(NULLIF(pgs.TTO, ''), '0') AS TTO,
                    COALESCE(NULLIF(pgs.PF, ''), '0') AS PF,
                    COALESCE(NULLIF(pgs.PTS, ''), '0') AS PTS,
                    COALESCE(NULLIF(pgs.PLUS_MINUS, ''), '0') AS PLUS_MINUS,

                    -- Player bio from common_player_info
                    COALESCE(cpi.jersey, '') AS JERSEY,
                    COALESCE(cpi.position, '') AS POSITION,
                    COALESCE(cpi.height, '') AS HEIGHT,
                    COALESCE(cpi.weight, '') AS WEIGHT,

                    -- Draft info via draft_history
                    COALESCE(CAST(dh.round_number AS CHAR), '') AS DRAFT_ROUND,
                    COALESCE(CAST(dh.overall_pick AS CHAR), '') AS DRAFT_NUMBER,

                    -- Game context
                    COALESCE(CAST(g.game_date AS CHAR), '') AS GAME_DATE,

                    -- Season averages subquery
                    COALESCE(season_avg.AVG_PTS, 0) AS SEASON_AVG_PTS,
                    COALESCE(season_avg.AVG_REB, 0) AS SEASON_AVG_REB,
                    COALESCE(season_avg.AVG_AST, 0) AS SEASON_AVG_AST,
                    COALESCE(season_avg.GAMES_PLAYED, 0) AS GAMES_PLAYED,

                    -- Game Score using Hollinger player efficiency formula:
                    -- PTS + 0.4*FGM - 0.7*FGA - 0.4*(FTA-FTM) + 0.7*OREB + 0.3*DREB + STL + 0.7*AST + 0.7*BLK - 0.4*PF - TTO
                    ROUND(
                        CAST(COALESCE(NULLIF(pgs.PTS,''),'0') AS DECIMAL(10,2))
                        + 0.4 * CAST(COALESCE(NULLIF(pgs.FGM,''),'0') AS DECIMAL(10,2))
                        - 0.7 * CAST(COALESCE(NULLIF(pgs.FGA,''),'0') AS DECIMAL(10,2))
                        - 0.4 * (CAST(COALESCE(NULLIF(pgs.FTA,''),'0') AS DECIMAL(10,2))
                                 - CAST(COALESCE(NULLIF(pgs.FTM,''),'0') AS DECIMAL(10,2)))
                        + 0.7 * CAST(COALESCE(NULLIF(pgs.OREB,''),'0') AS DECIMAL(10,2))
                        + 0.3 * CAST(COALESCE(NULLIF(pgs.DREB,''),'0') AS DECIMAL(10,2))
                        + CAST(COALESCE(NULLIF(pgs.STL,''),'0') AS DECIMAL(10,2))
                        + 0.7 * CAST(COALESCE(NULLIF(pgs.AST,''),'0') AS DECIMAL(10,2))
                        + 0.7 * CAST(COALESCE(NULLIF(pgs.BLK,''),'0') AS DECIMAL(10,2))
                        - 0.4 * CAST(COALESCE(NULLIF(pgs.PF,''),'0') AS DECIMAL(10,2))
                        - CAST(COALESCE(NULLIF(pgs.TTO,''),'0') AS DECIMAL(10,2))
                    , 1) AS GAME_SCORE,

                    -- Ranking within team by points scored in this game
                    RANK() OVER (
                        PARTITION BY pgs.TEAM_ID
                        ORDER BY CAST(COALESCE(NULLIF(pgs.PTS,''),'0') AS UNSIGNED) DESC
                    ) AS TEAM_PTS_RANK

                FROM player_game_stats pgs

                LEFT JOIN player p
                    ON pgs.PLAYER_ID = p.id

                LEFT JOIN game g
                    ON pgs.GAME_ID = g.game_id

                LEFT JOIN common_player_info cpi
                    ON pgs.PLAYER_ID = cpi.person_id

                LEFT JOIN draft_history dh
                    ON pgs.PLAYER_ID = dh.person_id

                -- Season averages: aggregates all the games for the player within the same season
                LEFT JOIN (
                    SELECT
                        sa_pgs.PLAYER_ID,
                        ROUND(AVG(CAST(COALESCE(NULLIF(sa_pgs.PTS,''),'0') AS DECIMAL(10,2))), 1) AS AVG_PTS,
                        ROUND(AVG(CAST(COALESCE(NULLIF(sa_pgs.REB,''),'0') AS DECIMAL(10,2))), 1) AS AVG_REB,
                        ROUND(AVG(CAST(COALESCE(NULLIF(sa_pgs.AST,''),'0') AS DECIMAL(10,2))), 1) AS AVG_AST,
                        COUNT(DISTINCT sa_pgs.GAME_ID) AS GAMES_PLAYED
                    FROM player_game_stats sa_pgs
                    INNER JOIN game sa_g
                        ON sa_pgs.GAME_ID = sa_g.game_id
                    WHERE sa_g.season_id = (
                        SELECT g2.season_id FROM game g2 WHERE g2.game_id = ?
                    )
                    GROUP BY sa_pgs.PLAYER_ID
                ) season_avg
                    ON pgs.PLAYER_ID = season_avg.PLAYER_ID

                WHERE pgs.GAME_ID = ? AND pgs.TEAM_ID = ?

                ORDER BY
                    -- Starters first, then bench
                    CASE WHEN COALESCE(NULLIF(pgs.START_POSITION, ''), '') != '' THEN 0 ELSE 1 END,
                    -- Within each group, highest points first
                    CAST(COALESCE(NULLIF(pgs.PTS, ''), '0') AS UNSIGNED) DESC,
                    -- Tiebreak by game score
                    ROUND(
                        CAST(COALESCE(NULLIF(pgs.PTS,''),'0') AS DECIMAL(10,2))
                        + 0.4 * CAST(COALESCE(NULLIF(pgs.FGM,''),'0') AS DECIMAL(10,2))
                        - 0.7 * CAST(COALESCE(NULLIF(pgs.FGA,''),'0') AS DECIMAL(10,2))
                    , 1) DESC
                """;
        System.out.println("getStatsByGameAndTeam:" + sql);
        System.out.println("GameId:" + gameId + " TeamId:" + teamId);
        List<PlayerGameStats> statsList = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, gameId);
            pstmt.setString(2, gameId);
            pstmt.setString(3, teamId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    statsList.add(mapRow(rs));
                }
            }
        }
        return statsList;
    }

    private PlayerGameStats mapRow(ResultSet rs) throws SQLException {
        PlayerGameStats stats = new PlayerGameStats();
        stats.setGameId(rs.getString("GAME_ID"));
        stats.setTeamId(rs.getString("TEAM_ID"));
        stats.setTeamAbbreviation(rs.getString("TEAM_ABBREVIATION"));
        stats.setTeamCity(rs.getString("TEAM_CITY"));
        stats.setPlayerId(rs.getString("PLAYER_ID"));
        stats.setPlayerName(rs.getString("PLAYER_NAME"));
        stats.setNickname(rs.getString("NICKNAME"));
        stats.setStartPosition(rs.getString("START_POSITION"));
        stats.setComment(rs.getString("COMMENT"));
        stats.setMin(rs.getString("MIN"));
        stats.setFgm(rs.getString("FGM"));
        stats.setFga(rs.getString("FGA"));
        stats.setFgPct(rs.getString("FG_PCT"));
        stats.setFg3m(rs.getString("FG3M"));
        stats.setFg3a(rs.getString("FG3A"));
        stats.setFg3Pct(rs.getString("FG3_PCT"));
        stats.setFtm(rs.getString("FTM"));
        stats.setFta(rs.getString("FTA"));
        stats.setFtPct(rs.getString("FT_PCT"));
        stats.setOreb(rs.getString("OREB"));
        stats.setDreb(rs.getString("DREB"));
        stats.setReb(rs.getString("REB"));
        stats.setAst(rs.getString("AST"));
        stats.setStl(rs.getString("STL"));
        stats.setBlk(rs.getString("BLK"));
        stats.setTto(rs.getString("TTO"));
        stats.setPf(rs.getString("PF"));
        stats.setPts(rs.getString("PTS"));
        stats.setPlusMinus(rs.getString("PLUS_MINUS"));

        //Additional player info - using try/catch to handle missing columns gracefully
        try { stats.setJersey(rs.getString("JERSEY")); } catch (SQLException ignored) {}
        try { stats.setPosition(rs.getString("POSITION")); } catch (SQLException ignored) {}
        try { stats.setHeight(rs.getString("HEIGHT")); } catch (SQLException ignored) {}
        try { stats.setWeight(rs.getString("WEIGHT")); } catch (SQLException ignored) {}
        try { stats.setDraftYear(rs.getString("DRAFT_YEAR")); } catch (SQLException ignored) {}
        try { stats.setDraftRound(rs.getString("DRAFT_ROUND")); } catch (SQLException ignored) {}
        try { stats.setDraftNumber(rs.getString("DRAFT_NUMBER")); } catch (SQLException ignored) {}
        try { stats.setGameDate(rs.getString("GAME_DATE")); } catch (SQLException ignored) {}
        try { stats.setSeasonAvgPts(rs.getString("SEASON_AVG_PTS")); } catch (SQLException ignored) {}
        try { stats.setSeasonAvgReb(rs.getString("SEASON_AVG_REB")); } catch (SQLException ignored) {}
        try { stats.setSeasonAvgAst(rs.getString("SEASON_AVG_AST")); } catch (SQLException ignored) {}
        try { stats.setGamesPlayed(rs.getInt("GAMES_PLAYED")); } catch (SQLException ignored) {}
        try { stats.setGameScore(rs.getDouble("GAME_SCORE")); } catch (SQLException ignored) {}
        try { stats.setTeamPtsRank(rs.getInt("TEAM_PTS_RANK")); } catch (SQLException ignored) {}

        return stats;
    }
}
