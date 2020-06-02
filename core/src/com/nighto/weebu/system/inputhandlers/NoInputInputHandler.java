package com.nighto.weebu.system.inputhandlers;

import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.character.State;

public class NoInputInputHandler extends StateBasedInputHandler {

    public NoInputInputHandler() {
        super(new State[]{State.STATE_SIDESTEPPING, State.STATE_JUMPSQUAT});
    }

    @Override
    protected boolean doHandleInput(Character character) {
        return false;
    }
}
