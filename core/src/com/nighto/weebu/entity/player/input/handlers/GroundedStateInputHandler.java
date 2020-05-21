package com.nighto.weebu.entity.player.input.handlers;

import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.controller.GameController;
import com.nighto.weebu.controller.GameInput;
import com.nighto.weebu.entity.attack.MeleeAttack;
import com.nighto.weebu.entity.attack.ProjectileAttack;
import com.nighto.weebu.entity.player.Player;
import com.nighto.weebu.entity.player.State;
import com.nighto.weebu.entity.player.input.StateInputHandler;

public class GroundedStateInputHandler extends StateInputHandler {

    public GroundedStateInputHandler(Player player) {
        super(
                player,
                new State[] {
                        State.DEFAULT,
                        State.STANDING,
                        State.RUNNING
                },
                new State[] {
                        State.SUBSTATE_ATTACKING,
                        State.SUBSTATE_KNOCKBACK
                }
        );
    }

    @Override
    protected boolean doHandleInput(GameController gameController) {
        return handleShield(gameController) &&
                handleRun(gameController) &&
                handleCrouch(gameController) &&
                handleJump(gameController) &&
                handleNeutralSpecial(gameController) &&
                handleNeutralAttack(gameController);
    }

    private boolean handleShield(GameController gameController) {
        PhysicalComponent physicalComponent = getPlayer().getComponent(PhysicalComponent.class);

        if (gameController.isPressed(GameInput.Shield)) {
            getPlayer().spawnShield();
            physicalComponent.velocity.x = 0;

            enterState(State.SHIELDING);

            return false;
        }

        return true;
    }

    private boolean handleRun(GameController gameController) {
        PhysicalComponent physicalComponent = getPlayer().getComponent(PhysicalComponent.class);

        if (gameController.isPressed(GameInput.ControlLeftLight)) {
            getPlayer().setActiveControl(true);
            physicalComponent.velocity.x = (-getPlayer().getCharacterData().getAttributes().getGroundSpeed());
            enterState(State.RUNNING);
        } else if (gameController.isPressed(GameInput.ControlRightLight)) {
            getPlayer().setActiveControl(true);
            physicalComponent.velocity.x = (getPlayer().getCharacterData().getAttributes().getGroundSpeed());
            enterState(State.RUNNING);
        } else {
            getPlayer().setActiveControl(false);
            enterState(State.STANDING);
        }

        return true;
    }

    private boolean handleCrouch(GameController gameController) {
        PhysicalComponent physicalComponent = getPlayer().getComponent(PhysicalComponent.class);

        if (gameController.isPressed(GameInput.Crouch)) {
            physicalComponent.velocity.x = 0;
            enterState(State.CROUCHING);
        }

        return true;
    }

    private boolean handleJump(GameController gameController) {
        if (gameController.hasChangedSinceLastFrame(GameInput.Jump) && gameController.isPressed(GameInput.Jump)) {
            getPlayer().setJumpCount(1);
            getPlayer().resetTimers();
            enterState(State.JUMPSQUAT);

            return false;
        }

        return true;
    }

    private boolean handleNeutralAttack(GameController gameController) {
        PhysicalComponent physicalComponent = getPlayer().getComponent(PhysicalComponent.class);

        if (!inSubState(State.SUBSTATE_ATTACKING)) {
            if (gameController.isPressed(GameInput.NeutralAttack)) {
                float xOffsetDirectionMultiplier = (getPlayer().getFacingRight()) ? 1 : -1;
                float xOffset = 30 * xOffsetDirectionMultiplier;

                float xImpulseModifier = getPlayer().getFacingRight() ? 1 : -1;
                float xImpulse = 10 * xImpulseModifier;
                float yImpulse = 20;
                float knockbackInduced = 10f/60f;

                getPlayer().startAttack(
                        new MeleeAttack(
                                getPlayer(),
                                xOffset,
                                30,
                                knockbackInduced,
                                xImpulse, yImpulse
                        )
                );

                physicalComponent.velocity.x = 0;
            }
        }

        return true;
    }

    private boolean handleNeutralSpecial(GameController gameController) {
        PhysicalComponent physicalComponent = getPlayer().getComponent(PhysicalComponent.class);

        if (!inSubState(State.SUBSTATE_ATTACKING)) {
            if (gameController.isPressed(GameInput.NeutralSpecial)) {
                getPlayer().startAttack(new ProjectileAttack(getPlayer(), 0, 30));
                physicalComponent.velocity.x = 0;
            }
        }

        return true;
    }
}
