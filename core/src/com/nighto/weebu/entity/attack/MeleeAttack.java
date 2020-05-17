package com.nighto.weebu.entity.attack;


import com.badlogic.gdx.math.Rectangle;
import com.nighto.weebu.entity.character.Character;

import java.util.Collections;

public class MeleeAttack extends Attack {
    public MeleeAttack(Character owner, float hitBoxOffsetX, float hitBoxOffsetY) {
        super(owner, Collections.singletonList(new Rectangle(hitBoxOffsetX, hitBoxOffsetY, 20, 20)));

        startupTime = 1f/60f;
        attackTime = 3f/60f;
        knockbackInduced = 15f/60f;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if ((attackTime <= 0)) {
            setActive(false);
        }
    }
}
