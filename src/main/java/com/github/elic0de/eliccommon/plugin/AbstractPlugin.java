package com.github.elic0de.eliccommon.plugin;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractPlugin extends JavaPlugin {

    private static AbstractPlugin instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    protected void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) getServer().getPluginManager().registerEvents(listener, this);
    }

    public static AbstractPlugin getInstance() {
        return instance;
    }
}
