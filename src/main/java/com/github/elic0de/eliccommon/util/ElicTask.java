package com.github.elic0de.eliccommon.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class ElicTask {

    public static GameTask runTaskTimer(JavaPlugin plugin, Consumer<GameTask> task, long period, long time) {
        return new GameTask(plugin, task, period, time);
    }

    public static GameTask runTaskTimer(JavaPlugin plugin, Consumer<GameTask> task, long time) {
        return new GameTask(plugin, task, 20L, time);
    }

    public static class GameTask {

        @Getter
        private final BukkitTask task;
        @Getter
        private final long endTime;
        private final AtomicLong ticks = new AtomicLong();
        private final GameTask instance;

        @Setter
        private Consumer<GameTask> endExecute;
        public GameTask(JavaPlugin plugin, Consumer<GameTask> consumer, long period, long endTime) {
            this.instance = this;
            this.endTime = endTime;
            task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (ticks.incrementAndGet() >= endTime) {
                        if (endExecute != null) endExecute.accept(instance);
                        cancel();
                    }
                    consumer.accept(instance);
                }
            }.runTaskTimer(plugin, 0, period);
        }

        public Long getCurrentTicks() {
            return ticks.get();
        }
    }
}
