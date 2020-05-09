package com.josephbleau.game.entity.attack;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;


public class Laser extends Attack {

    private static final float STARTUP = .3f;

    private static final float ANIMATION_TIME = .7f;

    private static final float SPEED = 7;

    private static final Color COLOR = Color.CYAN;

    private static final Rectangle RECT = new Rectangle(0, 30, 40, 5);

    private float timeToLive = 3;

    public Laser(boolean facingRight) {
        super();
        startupTime = STARTUP;
        attackTime = ANIMATION_TIME;
        xVel = facingRight ? SPEED : -SPEED;
        currentColor = COLOR;
        shapeType = ShapeRenderer.ShapeType.Filled;
        getRects().add(RECT);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        timeToLive -= delta;
        if (timeToLive <= 0) {
            setActive(false);
        }
    }
}
