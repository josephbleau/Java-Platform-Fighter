package com.nighto.weebu.system;

import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.event.EventPublisher;

import java.util.Iterator;

public abstract class System {
    protected final GameContext gameContext;
    protected final EventPublisher eventPublisher;

    protected System(GameContext gameContext, EventPublisher eventPublisher) {
        this.gameContext = gameContext;
        this.eventPublisher = eventPublisher;
    }

    public void process() {
        Iterator<Entity> entityIterator = gameContext.refreshEntitiesIterator();

        while (entityIterator.hasNext()) {
            Entity entity = entityIterator.next();

            if (entity.isActive()) {
                process(entity);
            }
        }
    }

    abstract void process(Entity entity);
}
