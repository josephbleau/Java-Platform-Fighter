package com.josephbleau.game.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.josephbleau.game.entity.stage.Stage;
import com.josephbleau.game.event.EventListener;
import com.josephbleau.game.event.EventPublisher;
import com.josephbleau.game.event.events.CollissionEvent;
import com.josephbleau.game.event.events.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity is the base object representing all "things" in the game, whether it be a player, a stage, an item, or some
 * other interactive element. It has various properties that affect the way the game interprets, renders, and updates them.
 */
public class Entity implements EventListener, EventPublisher {
    /** Origin is used as a way to translate the entire group of rectangles **/
    private Rectangle origin;

    /** Rectangles representing the shape of the entity **/
    private List<Rectangle> rects;

    /** Color that the shape is rendered as **/
    private Color outlineColor;

    /** Active being set to true means update() will process **/
    private Boolean active;

    /** Hidden being set to true means that render() will process **/
    private Boolean hidden;

    /** Collidable being set to true means that collisions are registered and published **/
    private Boolean collidable;

    /** Solid being set to true means that objects cannot pass through this one **/
    private Boolean solid;

    public Entity() {
        this.origin = new Rectangle(0, 0,0 ,0);

        this.rects = new ArrayList<>();
        this.outlineColor = Color.BLACK;

        this.active = false;
        this.hidden = true;
        this.collidable = true;
        this.solid = true;
    }

    public Entity(final Rectangle origin, final List<Rectangle> rects) {
        this();

        this.origin = origin;

        for (Rectangle rect : rects) {
            this.rects.add(new Rectangle(rect.x, rect.y, rect.width, rect.height));
        }
    }

    public void render(ShapeRenderer shapeRenderer) {
        if (this.hidden) {
            return;
        }

        for (Rectangle rect : rects) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(this.outlineColor);
            shapeRenderer.rect(this.origin.x + rect.x, this.origin.y + rect.y, rect.width, rect.height);
            shapeRenderer.end();
        }
    }

    public void update(float delta) {
        if (!this.active) {
            return;
        }
    }

    public void spawn(float x, float y) {
        this.hidden = false;
        this.active = true;

        this.origin = new Rectangle(x, y, 0, 0);
    }

    @Override
    public void notify(Event event) {}

    public Boolean intersects(Entity otherEntity) {
        for (Rectangle rect : getTranslatedRects()) {
            for (Rectangle otherRect : otherEntity.getTranslatedRects()) {
                if (otherRect.overlaps(rect)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void applyForce(float x, float y) {
        this.origin.x += x;
        this.origin.y += y;
    }

    public List<Rectangle> getRects() {
        return rects;
    }

    public List<Rectangle> getTranslatedRects() {
        List<Rectangle> translatedRects = new ArrayList<Rectangle>();

        for (Rectangle rect : getRects()) {
            translatedRects.add(new Rectangle(this.origin.x + rect.x, this.origin.y + rect.y, rect.width, rect.height));
        }

        return translatedRects;
    }

    public Color getOutlineColor() {
        return outlineColor;
    }

    public Rectangle getOrigin() {
        return origin;
    }

    public Boolean isActive() {
        return active;
    }

    public Boolean isHidden() {
        return hidden;
    }

    public Boolean isCollidable() {
        return collidable;
    }

    public Boolean isSolid() {
        return solid;
    }
}
