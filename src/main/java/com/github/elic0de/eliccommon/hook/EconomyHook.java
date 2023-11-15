package com.github.elic0de.eliccommon.hook;

import org.bukkit.plugin.java.JavaPlugin;
import pakira.player.OnlinePlayer;

import java.math.BigDecimal;

public abstract class EconomyHook extends Hook {

    protected EconomyHook(JavaPlugin plugin, String name) {
        super(plugin, name);
    }

    public abstract BigDecimal getBalance(OnlinePlayer player);

    public abstract boolean hasMoney(OnlinePlayer player, BigDecimal amount);

    public abstract void takeMoney(OnlinePlayer player, BigDecimal amount);

    public abstract void giveMoney(OnlinePlayer player, BigDecimal amount);

    public abstract String formatMoney(BigDecimal amount);

}
