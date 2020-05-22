package com.nighto.weebu.system.inputhandlers;

import com.nighto.weebu.component.CharacterDataComponent;
import com.nighto.weebu.component.ControllerComponent;
import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.component.StateComponent;
import com.nighto.weebu.controller.GameInput;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.player.State;

public class HangingInputHandler extends StateBasedInputHandler {

    public HangingInputHandler() {
        super(new State[]{State.HANGING});
    }

    @Override
    protected boolean doHandleInput(Character character) {
        PhysicalComponent physical = character.getComponent(PhysicalComponent.class);
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        CharacterDataComponent characterData = character.getComponent(CharacterDataComponent.class);
        StateComponent state = character.getComponent(StateComponent.class);

        if (controller.isPressed(GameInput.Jump)) {
            physical.velocity.y = (characterData.getActiveAttributes().getFullHopSpeed());

            state.enterState(State.AIRBORNE, State.SUBSTATE_DEFAULT);
        }

        return true;
    }
}
