package G16.menik._0.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import G16.menik._0.models.Game;
import G16.menik._0.models.PlayerGameStats;
import G16.menik._0.services.GameService;
import G16.menik._0.services.GameService.TeamOption;
import G16.menik._0.services.PlayerGameStatsService;
import G16.menik._0.services.UserService;

/**
 * Controller for handling NBA game browsing and detailed game statistics.
 * Manages game list display and individual game detail views with player statistics.
 */
@Controller
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerGameStatsService playerGameStatsService;

    @Autowired
    private UserService userService;

    /**
     * Displays a list of recent NBA games.
     * Retrieves the most recent games and available teams for filtering.
     *
     * @param model the Spring model to pass data to the template
     * @return the view name "games" for rendering the games list page
     */
    @GetMapping("/games")
    public String games(Model model) {
        List<Game> games = new ArrayList<>();
        List<TeamOption> teams = new ArrayList<>();
        try {
            games = gameService.getRecentGames(1314);
            teams = gameService.getAllTeams();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        model.addAttribute("games", games);
        model.addAttribute("teams", teams);
        model.addAttribute("user", userService.getLoggedInUser());
        model.addAttribute("title", "Games");
        model.addAttribute("pageTitle", "NBA Games");
        model.addAttribute("pageSubtitle", "Browsing all game results from the 2018 - 19 NBA season.");
        return "games";
    }

    /**
     * Displays detailed statistics for a specific game including player box scores.
     * Retrieves player statistics for both home and away teams.
     * Redirects to games list if the specified game is not found.
     *
     * @param gameId the unique identifier of the game
     * @param model the Spring model to pass data to the template
     * @return the view name "game_detail" for rendering game details, or redirect to games if not found
     */
    @GetMapping("/game/{gameId}")
    public String gameDetail(@PathVariable String gameId, Model model) {
        Game game = null;
        try {
            game = gameService.getGameById(gameId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (game == null) {
            return "redirect:/games";
        }

        List<PlayerGameStats> homePlayerStats = new ArrayList<>();
        List<PlayerGameStats> awayPlayerStats = new ArrayList<>();
        try {
            System.out.println("GameId:" + gameId);
            homePlayerStats = playerGameStatsService.getStatsByGameAndTeam(gameId, game.getTeamIdHome());
            awayPlayerStats = playerGameStatsService.getStatsByGameAndTeam(gameId, game.getTeamIdAway());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("game", game);
        model.addAttribute("homePlayerStats", homePlayerStats);
        model.addAttribute("awayPlayerStats", awayPlayerStats);
        model.addAttribute("user", userService.getLoggedInUser());
        model.addAttribute("title", "Game Details");
        model.addAttribute("pageTitle", game.getTeamAbbreviationHome() + " vs " + game.getTeamAbbreviationAway());
        model.addAttribute("pageSubtitle", game.getMatchupHome());
        return "game_detail";
    }

    @GetMapping("/games/team/{team}")
    public String gamesByTeam(@PathVariable String team, Model model) {
        List<Game> games = new ArrayList<>();
        try {
            games = gameService.getGamesByTeam(team.toUpperCase());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        model.addAttribute("games", games);
        model.addAttribute("selectedTeam", team.toUpperCase());
        model.addAttribute("user", userService.getLoggedInUser());
        model.addAttribute("title", team.toUpperCase() + " Games");
        model.addAttribute("pageTitle", team.toUpperCase() + " Games");
        model.addAttribute("pageSubtitle", "All games for " + team.toUpperCase());
        return "games";
    }
}   

