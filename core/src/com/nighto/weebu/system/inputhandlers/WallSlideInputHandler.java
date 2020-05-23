package com.nighto.weebu.system.inputhandlers;

import com.nighto.weebu.component.CharacterDataComponent;
import com.nighto.weebu.component.ControllerComponent;
import com.nighto.weebu.component.StateComponent;
import com.nighto.weebu.controller.GameInput;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.character.State;

public class WallSlideInputHandler extends StateBasedInputHandler {
    public WallSlideInputHandler() {
        super(new State[]{State.WALLSLIDING});
    }

    @Override
    public boolean doHandleInput(Character character) {
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        CharacterDataComponent characterData = character.getComponent(CharacterDataComponent.class);
        StateComponent state = character.getComponent(StateComponent.class);

        characterData.getActiveAttributes().setNumberOfJumps(characterData.getInitialAttributes().getNumberOfJumps());

        if (controller.isPressed(GameInput.Jump)) {
            state.enterState(State.JUMPSQUAT);
            return true;
        }

        if (state.inSubState(State.SUBSTATE_WALLSLIDING_RIGHT)) {
            if (controller.isPressed(GameInput.ControlRightLight) ||
                    controller.isPressed(GameInput.ControlRightHard)) {
                return false;
            }
        }

        if (state.inSubState(State.SUBSTATE_WALLSLIDING_LEFT)) {
            if (controller.isPressed(GameInput.ControlLeftLight) ||
                    controller.isPressed(GameInput.ControlLeftHard)) {
                return false;
            }
        }

        state.enterState(State.AIRBORNE);
        return true;
    }
}
