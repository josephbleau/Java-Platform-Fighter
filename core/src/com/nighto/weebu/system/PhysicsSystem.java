package com.nighto.weebu.system;

import com.badlogic.gdx.math.Vector2;
import com.nighto.weebu.component.CharacterDataComponent;
import com.nighto.weebu.component.ControllerComponent;
import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.component.StateComponent;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.player.State;
import com.nighto.weebu.event.EventPublisher;

import java.util.Arrays;

public class PhysicsSystem extends System {
    public PhysicsSystem(GameContext gameContext, EventPublisher eventPublisher) {
        super(gameContext, eventPublisher, Arrays.asList(PhysicalComponent.class, StateComponent.class, ControllerComponent.class, CharacterDataComponent.class));
    }

    @Override
    public void process(Entity entity) {
        PhysicalComponent physical = entity.getComponent(PhysicalComponent.class);
        StateComponent state = entity.getComponent(StateComponent.class);
        ControllerComponent controller = entity.getComponent(ControllerComponent.class);
        CharacterDataComponent characterData = entity.getComponent(CharacterDataComponent.class);

        if (physical.isEnabled()) {
            applyGravity(characterData, physical, state, controller);
            updatePosition(entity);
            updateDirection(physical, state);
        }
    }

    private void applyGravity(CharacterDataComponent characterData, PhysicalComponent physical, StateComponent state, ControllerComponent controller) {
        float proposedyVel = Math.max(characterData.getActiveAttributes().getFallSpeed(), physical.velocity.y - gameContext.getStage().getGravity());

        if (physical.velocity.y > characterData.getActiveAttributes().getFallSpeed()) {
            physical.velocity.y = proposedyVel;
        }

        if (state.inState(State.WALLSLIDING)) {
            physical.velocity.y = proposedyVel/3;
        }

        if (physical.velocity.y < 0 && characterData.getActiveAttributes().isFastFalling()) {
            physical.velocity.y = proposedyVel * 2;
        }

        // If a player is actively controlling (holding left or right) then do not apply air friction, velocity
        // will be limited by air speed in those instances.
        if (!controller.isActivelyControlling()) {
            if (state.inState(State.AIRBORNE)) {
                physical.velocity.x /= characterData.getActiveAttributes().getAirFriction();
            } else if (state.inState(State.STANDING)) {
                physical.velocity.x /= characterData.getActiveAttributes().getGroundFriction();
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