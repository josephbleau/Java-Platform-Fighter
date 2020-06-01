package com.nighto.weebu.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.nighto.weebu.component.Component;
import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.config.WorldConstants;
import com.nighto.weebu.event.EventHandler;
import com.nighto.weebu.event.EventListener;
import com.nighto.weebu.event.game.CollisionEvent;
import com.nighto.weebu.event.game.Event;
import com.nighto.weebu.system.GameContext;

import java.util.*;

/**
 * Entity is the base object representing all "things" in the game, whether it be a player, a stage, an item, or some
 * other interactive element. It has various properties that affect the way the game interprets, renders, and updates them.
 */
public abstract class Entity implements EventListener {

    private Map<Class<? extends Component>, Component> components;

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

    private GameContext gameContext;

    private UUID uuid;
    private String tag;

    public Entity() {
        tag = "Unknown";
        uuid = UUID.randomUUID();

        components = new HashMap<>();
        registerComponent(PhysicalComponent.class, new PhysicalComponent());

        this.defaultColor = Color.BLACK;
        this.currentColor = this.defaultColor;

        this.active = true;
        this.hidden = false;
        this.collidable = true;

        this.eventHandlers = new ArrayList<>();
        this.rects = new ArrayList<>();
    }

    public Entity(GameContext gameContext) {
        super();
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
            shapeRenderer.rect(
                    WorldConstants.UNIT_TO_PX * physicalComponent.position.x + rect.x,
                    WorldConstants.UNIT_TO_PX * physicalComponent.position.y + rect.y,
                    WorldConstants.UNIT_TO_PX * rect.width,
                    WorldConstants.UNIT_TO_PX * rect.height
            );
            shapeRenderer.end();
        }
    }

    public void registerComponent(Class<? extends Component> componentType, Component component) {
        components.put(componentType, component);
    }

    public <T extends Component> T getComponent(Class<? extends Component> componentType) {
        return (T) components.get(componentType);
    }

    public boolean componentEnabled(Class<? extends Component> component) {
        return getComponent(component) != null && getComponent(component).isEnabled();
    }

    public boolean componentsEnabled(Class<? extends Component>... components ) {
        boolean enabled = true;

        for (Class<? extends Component> component : components) {
            enabled &= componentEnabled(component);
        }

        return enabled;
    }

    public abstract void update(float delta);

    public void teleport(float x, float y) {
        teleport(x, y, false);
    }

    public void teleport(float x, float y, boolean keepVelocity) {
        PhysicalComponent physicalComponent = getComponent(PhysicalComponent.class);

        if (!keepVelocity) {
            physicalComponent.velocity = new Vector2();
        }

        physicalComponent.prevPosition.x = physicalComponent.position.x;
        physicalComponent.prevPosition.y = physicalComponent.position.y;
        physicalComponent.position.x = x;
        physicalComponent.position.y = y;

        physicalComponent.wallSlidingOn = null;
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

    public GameContext getGameContext() {
        return gameContext;
    }

    public void setGameContext(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Map<Class<? extends Component>, Component> getComponents() {
        return components;
    }
}
