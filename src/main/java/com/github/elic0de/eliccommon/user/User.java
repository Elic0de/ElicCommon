package com.github.elic0de.eliccommon.user;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class User {

    private final UUID uuid;
    private final String username;

    protected User(@NotNull UUID uuid, @NotNull String username) {
        this.uuid = uuid;
        this.username = username;
    }

    @NotNull
    public UUID getUniqueId() {
        return uuid;
    }

    @NotNull
    public String getUsername() {
        return username;
    }
}
