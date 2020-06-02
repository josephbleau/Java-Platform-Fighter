package com.nighto.weebu.system.inputhandlers;

import com.nighto.weebu.component.character.ControllerComponent;
import com.nighto.weebu.component.character.StateComponent;
import com.nighto.weebu.controller.GameInput;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.character.State;

public class CrouchingInputHandler extends StateBasedInputHandler {

    public CrouchingInputHandler() {
        super(new State[]{State.STATE_CROUCHING}, State.concat(State.ATTACKING_STATES, new State[]{State.SUBSTATE_KNOCKBACK, State.SUBSTATE_TUMBLE}));
    }

    @Override
    protected boolean doHandleInput(Character character) {
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        StateComponent state = character.getComponent(StateComponent.class);

        if(!controller.isPressed(GameInput.Crouch) && !state.inSubState(State.ATTACKING_STATES)) {
            state.enterState(State.STATE_STANDING);
        } else {
            if (controller.isPressed(GameInput.NeutralAttack)) {
                state.enterSubState(State.SUBSTATE_ATTACKING_DOWN_TILT);
                return false;
            }
        }

        return true;
    }
}
