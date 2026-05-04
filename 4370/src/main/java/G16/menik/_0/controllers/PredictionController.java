package G16.menik._0.controllers;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import G16.menik._0.services.PredictionService;
import G16.menik._0.services.PredictionService.PredictionResult;
import G16.menik._0.services.PredictionService.TeamOption;

/**
 * Controller for handling NBA game outcome predictions.
 * Provides interface for users to select teams and view predicted match outcomes.
 */
@Controller
public class PredictionController {

    private final PredictionService predictionService;

    /**
     * Constructs PredictionController with the specified PredictionService.
     *
     * @param predictionService the service for generating game predictions
     */
    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    /**
     * Displays the matchup predictor page and generates predictions.
     * Allows users to select home and away teams and displays predicted outcome.
     * Defaults to Milwaukee Bucks vs Toronto Raptors if no teams are selected.
     *
     * @param homeTeam optional home team abbreviation (defaults to "MIL")
     * @param awayTeam optional away team abbreviation (defaults to "TOR")
     * @param model the Spring model to pass data to the template
     * @return the view name "predictor" for rendering the prediction page
     * @throws RuntimeException if prediction data cannot be loaded
     */
    @GetMapping("/predictor")
    public String predictor(@RequestParam(value = "homeTeam", defaultValue = "") String homeTeam,
                            @RequestParam(value = "awayTeam", defaultValue = "") String awayTeam,
                            Model model) {
        try {
            List<TeamOption> teams = predictionService.getTeams();
            String selectedHome = chooseTeam(homeTeam, teams, "MIL", 0);
            String selectedAway = chooseTeam(awayTeam, teams, "TOR", 1);
            if (selectedHome.equals(selectedAway)) {
                selectedAway = teams.stream()
                        .map(TeamOption::abbreviation)
                        .filter(abbreviation -> !abbreviation.equals(selectedHome))
                        .findFirst()
                        .orElse(selectedAway);
            }

            PredictionResult prediction = teams.size() >= 2
                    ? predictionService.predict(selectedHome, selectedAway)
                    : null;

            model.addAttribute("title", "Matchup Predictor");
            model.addAttribute("pageTitle", "Matchup Predictor");
            model.addAttribute("pageSubtitle", "Estimates NBA matchup outcomes.");
            model.addAttribute("homeTeams", selectableTeams(teams, selectedHome));
            model.addAttribute("awayTeams", selectableTeams(teams, selectedAway));
            model.addAttribute("prediction", prediction);
            model.addAttribute("hasPrediction", prediction != null);
            return "predictor";
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load prediction data", e);
        }
    }

    private String chooseTeam(String requestedTeam, List<TeamOption> teams, String preferredTeam, int fallbackIndex) {
        String normalized = requestedTeam == null ? "" : requestedTeam.trim().toUpperCase();
        if (containsTeam(teams, normalized)) {
            return normalized;
        }
        if (containsTeam(teams, preferredTeam)) {
            return preferredTeam;
        }
        if (teams.isEmpty()) {
            return "";
        }
        int index = Math.min(fallbackIndex, teams.size() - 1);
        return teams.get(index).abbreviation();
    }

    private boolean containsTeam(List<TeamOption> teams, String abbreviation) {
        return teams.stream().anyMatch(team -> team.abbreviation().equals(abbreviation));
    }

    private List<SelectableTeamOption> selectableTeams(List<TeamOption> teams, String selectedTeam) {
        return teams.stream()
                .map(team -> new SelectableTeamOption(
                        team.abbreviation(),
                        team.displayName(),
                        team.abbreviation().equals(selectedTeam)))
                .toList();
    }

    public record SelectableTeamOption(String abbreviation, String displayName, boolean selected) {
    }
}
