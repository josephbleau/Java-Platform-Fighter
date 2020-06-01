package com.nighto.weebu.system;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.nighto.weebu.component.stage.StageDataComponent;
import com.nighto.weebu.config.WorldConstants;
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
        // Draw debug grid (1m x 1m)
        drawDebugGrid();
        entity.render(shapeRenderer);
    }

    private void drawDebugGrid() {
        StageDataComponent stageDataComponent = gameContext.getStage().getComponent(StageDataComponent.class);

        float stageWidthInPx = stageDataComponent.width * WorldConstants.UNIT_TO_PX;
        float stageHeightInPx = stageDataComponent.height * WorldConstants.UNIT_TO_PX;

        for (int y = 0; y < stageHeightInPx; y += WorldConstants.UNIT_TO_PX) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.GRAY);
            shapeRenderer.line(0, y, stageWidthInPx, y);
            shapeRenderer.end();
        }

        for (int x = 0; x < stageWidthInPx; x += WorldConstants.UNIT_TO_PX) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.GRAY);
            shapeRenderer.line(x, 0, x, stageHeightInPx);
            shapeRenderer.end();
        }
    }

    @Override
    public void process() {
        shapeRenderer.setProjectionMatrix(gameContext.getCamera().combined);
        super.process();
    }
}
