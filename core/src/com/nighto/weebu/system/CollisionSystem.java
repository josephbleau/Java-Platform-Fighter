package com.nighto.weebu.system;

import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.event.EventPublisher;
import com.nighto.weebu.event.events.DeathEvent;

public class CollisionSystem extends System {
    public CollisionSystem(GameContext gameContext, EventPublisher eventPublisher) {
        super(gameContext, eventPublisher);
    }

    @Override
    void process(Entity entity) {
        if (!gameContext.getStage().inBounds(entity)) {
            DeathEvent deathEvent = new DeathEvent(entity);
            eventPublisher.publish(deathEvent);
        }

        for (Entity collidableEntity : gameContext.getEntities()) {
            if (collidableEntity == entity) {
                continue;
            }

            if (entity.isCollidable() && collidableEntity.isCollidable()) {
                eventPublisher.publish(entity.intersects(collidableEntity));
            }
        }
    }
}
