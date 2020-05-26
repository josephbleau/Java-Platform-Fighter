package com.nighto.weebu.system.inputhandlers;

import com.nighto.weebu.component.character.ControllerComponent;
import com.nighto.weebu.component.character.StateComponent;
import com.nighto.weebu.controller.GameInput;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.character.State;

public class ShieldStateInputHandler extends StateBasedInputHandler {

    public ShieldStateInputHandler() {
        super(new State[]{State.SHIELDING});
    }

    @Override
    public boolean doHandleInput(Character character) {
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        StateComponent state = character.getComponent(StateComponent.class);

        if (controller.isPressed(GameInput.Sidestep)) {
            state.enterState(State.SIDESTEPPING);
        } else if (controller.isPressed(GameInput.Jump)) {
            state.enterState(State.JUMPSQUAT);
        } else if (!controller.isPressed(GameInput.Shield)) {
            state.enterState(State.STANDING);
        }

        return false;
    }
}
