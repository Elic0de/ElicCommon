package com.github.elic0de.eliccommon.hook;

import com.github.elic0de.eliccommon.plugin.AbstractPlugin;

public abstract class Hook {

    protected final AbstractPlugin plugin;
    private final String name;
    private boolean enabled = false;

    protected Hook(AbstractPlugin plugin, String name) {
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
