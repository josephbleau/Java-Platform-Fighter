package com.nighto.weebu.system;

import com.badlogic.gdx.Gdx;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.event.EventPublisher;
import com.nighto.weebu.event.events.DeathEvent;

import java.util.Collections;

public class CollisionSystem extends System {
    public CollisionSystem(GameContext gameContext, EventPublisher eventPublisher) {
        super(gameContext, eventPublisher, Collections.emptyList());
    }

    @Override
    void process(Entity entity) {}

    @Override
    public void process() {
        for (int i = 0; i < gameContext.getEntities().size(); i++) {
            Entity e1 = gameContext.getEntities().get(i);

            if (!gameContext.getStage().inBounds(e1)) {
                DeathEvent deathEvent = new DeathEvent(e1);
                eventPublisher.publish(deathEvent);
                continue;
            }

            if (!e1.isCollidable()) {
                continue;
            }

            for (int j = i+1; j < gameContext.getEntities().size(); j++) {
                Entity e2 = gameContext.getEntities().get(j);
                Gdx.app.debug("Collision", "Checking " + e1.getTag() + " and " + e2.getTag());

                if (!e2.isCollidable()) {
                    continue;
                }

                eventPublisher.publish(e1.intersects(e2));
            }
        }
    }
}
