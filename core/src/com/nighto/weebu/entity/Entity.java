package com.nighto.weebu.entity;

import com.badlogic.gdx.math.Rectangle;
import com.nighto.weebu.component.Component;
import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.event.EventListener;
import com.nighto.weebu.event.game.CollisionEvent;
import com.nighto.weebu.system.GameContext;

import java.util.*;

public abstract class Entity extends EventListener {

    private GameContext gameContext;

    private final Map<Class<? extends Component>, Component> components;
    protected PhysicalComponent physicalComponent;

    private final UUID uuid;
    private String tag;
    private boolean active;

    public Entity() {
        tag = "Unknown";
        uuid = UUID.randomUUID();
        active = true;

        components = new HashMap<>();
        physicalComponent = new PhysicalComponent();
        registerComponent(PhysicalComponent.class, physicalComponent);
    }

    public Entity(GameContext gameContext) {
        this();
        this.gameContext = gameContext;
    }

    public void registerComponent(Class<? extends Component> componentType, Component component) {
        components.put(componentType, component);
    }

    public <T extends Component> T getComponent(Class<? extends Component> componentType) {
        return (T) components.get(componentType);
    }

    public Map<Class<? extends Component>, Component> getComponents() {
        return components;
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

    public abstract List<Rectangle> getCollidables();

    public boolean isCollidable() {
        return physicalComponent.collidable;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
