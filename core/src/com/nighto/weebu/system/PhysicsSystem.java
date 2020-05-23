package com.nighto.weebu.system;

import com.badlogic.gdx.math.Vector2;
import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.component.StateComponent;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.player.State;
import com.nighto.weebu.event.EventPublisher;

public class PhysicsSystem extends System {
    public PhysicsSystem(GameContext gameContext, EventPublisher eventPublisher) {
        super(gameContext, eventPublisher);
    }

    @Override
    public void process(Entity entity) {
        PhysicalComponent physicalComponent = entity.getComponent(PhysicalComponent.class);
        StateComponent stateComponent = entity.getComponent(StateComponent.class);

        // TODO: Contemplate better way to enable that an entity supports certain components. Should it be
        // TODO: all or nothing, or should systems partially work if one or some of the components are present?
        // TODO: Should a system needing to interact with multiple components be an indication that the system
        // TODO: should be broken into multiple components?
        if (physicalComponent != null && physicalComponent.isEnabled()) {
            updatePosition(entity);

            if (stateComponent != null && stateComponent.isEnabled()) {
                updateDirection(physicalComponent, stateComponent);
            }
        }
    }

    private void updatePosition(Entity entity) {
        entity.update(gameContext.getFrameDelta());

        PhysicalComponent physicalComponent = entity.getComponent(PhysicalComponent.class);

        physicalComponent.prevPosition = new Vector2(physicalComponent.position);
        physicalComponent.prevVelocity = new Vector2(physicalComponent.velocity);
        physicalComponent.position.add(physicalComponent.velocity);
    }

    private void updateDirection(PhysicalComponent physicalComponent, StateComponent stateComponent) {
        if (!stateComponent.inState(State.HANGING, State.JUMPSQUAT, State.AIRBORNE, State.SIDESTEPPING)) {
            if (physicalComponent.velocity.x > 0) {
                physicalComponent.facingRight = true;
            } else if (physicalComponent.velocity.x < 0) {
                physicalComponent.facingRight = false;
            }
        }
    }
}
