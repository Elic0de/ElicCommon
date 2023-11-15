package com.github.elic0de.eliccommon.game;

import com.github.elic0de.eliccommon.player.OnlinePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Handler implements Listener {

    private final Game game;

    protected Handler(Game game) {
        this.game = game;
    }

    public final void register(final JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public final void unregister() {
        HandlerList.unregisterAll(this);
    }

    public void join(OnlinePlayer player) {

    }

    public void leave(OnlinePlayer player) {

    }

    public void start() {

    }

    public void end() {

    }

    public void reset() {

    }

    public final void nextHandler(final JavaPlugin plugin) {
        game.nextHandler(plugin);
    }

    public final void nextHandlerAndStart(JavaPlugin plugin) {
        game.nextHandlerAndStart(plugin);
    }

    public void title(String title, String subTitle) {
        getGame().title(title, subTitle);
    }

    public final void broadcast(String msg) {
        game.broadcast(Component.text(ChatColor.translateAlternateColorCodes('&', msg)));
    }

    public final void broadcast(Collection<String> msgs) {
        for (String m : msgs) {
            game.broadcast(Component.text(ChatColor.translateAlternateColorCodes('&', m)));
        }
    }

    public final void broadcast(Component msg) {
        game.broadcast(msg);
    }

    public final void sound(Sound sound) {
        game.sound(sound);
    }

    public Game getGame() {
        return game;
    }


    public <T extends OnlinePlayer> List<T> getPlayers(Class<T> type) {
        return getGame().getPlayers().stream().map(type::cast).collect(Collectors.toList());
    }

    public Set<OnlinePlayer> getPlayers() {
        return getGame().getPlayers();
    }

}
