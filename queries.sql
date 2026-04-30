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
