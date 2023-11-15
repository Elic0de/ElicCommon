package com.github.elic0de.eliccommon.hook;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class Hook {

    protected final JavaPlugin plugin;
    private final String name;
    private boolean enabled = false;

    protected Hook(@NotNull JavaPlugin plugin, @NotNull String name) {
        this.plugin = plugin;
        this.name = name;
    }

    protected abstract void onEnable();

    public final void enable() {
        this.onEnable();
        this.enabled = true;
    }

    public boolean isDisabled() {
        return !enabled;
    }

    @NotNull
    public String getName() {
        return name;
    }

}
