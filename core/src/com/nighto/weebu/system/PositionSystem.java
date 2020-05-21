package com.nighto.weebu.system;

import com.badlogic.gdx.math.Vector2;
import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.entity.Entity;

public class PositionSystem extends System {
    private PositionSystem() {}
    private static System instance;

    @Override
    public void process(Entity entity) {
        PhysicalComponent physicalComponent = entity.getComponent(PhysicalComponent.class);

        if (physicalComponent != null && physicalComponent.isEnabled()) {
            physicalComponent.prevPosition = new Vector2(physicalComponent.position);
            physicalComponent.prevVelocity = new Vector2(physicalComponent.velocity);

            physicalComponent.position.add(physicalComponent.velocity);
        }
    }

    public static System getInstance() {
        if (instance == null) {
            instance = new PositionSystem();
        }

        return instance;
    }
}
