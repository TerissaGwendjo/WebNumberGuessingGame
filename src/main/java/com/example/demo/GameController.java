package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.Random;

/**
 * Controller for the Number Guessing Game Web application.
 * Handles game state, attempts, difficulty, restart, and session attributes.
 */
@Controller
@SessionAttributes({GameController.NUMBER_ATTR, GameController.ATTEMPTS_ATTR, GameController.MAXRANGE_ATTR})
public class GameController {

    // ----- Constants -----
    public static final String NUMBER_ATTR = "numberToGuess";
    public static final String ATTEMPTS_ATTR = "attempts";
    public static final String MAXRANGE_ATTR = "maxRange";
    public static final String MESSAGE_ATTR = "message";
    private static final String INDEX_VIEW = "index";

    // ----- Random instance -----
    private final Random random = new Random(); // Reuse single Random instance

    // ----- Model attribute initializers -----
    @SuppressWarnings("unused")
    @ModelAttribute(NUMBER_ATTR)
    public Integer initNumber(@ModelAttribute(value = MAXRANGE_ATTR, binding = false) Integer maxRange) {
        if (maxRange == null) maxRange = 100;
        return random.nextInt(maxRange) + 1;
    }

    @SuppressWarnings("unused")
    @ModelAttribute(ATTEMPTS_ATTR)
    public Integer initAttempts() {
        return 0;
    }

    @SuppressWarnings("unused")
    @ModelAttribute(MAXRANGE_ATTR)
    public Integer initRange() {
        return 100;
    }

    // ----- GET / Home page -----
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute(MESSAGE_ATTR, "Start guessing!");
        return INDEX_VIEW;
    }

    // ----- POST /guess - Handle user guesses -----
    @PostMapping("/guess")
    public String guess(@RequestParam int guess,
                        @ModelAttribute(NUMBER_ATTR) int numberToGuess,
                        @ModelAttribute(ATTEMPTS_ATTR) int attempts,
                        Model model,
                        SessionStatus status) {

        attempts++;
        model.addAttribute(ATTEMPTS_ATTR, attempts);

        if (guess < numberToGuess) {
            model.addAttribute(MESSAGE_ATTR, "Too low!");
        } else if (guess > numberToGuess) {
            model.addAttribute(MESSAGE_ATTR, "Too high!");
        } else {
            // User guessed correctly
            model.addAttribute(MESSAGE_ATTR, "🏆 You won in " + attempts + " tries! Starting new game...");

            // Clear session attributes to reset game
            status.setComplete();

            // Reinitialize number and attempts for new game
            model.addAttribute(NUMBER_ATTR, random.nextInt(100) + 1);
            model.addAttribute(ATTEMPTS_ATTR, 0);
        }

        return INDEX_VIEW;
    }

    // ----- POST /restart - Restart the game -----
    @PostMapping("/restart")
    public String restart(@ModelAttribute(MAXRANGE_ATTR) int maxRange,
                          Model model,
                          SessionStatus status) {

        // Clear previous session attributes
        status.setComplete();

        // Initialize new game
        model.addAttribute(NUMBER_ATTR, random.nextInt(maxRange) + 1);
        model.addAttribute(ATTEMPTS_ATTR, 0);
        model.addAttribute(MESSAGE_ATTR, "🔄 Game restarted!");

        return INDEX_VIEW;
    }

    // ----- POST /difficulty - Set difficulty level -----
    @PostMapping("/difficulty")
    public String setDifficulty(@RequestParam int range,
                                Model model,
                                SessionStatus status) {

        // Clear previous session attributes
        status.setComplete();

        // Set new difficulty and initialize game
        model.addAttribute(MAXRANGE_ATTR, range);
        model.addAttribute(NUMBER_ATTR, random.nextInt(range) + 1);
        model.addAttribute(ATTEMPTS_ATTR, 0);
        model.addAttribute(MESSAGE_ATTR, "🎚 Difficulty set to 1 - " + range);

        return INDEX_VIEW;
    }

}