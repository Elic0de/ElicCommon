package com.github.elic0de.eliccommon.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public interface BukkitTaskRunner {

    default int runAsync(@NotNull Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), runnable).getTaskId();
    }

    default int runSync(@NotNull Runnable runnable) {
        return Bukkit.getScheduler().runTask(getPlugin(), runnable).getTaskId();
    }

    default int runTimedAsync(@NotNull Runnable runnable, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(getPlugin(), runnable, delay, period).getTaskId();
    }

    default void cancelTask(int taskId) {
        Bukkit.getScheduler().cancelTask(taskId);
    }

    @NotNull
    Plugin getPlugin();

}
