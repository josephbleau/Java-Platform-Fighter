package com.nighto.weebu.system.inputhandlers;

import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.component.character.ControllerComponent;
import com.nighto.weebu.component.character.CharacterStateComponent;
import com.nighto.weebu.controller.GameInput;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.character.CharacterState;
import com.nighto.weebu.entity.stage.Stage;
import com.nighto.weebu.entity.stage.parts.Platform;

public class CrouchingInputHandler extends StateBasedInputHandler {

    public CrouchingInputHandler() {
        super(new CharacterState[]{CharacterState.STATE_CROUCHING}, CharacterState.concat(CharacterState.ATTACKING_STATES, new CharacterState[]{CharacterState.SUBSTATE_KNOCKBACK, CharacterState.SUBSTATE_TUMBLE}));
    }

    @Override
    protected boolean doHandleInput(Character character) {
        ControllerComponent controller = character.getComponent(ControllerComponent.class);
        CharacterStateComponent state = character.getComponent(CharacterStateComponent.class);
        PhysicalComponent physical = character.getComponent(PhysicalComponent.class);

        if(!controller.isPressed(GameInput.Crouch) && !state.inSubState(CharacterState.ATTACKING_STATES)) {
            Stage stage = character.getStage();
            Platform standingOn = stage.ifPlatformGetPlatform(physical.floorStandingOn);

            if (standingOn.passThru) {
                physical.move(physical.position.x, physical.position.y - 1);
                physical.floorStandingOn = null;
                state.enterState(CharacterState.STATE_AIRBORNE);
            } else {
                state.enterState(CharacterState.STATE_STANDING);
            }
        } else {
            if (controller.isPressed(GameInput.NeutralAttack)) {
                state.enterSubState(CharacterState.SUBSTATE_ATTACKING_DOWN_TILT);
                return false;
            }
        }

        return true;
    }
}
