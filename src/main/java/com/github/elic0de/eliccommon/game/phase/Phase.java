package com.github.elic0de.eliccommon.game.phase;

import com.github.elic0de.eliccommon.user.OnlineUser;
import lombok.Getter;

public abstract class Phase {

    @Getter
    private final long startDelay;
    @Getter
    private final long endDelay;

    public Phase(long startDelay, long endDelay) {
        this.startDelay = startDelay;
        this.endDelay = endDelay;
    }

    public abstract void start();

    public abstract void update();

    public abstract void end();

    public abstract void join(OnlineUser player);

    public abstract void leave(OnlineUser player);

}
