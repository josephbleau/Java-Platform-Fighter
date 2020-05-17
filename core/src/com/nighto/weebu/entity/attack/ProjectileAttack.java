package com.nighto.weebu.entity.attack;

import com.badlogic.gdx.math.Rectangle;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.screen.StageScreen;

import java.util.Collections;


public class ProjectileAttack extends Attack {

    private static final float STARTUP = .3f;
    private static final float ANIMATION_TIME = .7f;
    private static final float SPEED = 7;

    private float timeToLive = 3;

    public ProjectileAttack(Character owner, float hitBoxOffsetX, float hitBoxOffsetY) {
        super(owner, Collections.singletonList(new Rectangle(hitBoxOffsetX, hitBoxOffsetY, 40, 5)));

        startupTime = STARTUP;
        attackTime = ANIMATION_TIME;
        xVel = owner.getFacingRight() ? SPEED : -SPEED;

        setHidden(true);
        getRects().addAll(getHitBoxes());
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if ((timeToLive -= delta) <= 0) {
            setActive(false);
        }
    }
}
