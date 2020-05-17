package com.nighto.weebu.entity.attack;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.event.events.Event;
import com.nighto.weebu.screen.StageScreen;

import java.util.List;
import java.util.stream.Collectors;

public class Attack extends Entity {
    protected float startupTime;
    protected float attackTime;
    protected float knockbackInduced;

    private Character owner;

    private List<Rectangle> hitBoxes;
    private Color hitBoxColor;

    protected Attack(Character owner, List<Rectangle> hitBoxes) {
        super(owner.getStageScreen());
        this.owner = owner;
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

    @Override
    public void notify(Event event) {
        super.notify(event);
    }

    @Override
    public List<Rectangle> getCollidables() {
        return getHitBoxes();
    }

    public boolean isPlaying() {
        return startupTime > 0 || attackTime > 0;
    }

    public List<Rectangle> getHitBoxes() {
        // Translated by position
        return hitBoxes.stream().map(r -> new Rectangle(r.x + xPos, r.y + yPos, r.width, r.height)).collect(Collectors.toList());
    }
}
