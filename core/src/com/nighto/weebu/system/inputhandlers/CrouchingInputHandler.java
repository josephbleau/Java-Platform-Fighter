package com.nighto.weebu.system.inputhandlers;

import com.nighto.weebu.component.ControllerComponent;
import com.nighto.weebu.component.StateComponent;
import com.nighto.weebu.controller.GameInput;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.character.State;

public class CrouchingInputHandler extends StateBasedInputHandler {

    public CrouchingInputHandler() {
        super(new State[]{State.CROUCHING}, new State[]{State.SUBSTATE_KNOCKBACK});
    }

    @Override
    protected boolean doHandleInput(Character character) {
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        StateComponent state = character.getComponent(StateComponent.class);

        if(!controller.isPressed(GameInput.Crouch)) {
            state.enterState(State.STANDING);

            return false;
        }

        return true;
    }
}
