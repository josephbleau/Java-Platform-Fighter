package com.nighto.weebu.entity.stage;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.nighto.weebu.component.stage.StageDataComponent;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.stage.parts.Ledge;
import com.nighto.weebu.entity.stage.parts.Platform;

import java.io.IOException;
import java.util.List;

public class Stage extends Entity {

    protected StageDataComponent stageDataComponent;

    public Stage(Stages stages) {
        try {
            stageDataComponent = StageDataComponent.loadFromJson(stages.name + ".json");
            registerComponent(StageDataComponent.class, stageDataComponent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setTag(stages.name);
    }

    public float getGravity() {
        StageDataComponent stageData = getComponent(StageDataComponent.class);
        return stageData.gravity;
    }

    @Override
    public List<Rectangle> getCollidables() {
        return stageDataComponent.getAllBoundingBoxes();
    }

    /** Returns true if the entity is completely outside of the blast zone **/
    public boolean inBounds(Entity entity) {
        for (Rectangle rect : entity.getCollidables()) {
            if (stageDataComponent.blastZone.contains(rect)) {
                return true;
            }
        }

        return false;
    }

    public boolean isGround(Shape2D rect) {
        for (Rectangle rectangle : stageDataComponent.getPlatformBoundingBoxes()) {
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
        for (Ledge ledge : stageDataComponent.ledges) {
            if (ledge.boundingBox.equals(shape)) {
                return ledge;
            }
        }

        return null;
    }

    public Platform ifPlatformGetPlatform(Shape2D shape) {
        for (Platform plat : stageDataComponent.platforms) {
            if (plat.boundingBox.equals(shape)) {
                return plat;
            }
        }

        return null;
    }
}
