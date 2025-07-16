package com.cosmic.snakegamecraft.util;

import java.util.List;

public class Tips {

    private static final List<String> TIPS = List.of(
            "🛠 You can change the speed in the Settings.",
            "🍏 Modern Mode has special items and difficulty scaling.",
            "💡 Play Crazy Mode for AI enemies and weapons!",
            "🔄 Your snake gets faster as it grows in Modern Mode.",
            "\uD83D\uDC46 Click me to show a new tip!", // Emoji for ☝️
            "💡 The Highscore scales with your speed multiplier.",
            "🎮 Use the arrow keys or WASD to control your snake.",
            "💡 Hover over the buttons to see tooltips.",
            "💾 Login to save highscores and custom settings."
    );

    /**
     * Returns a tip that is not the login tip.
     * @return Tip as a String.
     */
    public static String getTipRandom() {
        return TIPS.get((int) (Math.random() * (TIPS.size() - 1)));
    }

    /**
     * Returns the last tip, which is intended for users who are not logged in.
     * @return Login Tip as a String.
     */
    public static String getLoginTip() {
        return TIPS.getLast(); // Last tip is for not logged in
    }

}
