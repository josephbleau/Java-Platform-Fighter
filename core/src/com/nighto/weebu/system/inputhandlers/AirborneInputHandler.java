package com.nighto.weebu.system.inputhandlers;

import com.nighto.weebu.component.CharacterDataComponent;
import com.nighto.weebu.component.ControllerComponent;
import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.component.StateComponent;
import com.nighto.weebu.controller.GameInput;
import com.nighto.weebu.entity.attack.ProjectileAttack;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.player.State;

public class AirborneInputHandler extends StateBasedInputHandler {

    public AirborneInputHandler() {
        super(new State[]{State.AIRBORNE}, new State[]{State.SUBSTATE_KNOCKBACK});
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
                characterData.getActiveAttributes().setNumberOfJumps(jumpCount-1);
                state.enterState(State.JUMPSQUAT);
            }
        } else {
            // TODO: Contemplate reset method / not sure if I even like this entire attribute structure...
            characterData.getActiveAttributes().setNumberOfJumps(characterData.getInitialAttributes().getNumberOfJumps());
        }

        return true;
    }

    private boolean handleSidestep(Character character) {
        PhysicalComponent physical = character.getComponent(PhysicalComponent.class);
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        StateComponent state = character.getComponent(StateComponent.class);

        if (controller.isPressed(GameInput.Shield)) {
            physical.velocity.x = 0;
            state.enterState(State.SIDESTEPPING);

            return false;
        }

        return true;
    }

    private boolean handleDrift(Character character) {
        PhysicalComponent physical = character.getComponent(PhysicalComponent.class);
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        CharacterDataComponent characterData = character.getComponent(CharacterDataComponent.class);

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
                character.startAttack(new ProjectileAttack(character.getGameContext(), character,-30 , 30));
                return false;
            }
        }

        return true;
    }
}
