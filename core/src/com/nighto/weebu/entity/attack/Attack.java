package com.nighto.weebu.entity.attack;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.event.game.Event;

import java.util.List;
import java.util.stream.Collectors;

public class Attack extends Entity {
    protected float startupTime;
    protected float attackTime;

    protected float knockbackInduced;
    protected float knockbackModifierIncrease;
    protected float xImpulse;
    protected float yImpulse;

    private Entity owner;

    private List<Rectangle> hitBoxes;
    private Color hitBoxColor;

    protected Attack(Entity owner, List<Rectangle> hitBoxes) {
        setHidden(true);

        this.owner = owner;
        this.hitBoxes = hitBoxes;

        hitBoxColor = Color.ORANGE;
    }

    @Override
    public void update(float delta) {
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
        PhysicalComponent physical = getComponent(PhysicalComponent.class);

        for (Rectangle hitBox : hitBoxes) {
            shapeRenderer.begin(shapeType);
            shapeRenderer.setColor(hitBoxColor);
            shapeRenderer.rect(physical.position.x + hitBox.x, physical.position.y + hitBox.y, hitBox.width, hitBox.height);
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

    @Override
    public List<Rectangle> getTranslatedRects() {
        return getHitBoxes();
    }

    @Override
    public void teleport(float x, float y) {
        teleport(x, y, true);
    }

    public boolean isPlaying() {
        return startupTime > 0 || attackTime > 0;
    }

    public List<Rectangle> getHitBoxes() {
        PhysicalComponent physicalComponent = getComponent(PhysicalComponent.class);

        // Translated by position
        return hitBoxes.stream().map(r -> new Rectangle(r.x + physicalComponent.position.x, r.y + physicalComponent.position.y, r.width, r.height)).collect(Collectors.toList());
    }

    public float getKnockbackInduced() {
        return knockbackInduced;
    }

    public float getKnockbackModifierIncrease() {
        return knockbackModifierIncrease;
    }

    public float getxImpulse() {
        return xImpulse;
    }

    public float getyImpulse() {
        return yImpulse;
    }

    public Entity getOwner() {
        return owner;
    }
}
