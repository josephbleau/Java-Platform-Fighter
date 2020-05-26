package com.nighto.weebu.entity.stage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.nighto.weebu.component.stage.StageDataComponent;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.stage.parts.Ledge;
import com.nighto.weebu.entity.stage.parts.Platform;

import java.io.IOException;
import java.util.List;

public class Stage extends Entity {
    protected Color ledgeColor;
    protected Color stageColor;
    protected Color blastZoneColor;

    public Stage(Stages stages) {
        setActive(true);

        stageColor = Color.DARK_GRAY;
        blastZoneColor = Color.GRAY;
        ledgeColor = Color.BLUE;

        try {
            registerComponent(StageDataComponent.class, StageDataComponent.loadFromJson(stages.name + ".json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setTag(stages.name);
    }

    public void render(ShapeRenderer shapeRenderer) {
        StageDataComponent stageData = getComponent(StageDataComponent.class);

        // Draw the platforms
        for (Platform platform : stageData.platforms) {
            if(!platform.passThru) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            } else {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            }

            shapeRenderer.setColor(stageColor);
            shapeRenderer.rect(platform.boundingBox.x, platform.boundingBox.y, platform.boundingBox.width, platform.boundingBox.height);
            shapeRenderer.end();
        }

        // Draw the blast zone in red.
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(blastZoneColor);
        shapeRenderer.rect(stageData.blastZone.x, stageData.blastZone.y, stageData.blastZone.width, stageData.blastZone.height);
        shapeRenderer.end();

        for (Ledge ledge : stageData.ledges) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(ledgeColor);
            shapeRenderer.rect(ledge.boundingBox.x, ledge.boundingBox.y, ledge.boundingBox.width, ledge.boundingBox.height);
            shapeRenderer.end();
        }
    }

    @Override
    public void update(float delta) {}

    public float getGravity() {
        StageDataComponent stageData = getComponent(StageDataComponent.class);
        return stageData.gravity;
    }

    @Override
    public List<Rectangle> getCollidables() {
        StageDataComponent stageData = getComponent(StageDataComponent.class);
        return stageData.getAllBoundingBoxes();
    }

    /** Returns true if the entity is completely outside of the blast zone **/
    public boolean inBounds(Entity entity) {
        StageDataComponent stageData = getComponent(StageDataComponent.class);

        for (Rectangle rect : entity.getTranslatedRects()) {
            if (stageData.blastZone.contains(rect)) {
                return true;
            }
        }

        return false;
    }

    public boolean isGround(Shape2D rect) {
        StageDataComponent stageData = getComponent(StageDataComponent.class);

        for (Rectangle rectangle : stageData.getPlatformBoundingBoxes()) {
            if (rect.equals(rectangle)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns a handle to a stage ledge if the shape passed in is from that ledge.
     */
    public Ledge ifLedgeGetLedge(Shape2D shape) {
        StageDataComponent stageData = getComponent(StageDataComponent.class);

        for (Ledge ledge : stageData.ledges) {
            if (ledge.boundingBox.equals(shape)) {
                return ledge;
            }
        }

        return null;
    }

    public Platform ifPlatformGetPlatform(Shape2D shape) {
        StageDataComponent stageData = getComponent(StageDataComponent.class);

        for (Platform plat : stageData.platforms) {
            if (plat.boundingBox.equals(shape)) {
                return plat;
            }
        }

        return null;
    }
}
