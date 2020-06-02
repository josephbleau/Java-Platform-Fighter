package com.nighto.weebu.system.inputhandlers;

import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.component.character.CharacterDataComponent;
import com.nighto.weebu.component.character.ControllerComponent;
import com.nighto.weebu.component.character.CharacterStateComponent;
import com.nighto.weebu.controller.GameInput;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.character.CharacterState;

public class JumpSquatExitInputHandler extends StateBasedInputHandler {

    public JumpSquatExitInputHandler() {
        super(new CharacterState[]{CharacterState.STATE_EXIT_JUMPSQUAT});
    }

    @Override
    protected boolean doHandleInput(Character character) {
        PhysicalComponent physical = character.getComponent(PhysicalComponent.class);
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        CharacterDataComponent characterData = character.getComponent(CharacterDataComponent.class);
        CharacterStateComponent state = character.getComponent(CharacterStateComponent.class);

        state.enterState(CharacterState.STATE_AIRBORNE);

        if (characterData.getActiveAttributes().getNumberOfJumps() >= 0){
            physical.floorStandingOn = null;

            if (controller.isPressed(GameInput.Jump)) {
                physical.velocity.y = characterData.getActiveAttributes().getFullHopSpeed();
            } else {
                physical.velocity.y = characterData.getActiveAttributes().getShortHopSpeed();
            }
        }

        return true;
    }
}
