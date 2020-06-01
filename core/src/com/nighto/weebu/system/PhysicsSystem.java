package com.nighto.weebu.system;

import com.badlogic.gdx.math.Vector2;
import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.component.character.CharacterDataComponent;
import com.nighto.weebu.component.character.ControllerComponent;
import com.nighto.weebu.component.character.StateComponent;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.character.State;
import com.nighto.weebu.event.EventPublisher;

import java.util.Collections;

public class PhysicsSystem extends System {
    public PhysicsSystem(GameContext gameContext, EventPublisher eventPublisher) {
        super(gameContext, eventPublisher, Collections.singletonList(PhysicalComponent.class));
    }

    @Override
    public void process(Entity entity) {
        PhysicalComponent physical = entity.getComponent(PhysicalComponent.class);
        StateComponent state = entity.getComponent(StateComponent.class);
        ControllerComponent controller = entity.getComponent(ControllerComponent.class);
        CharacterDataComponent characterData = entity.getComponent(CharacterDataComponent.class);

        if (entity.componentsEnabled(PhysicalComponent.class)) {
            if (physical.floorStandingOn != null) {
                float stageRight = physical.floorStandingOn.x + physical.floorStandingOn.width;
                float stageLeft = physical.floorStandingOn.x;
                float playerRight = physical.position.x + physical.boundingBox.width;
                float playerLeft = physical.position.x;

                boolean standingOnSurface =
                        (stageRight < playerRight && stageRight > playerLeft) ||
                                (stageLeft < playerRight && stageLeft > playerLeft) ||
                                (playerRight < stageRight && playerRight > stageLeft) ||
                                (playerLeft < stageRight && playerLeft > stageLeft);

                if (!standingOnSurface) {
                    physical.floorStandingOn = null;
                    state.enterState(State.AIRBORNE);

                    characterData.getActiveAttributes().decrementJumps();
                }
            }
        }

        if (entity.componentsEnabled(CharacterDataComponent.class, PhysicalComponent.class, StateComponent.class, ControllerComponent.class)) {
            if (state.inState(State.AIRBORNE, State.WALLSLIDING)) {
                applyGravity(characterData, physical, state, controller);
            }
        }

        if (entity.componentsEnabled(ControllerComponent.class, PhysicalComponent.class, StateComponent.class)) {
            updateDirection(controller, physical, state);
        }

        if (entity.componentsEnabled(PhysicalComponent.class)) {
            updatePosition(entity);
        }

        if (entity.componentsEnabled(PhysicalComponent.class, StateComponent.class)) {
            // If a player is actively controlling (holding left or right) then do not apply air friction, velocity
            // will be limited by air speed in those instances.
            if (controller == null || !controller.isActivelyControlling()) {
                if (state.inState(State.DIRECTIONAL_AIRDODGE)) {
                    physical.velocity.x /= characterData.getActiveAttributes().getAirFriction();
                    physical.velocity.y /= characterData.getActiveAttributes().getAirFriction();
                } else if (state.inState(State.AIRBORNE)) {
                    physical.velocity.x /= characterData.getActiveAttributes().getAirFriction();
                } else if (state.inState(State.STANDING)) {
                    physical.velocity.x /= characterData.getActiveAttributes().getGroundFriction();
                }
            }
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
    }

    private void updatePosition(Entity entity) {
        entity.update(gameContext.getFrameDelta());

        PhysicalComponent physicalComponent = entity.getComponent(PhysicalComponent.class);

        physicalComponent.prevPosition = new Vector2(physicalComponent.position);
        physicalComponent.prevVelocity = new Vector2(physicalComponent.velocity);

        physicalComponent.position.mulAdd(physicalComponent.velocity, gameContext.getFrameDelta());
    }

    private void updateDirection(ControllerComponent controller, PhysicalComponent physicalComponent, StateComponent stateComponent) {
        if (controller.isActivelyControlling() && !stateComponent.inState(State.HANGING, State.JUMPSQUAT, State.AIRBORNE, State.SIDESTEPPING, State.DIRECTIONAL_AIRDODGE, State.AIRDODGE) && !stateComponent.inSubState(State.ATTACKING_STATES)) {
            if (physicalComponent.velocity.x > 0) {
                physicalComponent.facingRight = true;
            } else if (physicalComponent.velocity.x < 0) {
                physicalComponent.facingRight = false;
            }
        }
    }
}
