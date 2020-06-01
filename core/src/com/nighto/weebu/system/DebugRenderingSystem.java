package com.nighto.weebu.system;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.event.EventPublisher;

import java.util.Collections;

public class DebugRenderingSystem extends System {
    private final ShapeRenderer shapeRenderer;

    public DebugRenderingSystem(GameContext gameContext, EventPublisher eventPublisher) {
        super(gameContext, eventPublisher, Collections.emptyList());
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    protected void process(Entity entity) {
        entity.render(shapeRenderer);
    }

    @Override
    public void process() {
        shapeRenderer.setProjectionMatrix(gameContext.getCamera().combined);
        super.process();
    }
}
