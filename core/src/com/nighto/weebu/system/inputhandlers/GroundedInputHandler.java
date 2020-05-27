package com.nighto.weebu.system.inputhandlers;

import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.component.character.CharacterDataComponent;
import com.nighto.weebu.component.character.ControllerComponent;
import com.nighto.weebu.component.character.StateComponent;
import com.nighto.weebu.controller.GameInput;
import com.nighto.weebu.entity.attack.MeleeAttack;
import com.nighto.weebu.entity.attack.ProjectileAttack;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.character.State;

public class GroundedInputHandler extends StateBasedInputHandler {

    public GroundedInputHandler() {
        super(
                new State[] {
                        State.DEFAULT,
                        State.STANDING,
                        State.RUNNING
                },
                new State[] {
                        State.SUBSTATE_ATTACKING,
                        State.SUBSTATE_KNOCKBACK,
                        State.SUBSTATE_TUMBLE
                }
        );
    }

    @Override
    protected boolean doHandleInput(Character character) {
        return handleShield(character) &&
                handleRun(character) &&
                handleCrouch(character) &&
                handleJump(character) &&
                handleNeutralSpecial(character) &&
                handleNeutralAttack(character);
    }

    private boolean handleShield(Character character) {
        PhysicalComponent physical = character.getComponent(PhysicalComponent.class);
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        StateComponent state = character.getComponent(StateComponent.class);

        if (controller.isPressed(GameInput.Shield)) {
            character.startShielding();
            physical.velocity.x = 0;

            state.enterState(State.SHIELDING);

            return false;
        }

        return true;
    }

    private boolean handleRun(Character character) {
        PhysicalComponent physical = character.getComponent(PhysicalComponent.class);
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        CharacterDataComponent characterData = character.getComponent(CharacterDataComponent.class);
        StateComponent state = character.getComponent(StateComponent.class);

        if (controller.isPressed(GameInput.ControlLeftLight)) {
            controller.setActivelyControlling(true);
            physical.velocity.x = (-characterData.getActiveAttributes().getGroundSpeed());
            state.enterState(State.RUNNING);
        } else if (controller.isPressed(GameInput.ControlRightLight)) {
            controller.setActivelyControlling(true);
            physical.velocity.x = (characterData.getActiveAttributes().getGroundSpeed());
            state.enterState(State.RUNNING);
        } else {
            controller.setActivelyControlling(false);
            state.enterState(State.STANDING);
        }

        return true;
    }

    private boolean handleCrouch(Character character) {
        PhysicalComponent physical = character.getComponent(PhysicalComponent.class);
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        StateComponent state = character.getComponent(StateComponent.class);

        if (controller.isPressed(GameInput.Crouch)) {
            physical.velocity.x = 0;
            state.enterState(State.CROUCHING);
        }

        return true;
    }

    private boolean handleJump(Character character) {
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        CharacterDataComponent characterData = character.getComponent(CharacterDataComponent.class);
        StateComponent state = character.getComponent(StateComponent.class);

        if (controller.hasChangedSinceLastFrame(GameInput.Jump) && controller.isPressed(GameInput.Jump)) {
            characterData.getActiveAttributes().setNumberOfJumps(1);
            characterData.getTimers().resetTimers();
            state.enterState(State.JUMPSQUAT);

            return false;
        }

        return true;
    }

    private boolean handleNeutralAttack(Character character) {
        PhysicalComponent physical = character.getComponent(PhysicalComponent.class);
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        StateComponent state = character.getComponent(StateComponent.class);

        if (!state.inSubState(State.SUBSTATE_ATTACKING)) {
            if (controller.isPressed(GameInput.NeutralAttack)) {
                state.enterSubState(State.SUBSTATE_ATTACKING);
                physical.velocity.x = 0;
            }
        }

        return true;
    }

    private boolean handleNeutralSpecial(Character character) {
        PhysicalComponent physical = character.getComponent(PhysicalComponent.class);
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        StateComponent state = character.getComponent(StateComponent.class);

        if (!state.inSubState(State.SUBSTATE_ATTACKING)) {
            if (controller.isPressed(GameInput.NeutralSpecial)) {
                state.enterSubState(State.SUBSTATE_ATTACKING);
                physical.velocity.x = 0;
            }
        }

        return true;
    }
}
