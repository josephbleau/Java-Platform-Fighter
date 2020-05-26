package com.nighto.weebu.system.inputhandlers;

import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.component.character.CharacterDataComponent;
import com.nighto.weebu.component.character.ControllerComponent;
import com.nighto.weebu.component.character.StateComponent;
import com.nighto.weebu.controller.GameInput;
import com.nighto.weebu.entity.attack.ProjectileAttack;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.character.State;

public class AirborneInputHandler extends StateBasedInputHandler {

    public AirborneInputHandler() {
        super(new State[]{State.AIRBORNE}, new State[]{State.SUBSTATE_KNOCKBACK, State.SUBSTATE_TUMBLE, State.AIRDODGE});
    }

    @Override
    protected boolean doHandleInput(Character character) {
        return handleJump(character) &&
                handleSidestep(character) &&
                handleDrift(character) &&
                handleFastFall(character) &&
                handleNeutralSpecial(character);
    }

    private boolean handleJump(Character character) {
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        CharacterDataComponent characterData = character.getComponent(CharacterDataComponent.class);
        StateComponent state = character.getComponent(StateComponent.class);

        boolean jumpChanged = controller.hasChangedSinceLastFrame(GameInput.Jump);
        int jumpCount = characterData.getActiveAttributes().getNumberOfJumps();

        if (jumpChanged && jumpCount > 0) {
            if (controller.isPressed(GameInput.Jump)) {
                characterData.getActiveAttributes().decrementJumps();
                state.enterState(State.JUMPSQUAT);
            }
        }

        return true;
    }

    private boolean handleSidestep(Character character) {
        PhysicalComponent physical = character.getComponent(PhysicalComponent.class);
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        StateComponent state = character.getComponent(StateComponent.class);
        CharacterDataComponent characterData = character.getComponent(CharacterDataComponent.class);

        boolean performedAirDodge = controller.isPressed(GameInput.Shield) &&
                characterData.getActiveAttributes().getNumberOfAirDodges() > 0;

        if (performedAirDodge) {
            state.enterState(State.AIRDODGE);
            characterData.getActiveAttributes().decrementAirDodges();
            characterData.getActiveAttributes().setNumberOfJumps(0);

            physical.velocity.x = 0;
            physical.velocity.y = 0;

            // TODO: Compute stick-axis velocity vector, for now just do axis aligned directional air-dodges
            if (controller.isPressed(GameInput.ControlLeftHard) || controller.isPressed(GameInput.ControlLeftLight)) {
                state.enterState(State.DIRECTIONAL_AIRDODGE);
                physical.velocity.x = -characterData.getActiveAttributes().getAirDodgeVelocity();
            }

            if (controller.isPressed(GameInput.ControlRightHard) || controller.isPressed(GameInput.ControlRightLight)) {
                state.enterState(State.DIRECTIONAL_AIRDODGE);
                physical.velocity.x = characterData.getActiveAttributes().getAirDodgeVelocity();
            }

            if (controller.isPressed(GameInput.Jump)) {
                state.enterState(State.DIRECTIONAL_AIRDODGE);
                physical.velocity.y = characterData.getActiveAttributes().getAirDodgeVelocity();
            }

            if (controller.isPressed(GameInput.Crouch)) {
                state.enterState(State.DIRECTIONAL_AIRDODGE);
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
        StateComponent stateComponent = character.getComponent(StateComponent.class);

        if (stateComponent.inState(State.DIRECTIONAL_AIRDODGE)) {
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

    private boolean handleNeutralSpecial(Character character) {
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        StateComponent state = character.getComponent(StateComponent.class);

        if(!state.inSubState(State.SUBSTATE_ATTACKING)) {
            if (controller.isPressed(GameInput.NeutralSpecial)) {
                character.startAttack(new ProjectileAttack(character,-30 , 30));
                return false;
            }
        }

        return true;
    }
}
