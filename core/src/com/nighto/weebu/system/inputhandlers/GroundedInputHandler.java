package com.nighto.weebu.system.inputhandlers;

import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.component.character.CharacterDataComponent;
import com.nighto.weebu.component.character.CharacterStateComponent;
import com.nighto.weebu.component.character.ControllerComponent;
import com.nighto.weebu.controller.GameInput;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.character.CharacterState;

public class GroundedInputHandler extends StateBasedInputHandler {

    public GroundedInputHandler() {
        super(
                new CharacterState[] {
                        CharacterState.STATE_DEFAULT,
                        CharacterState.STATE_STANDING,
                        CharacterState.STATE_RUNNING
                },
                CharacterState.concat(CharacterState.ATTACKING_STATES, CharacterState.KNOCKBACK_STATES)
        );
    }

    @Override
    protected boolean doHandleInput(Character character) {
        return handleRun(character) &&
                handleCrouch(character) &&
                handleJump(character) &&
                handleNeutralSpecial(character) &&
                handleStandingAttacks(character) &&
                handleRunningAttacks(character);
    }

    private boolean handleRun(Character character) {
        PhysicalComponent physical = character.getComponent(PhysicalComponent.class);
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        CharacterDataComponent characterData = character.getComponent(CharacterDataComponent.class);
        CharacterStateComponent state = character.getComponent(CharacterStateComponent.class);

        if (controller.isPressed(GameInput.ControlLeftLight) || controller.isPressed(GameInput.ControlLeftHard)) {
            controller.setActivelyControlling(true);
            physical.velocity.x = (-characterData.getActiveAttributes().getGroundSpeed());
            state.enterState(CharacterState.STATE_RUNNING);
        } else if (controller.isPressed(GameInput.ControlRightLight) || controller.isPressed(GameInput.ControlRightHard)) {
            controller.setActivelyControlling(true);
            physical.velocity.x = (characterData.getActiveAttributes().getGroundSpeed());
            state.enterState(CharacterState.STATE_RUNNING);
        } else {
            controller.setActivelyControlling(false);
            state.enterState(CharacterState.STATE_STANDING);
        }

        return true;
    }

    private boolean handleCrouch(Character character) {
        PhysicalComponent physical = character.getComponent(PhysicalComponent.class);
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        CharacterStateComponent state = character.getComponent(CharacterStateComponent.class);

        if (controller.isPressed(GameInput.Crouch)) {
            physical.velocity.x = 0;
            state.enterState(CharacterState.STATE_CROUCHING);
            return false;
        }

        return true;
    }

    private boolean handleJump(Character character) {
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        CharacterDataComponent characterData = character.getComponent(CharacterDataComponent.class);
        CharacterStateComponent state = character.getComponent(CharacterStateComponent.class);

        if (controller.hasChangedSinceLastFrame(GameInput.Jump) && controller.isPressed(GameInput.Jump)) {
            characterData.getActiveAttributes().setNumberOfJumps(1);
            characterData.getTimers().resetTimers();
            state.enterState(CharacterState.STATE_JUMPSQUAT);

            return false;
        }

        return true;
    }

    private boolean handleStandingAttacks(Character character) {
        PhysicalComponent physical = character.getComponent(PhysicalComponent.class);
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        CharacterStateComponent state = character.getComponent(CharacterStateComponent.class);

        if(state.inSubState(CharacterState.ATTACKING_STATES)) {
           return false;
        }

        if (state.inState(CharacterState.STATE_RUNNING)) {
            return true;
        }

        if (!controller.isPressed(GameInput.NeutralAttack)) {
            return true;
        }

        if (controller.isPressed(GameInput.ControlUp)) {
            state.enterSubState(CharacterState.SUBSTATE_ATTACKING_UP_TILT);
        } else {
            state.enterSubState(CharacterState.SUBSTATE_ATTACKING_NEUTRAL_NORMAL);
        }

        physical.velocity.x = 0;

        return false;
    }

    private boolean handleRunningAttacks(Character character) {
        PhysicalComponent physical = character.getComponent(PhysicalComponent.class);
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        CharacterStateComponent state = character.getComponent(CharacterStateComponent.class);

        if(state.inSubState(CharacterState.ATTACKING_STATES)) {
            return false;
        }

        if (!state.inState(CharacterState.STATE_RUNNING)) {
            return true;
        }

        if (!controller.isPressed(GameInput.NeutralAttack)) {
            return true;
        }

        state.enterSubState(CharacterState.SUBSTATE_ATTACKING_DASH_ATTACK);
        physical.velocity.x = 0;

        return false;
    }

    private boolean handleNeutralSpecial(Character character) {
        PhysicalComponent physical = character.getComponent(PhysicalComponent.class);
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        CharacterStateComponent state = character.getComponent(CharacterStateComponent.class);

        if (!state.inSubState(CharacterState.ATTACKING_STATES)) {
            if (controller.isPressed(GameInput.NeutralSpecial)) {
                state.enterSubState(CharacterState.SUBSTATE_ATTACKING_NEUTRAL_SPECIAL);
                physical.velocity.x = 0;
            }
        }

        return true;
    }
}
