package com.nighto.weebu.system.inputhandlers;

import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.character.CharacterState;

public class NoInputInputHandler extends StateBasedInputHandler {

    public NoInputInputHandler() {
        super(new CharacterState[]{CharacterState.STATE_SIDESTEPPING, CharacterState.STATE_JUMPSQUAT});
    }

    @Override
    protected boolean doHandleInput(Character character) {
        return false;
    }
}
