package com.github.elic0de.eliccommon.util;

import org.jetbrains.annotations.NotNull;

public final class TimeFormat {

    public static @NotNull String format(int seconds) {
        int secs;
        int mins = 0;

        if (seconds < 60) {
            secs = seconds;
        } else {
            mins = seconds / 60;
            secs = seconds % 60;
        }

        if (mins > 0) {
            return mins + ":" + (secs < 10 ? "0" + secs : secs);
        }
        else {
            if (secs < 0) {
                return "00:00";
            }
            if (secs < 10) {
                return "00:0" + secs;
            }
            return "00:" + secs;
        }
    }
}