package com.github.elic0de.eliccommon.user;

import de.themoep.minedown.MineDown;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class OnlineUser extends User {

    protected OnlineUser(@NotNull UUID uuid, @NotNull String username) {
        super(uuid, username);
    }

    public final void sendMessage(@NotNull MineDown mineDown) {
        getPlayer().spigot().sendMessage(mineDown.toComponent());
    }

    public final void sendActionBar(@NotNull MineDown mineDown) {
        getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, mineDown.toComponent());
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



    @NotNull
    public abstract Player getPlayer();
}
