package com.nighto.weebu.entity.attack;

import com.nighto.weebu.entity.Entity;

public class Attack extends Entity {
    protected float startupTime;
    protected float attackTime;

    protected Attack() {
        super();
        setHidden(true);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (startupTime > 0) {
            startupTime -= delta;
            if (startupTime <= 0) {
                setHidden(false);
                attackTime -= startupTime;
                startupTime = 0;
            }
        }

        if (startupTime == 0) {
            attackTime -= delta;
        }
    }

    public boolean isPlaying() {
        return startupTime > 0 || attackTime > 0;
    }
}
