package com.cosmic.snakegamecraft.util;

import java.util.List;
import java.util.Random;

import static com.cosmic.snakegamecraft.util.Constants.FALLOUT_MODE_THRESHOLD;
import static com.cosmic.snakegamecraft.util.Constants.MODERN_MODE_THRESHOLD;

public class Tips {

    private static final List<String> TIPS = List.of(
            "🛠 You can change the speed in the Settings.",
            "🍏 Modern Mode has special items and difficulty scaling.",
            "💡 Play Fallout Mode for another playing experience",
            "🔄 Your snake gets faster as it grows in Modern Mode.",
            "\uD83D\uDC46 Click me to show a new tip!", // Emoji for ☝️
            "💡 The Highscore scales with your speed multiplier.",
            "🎮 Use the arrow keys or WASD to control your snake.",
            "💡 Hover over the buttons to see tooltips.",
            "💡 Unlock Modern Mode by reaching at least " + MODERN_MODE_THRESHOLD + " in Classic Mode",
            "💡 Unlock Fallout Mode by reaching at least " + FALLOUT_MODE_THRESHOLD + " in Modern Mode",
            "💾 Login to save highscores and custom settings."
    );

    /**
     * Returns a tip that is not the login tip.
     * @return Tip as a String.
     */
    public static String getTipRandom() {
        Random rand = new Random();
        return TIPS.get(rand.nextInt(TIPS.size() - 1)); // Exclude last tip
    }

    /**
     * Returns the last tip, which is intended for users who are not logged in.
     * @return Login Tip as a String.
     */
    public static String getLoginTip() {
        return TIPS.getLast(); // Last tip is for not logged in
    }

}
