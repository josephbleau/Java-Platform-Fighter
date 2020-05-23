package com.nighto.weebu.system;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.event.EventPublisher;

import java.util.Collections;

public class DebugRenderingSystem extends System {
    private final OrthographicCamera camera;
    private final ShapeRenderer shapeRenderer;

    public DebugRenderingSystem(GameContext gameContext, EventPublisher eventPublisher) {
        super(gameContext, eventPublisher, Collections.emptyList());

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    protected void process(Entity entity) {
        entity.render(shapeRenderer);
    }

    @Override
    public void process() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        super.process();
    }
}
