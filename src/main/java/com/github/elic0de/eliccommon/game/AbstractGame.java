package com.github.elic0de.eliccommon.game;

import com.github.elic0de.eliccommon.game.phase.Phase;
import com.github.elic0de.eliccommon.plugin.AbstractPlugin;
import com.github.elic0de.eliccommon.user.OnlineUser;
import de.themoep.minedown.MineDown;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class AbstractGame {

    private final @Nullable Supplier<Integer> queryPhase = null;
    private int currentPhase = 0;
    private BukkitTask startTask;
    private BukkitTask endTask;
    @Getter
    protected final AtomicLong currentStartTicks = new AtomicLong();
    @Getter
    protected final AtomicLong currentEndTicks = new AtomicLong();
    protected final Set<OnlineUser> players = new HashSet<>();

    public void join(@NotNull OnlineUser player) {
        players.add(player);
        getPhase().join(player);
    }

    public void leave(@NotNull OnlineUser player) {
        players.remove(player);
        getPhase().leave(player);
    }

    public void broadcast(MineDown message) {
        players.forEach(player -> player.sendMessage(message));
    }

    public void title(String title, String subTitle) {
        players.forEach(player -> player.sendTitle(title, subTitle));
    }

    public void sound(Sound sound) {
        players.forEach(player -> player.playSound(sound));
    }

    public void reset() {
        currentStartTicks.set(0);
        currentEndTicks.set(0);
        players.clear();
    }

    public void updatePhase() {
        getPhase().update();
    }

    public void nextPhase() {
        queryCurrentPhase();
        nextPhaseTask();
    }

    private void nextPhaseTask() {
        final long PERIOD = 20;
        currentStartTicks.set(0);
        currentEndTicks.set(0);

        if (startTask != null) startTask.cancel();
        if (endTask != null) endTask.cancel();
        // todo:
        startTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (currentStartTicks.incrementAndGet() > getPhase().getStartDelay()) {
                    getPhase().start();
                    cancel();
                }
            }
        }.runTaskTimer(AbstractPlugin.getInstance(), 0, PERIOD);

        endTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (currentEndTicks.incrementAndGet() > getPhase().getStartDelay() + getPhase().getEndDelay()) {
                    getPhase().end();
                    cancel();
                }
            }
        }.runTaskTimer(AbstractPlugin.getInstance(), 0, PERIOD);
        currentPhase = getPhases().length > currentPhase + 1 ? currentPhase + 1 : 0;
    }

    private void queryCurrentPhase() {
        if (queryPhase != null) {
            currentPhase = queryPhase.get();
        }
    }

    public Phase getPhase() {
        queryCurrentPhase();
        return getPhases()[currentPhase];
    }

    public <T extends OnlineUser> List<T> getPlayers(@NotNull Class<@NotNull T> type) {
        return players.stream().map(type::cast).collect(Collectors.toList());
    }

    @NotNull
    public abstract Phase[] getPhases();
}
