package com.nighto.weebu.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.nighto.weebu.event.EventListener;
import com.nighto.weebu.event.EventPublisher;
import com.nighto.weebu.event.events.CollissionEvent;
import com.nighto.weebu.event.events.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity is the base object representing all "things" in the game, whether it be a player, a stage, an item, or some
 * other interactive element. It has various properties that affect the way the game interprets, renders, and updates them.
 */
public class Entity implements EventListener, EventPublisher {

    /** Current x position **/
    protected float xPos;

    /** Current y position **/
    protected float yPos;

    /** Current x velocity **/
    protected float xVel;

    /** Current y velocity **/
    protected float yVel;

    /** Color that the shape is rendered as by default **/
    protected Color defaultColor;

    /** Color that the shape is rendered as currently (changes with state). **/
    protected Color currentColor;

    protected ShapeRenderer.ShapeType shapeType = ShapeRenderer.ShapeType.Line;

    /** Rectangles representing the shape of the entity **/
    private List<Rectangle> rects;

    /** Active being set to true means update() will process **/
    private boolean active;

    /** Hidden being set to true means that render() will process **/
    private boolean hidden;

    /** Collidable being set to true means that collisions are registered and published **/
    private boolean collidable;

    public Entity() {
        this.xPos = 0;
        this.yPos = 0;

        this.rects = new ArrayList<>();
        this.defaultColor = Color.BLACK;
        this.currentColor = this.defaultColor;

        this.active = false;
        this.hidden = true;
        this.collidable = true;
    }

    public void render(ShapeRenderer shapeRenderer) {
        if (hidden) {
            return;
        }

        for (Rectangle rect : rects) {
            shapeRenderer.begin(shapeType);
            shapeRenderer.setColor(currentColor);
            shapeRenderer.rect(xPos + rect.x, yPos + rect.y, rect.width, rect.height);
            shapeRenderer.end();
        }
    }

    public void update(float delta) {
        if (!active) {
            return;
        }

        xPos += xVel;
        yPos += yVel;
    }

    public void spawn(float x, float y) {
        hidden = false;
        active = true;

        xPos = x;
        yPos = y;
    }

    public void teleport(float x, float y, boolean keepVelocity) {
        if (!keepVelocity) {
            xVel = 0;
            yVel = 0;
        }

        xPos = x;
        yPos = y;
    }

    @Override
    public void notify(Event event) {}

    public CollissionEvent intersects(Entity otherEntity) {
        for (Rectangle rect : getCollidables()) {
            for (Rectangle otherRect : otherEntity.getCollidables()) {
                if (otherRect.overlaps(rect)) {
                    return new CollissionEvent(this, rect, otherEntity, otherRect);
                }
            }
        }

        return null;
    }

    public List<Rectangle> getRects() {
        return rects;
    }

    public List<Rectangle> getTranslatedRects() {
        List<Rectangle> translatedRects = new ArrayList<>();

        for (Rectangle rect : getRects()) {
            translatedRects.add(new Rectangle(xPos + rect.x, yPos + rect.y, rect.width, rect.height));
        }

        return translatedRects;
    }

    public List<Rectangle> getCollidables() {
        return getTranslatedRects();
    }

    public Boolean isCollidable() {
        return collidable;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void setxVel(float xVel) {
        this.xVel = xVel;
    }

    public void setyVel(float yVel) {
        this.yVel = yVel;
    }
}
