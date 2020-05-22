package com.nighto.weebu.system.inputhandlers;

import com.nighto.weebu.component.CharacterDataComponent;
import com.nighto.weebu.component.ControllerComponent;
import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.component.StateComponent;
import com.nighto.weebu.controller.GameInput;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.player.State;

public class JumpSquatExitInputHandler extends StateBasedInputHandler {

    public JumpSquatExitInputHandler() {
        super(new State[]{State.EXIT_JUMPSQUAT});
    }

    @Override
    protected boolean doHandleInput(Character character) {
        PhysicalComponent physical = character.getComponent(PhysicalComponent.class);
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        CharacterDataComponent characterData = character.getComponent(CharacterDataComponent.class);
        StateComponent state = character.getComponent(StateComponent.class);

        state.enterState(State.AIRBORNE);

        if (characterData.getActiveAttributes().getNumberOfJumps() > 0){
            if (controller.isPressed(GameInput.Jump)) {
                physical.velocity.y = characterData.getActiveAttributes().getFullHopSpeed();
            } else {
                physical.velocity.y = characterData.getActiveAttributes().getShortHopSpeed();
            }
        }

        return true;
    }
}
