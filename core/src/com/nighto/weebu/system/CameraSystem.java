package com.nighto.weebu.system;

import com.nighto.weebu.component.character.AnimationDataComponent;
import com.nighto.weebu.config.WorldConstants;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.event.EventPublisher;

import java.util.Collections;

public class CameraSystem extends System{
    public CameraSystem(GameContext gameContext, EventPublisher eventPublisher) {
        super(gameContext, eventPublisher, Collections.emptyList());
    }

    @Override
    void process(Entity entity) {
        if (entity.getTag().equals("Player")) {
            AnimationDataComponent animationDataComponent = entity.getComponent(AnimationDataComponent.class);


            gameContext.getCamera().position.x = animationDataComponent.skeleton.getRootBone().getWorldX();
            gameContext.getCamera().position.y = animationDataComponent.skeleton.getY();
            gameContext.getCamera().position.y = WorldConstants.VIEWPORT_HEIGHT * 3f/4f;

            gameContext.getCamera().update();
        }
    }
}
