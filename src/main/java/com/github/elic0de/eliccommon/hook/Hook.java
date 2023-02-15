package com.github.elic0de.eliccommon.hook;

import org.bukkit.plugin.Plugin;

public abstract class Hook {

    protected final Plugin plugin;
    private final String name;
    private boolean enabled = false;

    protected Hook(Plugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    protected abstract void onEnable();

    public final void enable() {
        this.onEnable();
        this.enabled = true;
    }

    public boolean isNotEnabled() {
        return !enabled;
    }

    public String getName() {
        return name;
    }

}
