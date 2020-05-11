package com.nighto.weebu.entity.attack;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.nighto.weebu.entity.Entity;

import java.util.List;

public class Attack extends Entity {
    protected float startupTime;
    protected float attackTime;

    private List<Rectangle> hitBoxes;
    private Color hitBoxColor;

    protected Attack(List<Rectangle> hitBoxes) {
        super();
        this.hitBoxes = hitBoxes;
        hitBoxColor = Color.ORANGE;
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

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        for (Rectangle hitBox : hitBoxes) {
            shapeRenderer.begin(shapeType);
            shapeRenderer.setColor(hitBoxColor);
            shapeRenderer.rect(xPos + hitBox.x, yPos + hitBox.y, hitBox.width, hitBox.height);
            shapeRenderer.end();
        }
    }

    public boolean isPlaying() {
        return startupTime > 0 || attackTime > 0;
    }

    public List<Rectangle> getHitBoxes() {
        return hitBoxes;
    }
}
