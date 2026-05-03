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
				WHERE pgs.GAME_ID = ? AND pgs.TEAM_ID = ?
				ORDER BY CAST(COALESCE(NULLIF(pgs.PTS, ''), '0') AS UNSIGNED) DESC
				""";
        System.out.println("getStatsByGameAndTeam:" + sql);
        System.out.println("GameId:" + gameId + " TeamId:" + teamId);
        List<PlayerGameStats> statsList = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, gameId);
            pstmt.setString(2, teamId);

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
        //stats.setStartPosition(rs.getString("START_POSITION"));
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
        return stats;
    }
}
