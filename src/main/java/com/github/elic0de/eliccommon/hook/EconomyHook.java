package com.github.elic0de.eliccommon.hook;

import com.github.elic0de.eliccommon.player.OnlinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

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
