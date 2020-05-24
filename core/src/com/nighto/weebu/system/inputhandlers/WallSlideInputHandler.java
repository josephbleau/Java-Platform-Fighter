package com.nighto.weebu.system.inputhandlers;

import com.nighto.weebu.component.CharacterDataComponent;
import com.nighto.weebu.component.ControllerComponent;
import com.nighto.weebu.component.PhysicalComponent;
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
        PhysicalComponent physical = character.getComponent(PhysicalComponent.class);

        characterData.getActiveAttributes().setNumberOfJumps(characterData.getInitialAttributes().getNumberOfJumps());

        if (controller.isPressed(GameInput.Jump)) {
            state.enterState(State.JUMPSQUAT);
            return true;
        }

        if (physical.wallSlidingOn == null) {
            state.enterState(State.AIRBORNE, State.SUBSTATE_DEFAULT);
            return true;
        }

        // Ensure we're still touching the wall, which means we're between the top and bottom of it
        // or in instances where the stage is shorter than the player, it's between the players
        // top and bottom.
        float stageTop = physical.wallSlidingOn.y + physical.wallSlidingOn.height;
        float stageBottom = physical.wallSlidingOn.y;
        float playerTop = physical.position.y + physical.boundingBox.height;
        float playerBottom = physical.position.y;

        boolean riding =
                (stageTop < playerTop && stageTop > playerBottom) ||
                (stageBottom < playerTop && stageBottom > playerBottom) ||
                (playerTop < stageTop && playerTop > stageBottom) ||
                (playerBottom < stageTop && playerBottom > stageBottom);

        if (riding) {
            boolean holdingLeft = controller.isPressed(GameInput.ControlLeftLight) || controller.isPressed(GameInput.ControlLeftHard);
            boolean holdingRight = controller.isPressed(GameInput.ControlRightLight) || controller.isPressed(GameInput.ControlRightHard);

            if (state.inSubState(State.SUBSTATE_WALLSLIDING_RIGHT)) {
                if (holdingLeft) {
                    state.enterState(State.AIRBORNE, State.SUBSTATE_DEFAULT);
                    return true;
                }

                if (holdingRight) {
                    return false;
                }
            }

            if (state.inSubState(State.SUBSTATE_WALLSLIDING_LEFT)) {
                if (holdingRight) {
                    state.enterState(State.AIRBORNE, State.SUBSTATE_DEFAULT);
                    return true;
                }

                if (holdingLeft) {
                    return false;
                }
            }
        }

        state.enterState(State.AIRBORNE, State.SUBSTATE_DEFAULT);
        return true;
    }
}
