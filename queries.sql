-- Purpose: Creates the users table if the DDL has not been run yet, so login and signup can store account records.
-- URL path: Database setup before using the web app; supports POST /signup and POST /login
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL
);

-- Purpose: Looks up a user by username so the app can verify the password hash during login and also retrieve stored account information in the user service.
-- URL path: POST /login, and reused internally by UserService.findByUsername
SELECT id, username, password_hash, full_name
FROM users
WHERE username = ?;

-- Purpose: Creates a new user account during signup by inserting the username, hashed password, and full name.
-- URL path: POST /signup
INSERT INTO users (username, password_hash, full_name)
VALUES (?, ?, ?);

-- Purpose: Retrieves draft picks for the Draft Explorer page, with optional filtering by draft year and by player or team text.
-- URL path: GET /draft
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
ORDER BY CAST(dh.season AS UNSIGNED) DESC, dh.overall_pick ASC;

-- Purpose: Ranks teams for one regular season using wins, losses, win percentage, scoring averages, and point differential.
-- URL path: GET /rankings
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
ORDER BY rank_number;

-- Purpose: Builds team-level feature vectors for the Matchup Predictor by aggregating each team's wins, losses, scoring averages, and point differential from the 2018-19 regular season.
-- URL path: GET /predictor
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
ORDER BY team_name;

-- Purpose: Retrieves historical home/away game outcomes used as labels for the Matchup Predictor's pure Java logistic model.
-- URL path: GET /predictor
SELECT team_abbreviation_home AS home_abbreviation,
       team_abbreviation_away AS away_abbreviation,
       CASE WHEN wl_home = 'W' THEN 1 ELSE 0 END AS home_win
FROM game
WHERE season_id = ? AND season_type = ?;

-- Purpose: This query will get the game details as well as player statistics for each game for regular season , playoffs, as well as the all star game
-- URL path: GET /games
SELECT
	pgs.GAME_ID, pgs.TEAM_ID,
	pgs.TEAM_ABBREVIATION, pgs.TEAM_CITY,
	pgs.PLAYER_ID, COALESCE(p.FULL_NAME, pgs.PLAYER_NAME, 'Unknown') AS PLAYER_NAME,
	COALESCE(pgs.NICKNAME, '') AS NICKNAME, COALESCE(NULLIF(pgs.START_POSITION, ''), NULL) AS START_POSITION,
	COALESCE(pgs.COMMENT, '') AS COMMENT, COALESCE(NULLIF(pgs.MIN, ''), '0') AS MIN,
	COALESCE(NULLIF(pgs.FGM, ''), '0') AS FGM, COALESCE(NULLIF(pgs.FGA, ''), '0') AS FGA,
	COALESCE(NULLIF(pgs.FG_PCT, ''), '0') AS FG_PCT, COALESCE(NULLIF(pgs.FG3M, ''), '0') AS FG3M,
	COALESCE(NULLIF(pgs.FG3A, ''), '0') AS FG3A, COALESCE(NULLIF(pgs.FG3_PCT, ''), '0') AS FG3_PCT,
	COALESCE(NULLIF(pgs.FTM, ''), '0') AS FTM, COALESCE(NULLIF(pgs.FTA, ''), '0') AS FTA,
	COALESCE(NULLIF(pgs.FT_PCT, ''), '0') AS FT_PCT, COALESCE(NULLIF(pgs.OREB, ''), '0') AS OREB,
	COALESCE(NULLIF(pgs.DREB, ''), '0') AS DREB, COALESCE(NULLIF(pgs.REB, ''), '0') AS REB,
	COALESCE(NULLIF(pgs.AST, ''), '0') AS AST, COALESCE(NULLIF(pgs.STL, ''), '0') AS STL,
	COALESCE(NULLIF(pgs.BLK, ''), '0') AS BLK, COALESCE(NULLIF(pgs.TTO, ''), '0') AS TTO,
	COALESCE(NULLIF(pgs.PF, ''), '0') AS PF, COALESCE(NULLIF(pgs.PTS, ''), '0') AS PTS,
	COALESCE(NULLIF(pgs.PLUS_MINUS, ''), '0') AS PLUS_MINUS,
	-- Player bio from common_player_info
	COALESCE(cpi.jersey, '') AS JERSEY, COALESCE(cpi.position, '') AS POSITION,
	COALESCE(cpi.height, '') AS HEIGHT, COALESCE(cpi.weight, '') AS WEIGHT,
	-- Draft info via draft_history
	COALESCE(CAST(dh.round_number AS CHAR), '') AS DRAFT_ROUND, COALESCE(CAST(dh.overall_pick AS CHAR), '') AS DRAFT_NUMBER,
	-- Game context
	COALESCE(CAST(g.game_date AS CHAR), '') AS GAME_DATE,
	-- Season averages subquery
	COALESCE(season_avg.AVG_PTS, 0) AS SEASON_AVG_PTS, COALESCE(season_avg.AVG_REB, 0) AS SEASON_AVG_REB,
	COALESCE(season_avg.AVG_AST, 0) AS SEASON_AVG_AST, COALESCE(season_avg.GAMES_PLAYED, 0) AS GAMES_PLAYED,
	-- Game Score (using Hollinger formula):
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
	-- Rank within team by points scored in this game
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
-- Season averages: aggregates all games for this player in the same season
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
    
SELECT * FROM game ORDER BY game_date DESC LIMIT 100
    
SELECT * FROM game WHERE team_abbreviation_home = ? OR team_abbreviation_away = ? ORDER BY game_date DESC;
	
SELECT * FROM game WHERE game_date BETWEEN ? AND ? ORDER BY game_date DESC;
	
SELECT DISTINCT team_abbreviation_home FROM game ORDER BY team_abbreviation_home;
