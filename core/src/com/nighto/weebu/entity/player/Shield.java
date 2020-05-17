package com.nighto.weebu.entity.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.event.events.CollisionEvent;
import com.nighto.weebu.screen.StageScreen;

import java.util.ArrayList;
import java.util.List;

public class Shield extends Entity {

    private static final float DRAIN_RATE = .07f;

    private static final float GROW_RATE = .2f;

    private Circle circle;

    private Color color;

    private float size = 1.0f;

    public Shield(StageScreen stageScreen, Circle circle, Color color) {
        super(stageScreen);

        this.circle = circle;
        this.color = color;

        setActive(false);
    }

    @Override
    public void update(float delta) {
        if (isActive()) {
            size = MathUtils.clamp(size - DRAIN_RATE * delta, 0, 1);
        } else {
            size = MathUtils.clamp(size + GROW_RATE * delta, 0, 1);
        }
    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        if (isActive()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(color);
            shapeRenderer.circle(xPos + circle.x, yPos + circle.y, size * circle.radius);
            shapeRenderer.end();
        }
    }

    @Override
    public List<CollisionEvent> intersects(Entity otherEntity) {
        List<CollisionEvent> collisionEvents = new ArrayList<>();

        if (isActive()) {
            Circle translatedCircle = new Circle(xPos + circle.x, yPos + circle.y, size * circle.radius);

            for (Rectangle otherRect : otherEntity.getTranslatedRects()) {
                if (Intersector.overlaps(translatedCircle, otherRect)) {
                    collisionEvents.add(new CollisionEvent(this, translatedCircle, otherEntity, otherRect));
                }
            }
        }

        return collisionEvents;
    }
}
