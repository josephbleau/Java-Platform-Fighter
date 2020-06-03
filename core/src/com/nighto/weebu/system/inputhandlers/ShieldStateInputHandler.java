package com.nighto.weebu.system.inputhandlers;

import com.nighto.weebu.component.character.CharacterStateComponent;
import com.nighto.weebu.component.character.ControllerComponent;
import com.nighto.weebu.controller.GameInput;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.character.CharacterState;

public class ShieldStateInputHandler extends StateBasedInputHandler {

    public ShieldStateInputHandler() {
        super(new CharacterState[]{CharacterState.STATE_SHIELDING});
    }

    @Override
    public boolean doHandleInput(Character character) {
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        CharacterStateComponent state = character.getComponent(CharacterStateComponent.class);

        if (controller.isPressed(GameInput.Sidestep)) {
            state.enterState(CharacterState.STATE_SIDESTEPPING);
        } else if (controller.isPressed(GameInput.Jump)) {
            state.enterState(CharacterState.STATE_JUMPSQUAT);
        } else if (!controller.isPressed(GameInput.Shield)) {
            state.enterState(CharacterState.STATE_STANDING);
        }

        return false;
    }
}
