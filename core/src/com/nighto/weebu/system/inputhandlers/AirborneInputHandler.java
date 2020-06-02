package com.nighto.weebu.system.inputhandlers;

import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.component.character.CharacterDataComponent;
import com.nighto.weebu.component.character.ControllerComponent;
import com.nighto.weebu.component.character.CharacterStateComponent;
import com.nighto.weebu.controller.GameInput;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.character.CharacterState;

public class AirborneInputHandler extends StateBasedInputHandler {

    public AirborneInputHandler() {
        super(new CharacterState[]{CharacterState.STATE_AIRBORNE}, new CharacterState[]{CharacterState.SUBSTATE_KNOCKBACK, CharacterState.SUBSTATE_TUMBLE, CharacterState.STATE_AIRDODGE});
    }

    @Override
    protected boolean doHandleInput(Character character) {
        return handleJump(character) &&
                handleSidestep(character) &&
                handleDrift(character) &&
                handleFastFall(character) &&
                handleAttacks(character);
    }

    private boolean handleJump(Character character) {
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        CharacterDataComponent characterData = character.getComponent(CharacterDataComponent.class);
        CharacterStateComponent state = character.getComponent(CharacterStateComponent.class);

        boolean jumpChanged = controller.hasChangedSinceLastFrame(GameInput.Jump);
        int jumpCount = characterData.getActiveAttributes().getNumberOfJumps();

        if (jumpChanged && jumpCount > 0) {
            if (controller.isPressed(GameInput.Jump)) {
                characterData.getActiveAttributes().decrementJumps();
                state.enterState(CharacterState.STATE_JUMPSQUAT);
            }
        }

        return true;
    }

    private boolean handleSidestep(Character character) {
        PhysicalComponent physical = character.getComponent(PhysicalComponent.class);
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        CharacterStateComponent state = character.getComponent(CharacterStateComponent.class);
        CharacterDataComponent characterData = character.getComponent(CharacterDataComponent.class);

        boolean performedAirDodge = controller.isPressed(GameInput.Shield) &&
                characterData.getActiveAttributes().getNumberOfAirDodges() > 0;

        if (performedAirDodge) {
            state.enterState(CharacterState.STATE_AIRDODGE);
            characterData.getActiveAttributes().decrementAirDodges();
            characterData.getActiveAttributes().setNumberOfJumps(0);

            physical.velocity.x = 0;
            physical.velocity.y = 0;

            // TODO: Compute stick-axis velocity vector, for now just do axis aligned directional air-dodges
            if (controller.isPressed(GameInput.ControlLeftHard) || controller.isPressed(GameInput.ControlLeftLight)) {
                state.enterState(CharacterState.STATE_DIRECTIONAL_AIRDODGE);
                physical.velocity.x = -characterData.getActiveAttributes().getAirDodgeVelocity();
            }

            if (controller.isPressed(GameInput.ControlRightHard) || controller.isPressed(GameInput.ControlRightLight)) {
                state.enterState(CharacterState.STATE_DIRECTIONAL_AIRDODGE);
                physical.velocity.x = characterData.getActiveAttributes().getAirDodgeVelocity();
            }

            if (controller.isPressed(GameInput.Jump)) {
                state.enterState(CharacterState.STATE_DIRECTIONAL_AIRDODGE);
                physical.velocity.y = characterData.getActiveAttributes().getAirDodgeVelocity();
            }

            if (controller.isPressed(GameInput.Crouch)) {
                state.enterState(CharacterState.STATE_DIRECTIONAL_AIRDODGE);
                physical.velocity.y = -characterData.getActiveAttributes().getAirDodgeVelocity();
            }

            controller.setActivelyControlling(false);
            return false;
        }

        return true;
    }

    private boolean handleDrift(Character character) {
        PhysicalComponent physical = character.getComponent(PhysicalComponent.class);
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        CharacterDataComponent characterData = character.getComponent(CharacterDataComponent.class);
        CharacterStateComponent stateComponent = character.getComponent(CharacterStateComponent.class);

        if (stateComponent.inState(CharacterState.STATE_DIRECTIONAL_AIRDODGE)) {
            return false;
        }

        if (controller.isPressed(GameInput.ControlLeftLight)) {
            controller.setActivelyControlling(true);
            physical.velocity.x = (-characterData.getActiveAttributes().getAirSpeed());
        } else if (controller.isPressed(GameInput.ControlRightLight)) {
            controller.setActivelyControlling(true);
            physical.velocity.x = (characterData.getActiveAttributes().getAirSpeed());
        } else {
            controller.setActivelyControlling(false);
        }

        return true;
    }

    private boolean handleFastFall(Character character) {
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        CharacterDataComponent characterData = character.getComponent(CharacterDataComponent.class);

        characterData.getActiveAttributes().setFastFalling(controller.isPressed(GameInput.Crouch));
        return true;
    }

    private boolean handleAttacks(Character character) {
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        CharacterStateComponent state = character.getComponent(CharacterStateComponent.class);

        if(!state.inSubState(CharacterState.ATTACKING_STATES)) {
            if (controller.isPressed(GameInput.NeutralSpecial)) {
                state.enterSubState(CharacterState.SUBSTATE_ATTACKING_NEUTRAL_SPECIAL);
                return false;
            }

            if (controller.isPressed(GameInput.NeutralAttack)) {
                state.enterSubState(CharacterState.SUBSTATE_ATTACKING_NEUTRAL_AIR);
                return false;
            }
        }

        return true;
    }
}
