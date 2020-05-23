package com.nighto.weebu.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.spine.Skeleton;
import com.nighto.weebu.component.Component;
import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.event.EventHandler;
import com.nighto.weebu.event.EventListener;
import com.nighto.weebu.event.EventPublisher;
import com.nighto.weebu.event.events.CollisionEvent;
import com.nighto.weebu.event.events.Event;
import com.nighto.weebu.screen.StageScreen;
import com.nighto.weebu.system.GameContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entity is the base object representing all "things" in the game, whether it be a player, a stage, an item, or some
 * other interactive element. It has various properties that affect the way the game interprets, renders, and updates them.
 */
public class Entity implements EventListener {

    private Map<Class<?>, Component> components;

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

    private List<EventHandler> eventHandlers;

    private StageScreen stageScreen;
    private GameContext gameContext;

    public Entity(StageScreen stageScreen, GameContext gameContext) {
        components = new HashMap<>();
        registerComponent(PhysicalComponent.class, new PhysicalComponent());

        this.rects = new ArrayList<>();
        this.defaultColor = Color.BLACK;
        this.currentColor = this.defaultColor;

        this.active = true;
        this.hidden = false;
        this.collidable = true;

        this.eventHandlers = new ArrayList<>();
        this.stageScreen = stageScreen;
        this.gameContext = gameContext;
    }

    public void render(ShapeRenderer shapeRenderer) {
        if (hidden) {
            return;
        }

        PhysicalComponent physicalComponent = getComponent(PhysicalComponent.class);

        for (Rectangle rect : rects) {
            shapeRenderer.begin(shapeType);
            shapeRenderer.setColor(currentColor);
            shapeRenderer.rect(physicalComponent.position.x + rect.x, physicalComponent.position.y + rect.y, rect.width, rect.height);
            shapeRenderer.end();
        }
    }

    public void registerComponent(Class<?> componentType, Component component) {
        components.put(componentType, component);
    }

    public <T extends Component> T getComponent(Class<?> componentType) {
        return (T) components.get(componentType);
    }

    public Skeleton getSkeleton() {
        return null;
    }

    public void update(float delta) {
        if (!active) {
            return;
        }
    }

    public void spawn(float x, float y) {
        hidden = false;
        active = true;

        // TODO: Move to PhysicalSystem
        registerComponent(PhysicalComponent.class, new PhysicalComponent(new Vector2(x, y), Vector2.Zero));
    }

    public void teleport(float x, float y, boolean keepVelocity) {
        // TODO: Move to PhysicalSystem
        PhysicalComponent physicalComponent = getComponent(PhysicalComponent.class);

        if (!keepVelocity) {
            physicalComponent.velocity = new Vector2();
        }

        physicalComponent.prevPosition.x = physicalComponent.position.x;
        physicalComponent.prevPosition.y = physicalComponent.position.y;
        physicalComponent.position.x = x;
        physicalComponent.position.y = y;
    }

    @Override
    public void notify(Event event) {
        for (EventHandler eventHandler : eventHandlers) {
            if (eventHandler.supports(event)) {
                eventHandler.handle(event);
            }
        }
    }

    public void registerEventHandler(EventHandler eventHandler) {
        this.eventHandlers.add(eventHandler);
    }

    public List<CollisionEvent> intersects(Entity otherEntity) {
        List<CollisionEvent> collisionEvents = new ArrayList<>();

        for (Rectangle rect : getCollidables()) {
            for (Rectangle otherRect : otherEntity.getCollidables()) {
                if (otherRect.overlaps(rect)) {
                    collisionEvents.add(new CollisionEvent(this, rect, otherEntity, otherRect));
                }
            }
        }

        return collisionEvents;
    }

    public List<Rectangle> getRects() {
        return rects;
    }

    public List<Rectangle> getTranslatedRects() {
        List<Rectangle> translatedRects = new ArrayList<>();

        // TODO: Move to RenderSystem
        PhysicalComponent physicalComponent = getComponent(PhysicalComponent.class);

        for (Rectangle rect : getRects()) {
            translatedRects.add(new Rectangle(physicalComponent.position.x + rect.x, physicalComponent.position.y + rect.y, rect.width, rect.height));
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

    public StageScreen getStageScreen() {
        return stageScreen;
    }

    public GameContext getGameContext() {
        return gameContext;
    }
}
