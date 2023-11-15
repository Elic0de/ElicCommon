package com.github.elic0de.eliccommon.player;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class OnlinePlayer {

    private final UUID uuid;
    private final String username;

    protected OnlinePlayer(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public final void sendMessage(String message) {
        getPlayer().sendMessage(message);
    }

    public final void sendMessage(Component message) {
       getPlayer().sendMessage(message);
    }

    public final void sendActionBar(String message) {
        getPlayer().sendActionBar(Component.text(ChatColor.translateAlternateColorCodes('&', message)));
    }

    public final void playSound(Sound sound) {
        getPlayer().playSound(getPlayer().getLocation(), sound, 1F, 1F);
    }

    public final void playSound(Sound sound, float volume, float pitch) {
        getPlayer().playSound(getPlayer().getLocation(), sound, volume, pitch);
    }

    public final void sendTitle(String title, String subTitle) {
        sendTitle(title, subTitle, 20, 60, 20);
    }

    public final void sendTitle(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        final String coloredTitle = ChatColor.translateAlternateColorCodes('&', title);
        final String coloredSubTitle = ChatColor.translateAlternateColorCodes('&', subTitle);
        getPlayer().sendTitle(coloredTitle, coloredSubTitle, fadeIn, stay, fadeOut);
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public abstract Player getPlayer();

}
