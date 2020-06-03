package com.nighto.weebu.system.inputhandlers;

import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.component.character.CharacterDataComponent;
import com.nighto.weebu.component.character.CharacterStateComponent;
import com.nighto.weebu.component.character.ControllerComponent;
import com.nighto.weebu.controller.GameInput;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.character.CharacterState;

public class HangingInputHandler extends StateBasedInputHandler {

    public HangingInputHandler() {
        super(new CharacterState[]{CharacterState.STATE_HANGING});
    }

    @Override
    protected boolean doHandleInput(Character character) {
        PhysicalComponent physical = character.getComponent(PhysicalComponent.class);
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        CharacterDataComponent characterData = character.getComponent(CharacterDataComponent.class);
        CharacterStateComponent state = character.getComponent(CharacterStateComponent.class);

        if (controller.isPressed(GameInput.Jump)) {
            physical.velocity.y = (characterData.getActiveAttributes().getFullHopSpeed());

            state.enterState(CharacterState.STATE_AIRBORNE, CharacterState.SUBSTATE_DEFAULT);
        }

        return true;
    }
}
