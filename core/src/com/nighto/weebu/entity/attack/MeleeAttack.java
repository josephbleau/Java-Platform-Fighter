package com.nighto.weebu.entity.attack;


import com.badlogic.gdx.math.Rectangle;
import com.nighto.weebu.entity.character.Character;

import java.util.Collections;

public class MeleeAttack extends Attack {
    public MeleeAttack(Character owner, float hitBoxOffsetX, float hitBoxOffsetY, float knockbackInduced, float xImpulse, float yImpulse) {
        super(owner, Collections.singletonList(new Rectangle(hitBoxOffsetX, hitBoxOffsetY, 20, 20)));

        startupTime = 1f/60f;
        attackTime = 10f/60f;

        this.knockbackInduced = knockbackInduced;
        this.xImpulse = xImpulse;
        this.yImpulse = yImpulse;
        this.knockbackModifierIncrease = 1;

        setTag("Melee Attack");
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if ((attackTime <= 0)) {
            setActive(false);
        }
    }
}
