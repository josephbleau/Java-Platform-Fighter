package com.nighto.weebu.system;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.BoundingBoxAttachment;
import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.component.character.AnimationDataComponent;
import com.nighto.weebu.component.stage.StageDataComponent;
import com.nighto.weebu.config.WorldConstants;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.stage.Stage;
import com.nighto.weebu.entity.stage.parts.Ledge;
import com.nighto.weebu.entity.stage.parts.Platform;
import com.nighto.weebu.event.EventPublisher;

import java.util.Collections;

public class DebugRenderingSystem extends System {
    private final ShapeRenderer shapeRenderer;

    protected Color ledgeColor = Color.BLUE;
    protected Color stageColor = Color.DARK_GRAY;
    protected Color blastZoneColor = Color.RED;

    public DebugRenderingSystem(GameContext gameContext, EventPublisher eventPublisher) {
        super(gameContext, eventPublisher, Collections.emptyList());
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    protected void process(Entity entity) {
        renderDebugGrid();

        if (entity instanceof Stage) {
            renderStage((Stage) entity);
        }

        if (entity instanceof Character) {
            renderCharacter((Character) entity);
        }
    }

    private void renderDebugGrid() {
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

    private void renderStage(Stage stage) {
        StageDataComponent stageData = stage.getComponent(StageDataComponent.class);

        // Draw the platforms
        for (Platform platform : stageData.platforms) {
            if(!platform.passThru) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            } else {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            }

            shapeRenderer.setColor(stageColor);
            shapeRenderer.rect(
                    WorldConstants.UNIT_TO_PX * platform.boundingBox.x,
                    WorldConstants.UNIT_TO_PX * platform.boundingBox.y,
                    WorldConstants.UNIT_TO_PX * platform.boundingBox.width,
                    WorldConstants.UNIT_TO_PX * platform.boundingBox.height
            );
            shapeRenderer.end();
        }

        // Draw the blast zone in red.
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(blastZoneColor);
        shapeRenderer.rect(
                WorldConstants.UNIT_TO_PX * stageData.blastZone.x,
                WorldConstants.UNIT_TO_PX * stageData.blastZone.y,
                WorldConstants.UNIT_TO_PX * stageData.blastZone.width,
                WorldConstants.UNIT_TO_PX * stageData.blastZone.height);
        shapeRenderer.end();

        for (Ledge ledge : stageData.ledges) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(ledgeColor);
            shapeRenderer.rect(
                    WorldConstants.UNIT_TO_PX * ledge.boundingBox.x,
                    WorldConstants.UNIT_TO_PX * ledge.boundingBox.y,
                    WorldConstants.UNIT_TO_PX * ledge.boundingBox.width,
                    WorldConstants.UNIT_TO_PX * ledge.boundingBox.height);
            shapeRenderer.end();
        }
    }

    private void renderCharacter(Character character) {
        AnimationDataComponent animationDataComponent = character.getComponent(AnimationDataComponent.class);
        PhysicalComponent physicalComponent = character.getComponent(PhysicalComponent.class);

        Slot collisionSlot = animationDataComponent.skeleton.findSlot("collision");

        if (collisionSlot != null) {
            BoundingBoxAttachment boundingBoxAttachment = (BoundingBoxAttachment) collisionSlot.getAttachment();

            float[] bbVerts = boundingBoxAttachment.getVertices();
            Polygon bbPoly = new Polygon(bbVerts);
            bbPoly.translate(collisionSlot.getBone().getWorldX() * WorldConstants.PX_TO_UNIT, collisionSlot.getBone().getWorldY() * WorldConstants.PX_TO_UNIT);
            bbPoly.setScale(collisionSlot.getBone().getScaleX() * WorldConstants.PX_TO_UNIT, collisionSlot.getBone().getWorldScaleY() * WorldConstants.PX_TO_UNIT);
            Rectangle bb = bbPoly.getBoundingRectangle();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.PURPLE);
            shapeRenderer.rect(
                    WorldConstants.UNIT_TO_PX * bb.x,
                    WorldConstants.UNIT_TO_PX * bb.y,
                    WorldConstants.UNIT_TO_PX * bb.width,
                    WorldConstants.UNIT_TO_PX * bb.height);
            shapeRenderer.end();
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(
                WorldConstants.UNIT_TO_PX * physicalComponent.position.x,
                WorldConstants.UNIT_TO_PX * physicalComponent.position.y,
                WorldConstants.UNIT_TO_PX * physicalComponent.dimensions.x,
                WorldConstants.UNIT_TO_PX * physicalComponent.dimensions.y
        );
        shapeRenderer.end();
    }

    @Override
    public void process() {
        shapeRenderer.setProjectionMatrix(gameContext.getCamera().combined);
        super.process();
    }
}
