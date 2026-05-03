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

-- Purpose: Retrieves available regular season IDs for the Team Rankings page season selector.
-- URL path: GET /rankings
SELECT DISTINCT season_id
FROM game
WHERE season_type = ?
ORDER BY season_id DESC;

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
ORDER BY rank_number;
