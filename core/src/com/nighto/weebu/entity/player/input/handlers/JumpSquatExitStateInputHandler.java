package com.nighto.weebu.entity.player.input.handlers;

import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.controller.GameController;
import com.nighto.weebu.controller.GameInput;
import com.nighto.weebu.entity.player.Player;
import com.nighto.weebu.entity.player.State;
import com.nighto.weebu.entity.player.input.StateInputHandler;

public class JumpSquatExitStateInputHandler extends StateInputHandler {

    public JumpSquatExitStateInputHandler(Player player) {
        super(
                player,
                new State[]{State.EXIT_JUMPSQUAT}
        );
    }

    @Override
    protected boolean doHandleInput(GameController gameController) {
        PhysicalComponent physicalComponent = getPlayer().getComponent(PhysicalComponent.class);

        enterState(State.AIRBORNE);

        if (getPlayer().getJumpCount() < getPlayer().getCharacterData().getAttributes().getNumberOfJumps()){
            if (gameController.isPressed(GameInput.Jump)) {
                physicalComponent.velocity.y = (getPlayer().getCharacterData().getAttributes().getFullHopSpeed());
            } else {
                physicalComponent.velocity.y = (getPlayer().getCharacterData().getAttributes().getShortHopSpeed());
            }
        }

        return true;
    }
}
