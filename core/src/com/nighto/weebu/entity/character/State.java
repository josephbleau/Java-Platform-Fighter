package com.nighto.weebu.entity.character;

import java.util.Arrays;
import java.util.stream.Stream;

public enum State {
    DEFAULT,                            // Neutral state. State is cleared to this at the beginning of each input loop.
    SUBSTATE_DEFAULT,
    STANDING,                           // Standing still on the level
    RUNNING,                            // Moving on the ground
    CROUCHING,                          // Player is crouched
    JUMPSQUAT,                          // Preparing to jump (enter this state when jumping from the ground, leave it when jump happens)
    EXIT_JUMPSQUAT,                     // Player is exiting jumpsquat, next input poll pass will determine short or full hop
    AIRBORNE,                           // In the air
    SHIELDING,                          // Still, in shield
    HANGING,                            // Hanging from a ledge
    WALLSLIDING,                        // Sliding down a wall
    SIDESTEPPING,                       // Player is invincible during a sidestep (dodge)
    AIRDODGE,                           // Player sidestepped in the air
    DIRECTIONAL_AIRDODGE,               // Player sidestepped in the air while holding a direction
    SUBSTATE_HANGING_LEFT,              // Player is hanging from a ledge on the left
    SUBSTATE_HANGING_RIGHT,             // Player is hanging from a ledge on the right
    SUBSTATE_KNOCKBACK,                 // Player cannot act while in knockback, exit knockback on landing (for now)
    SUBSTATE_TUMBLE,                    // Player enters tumble if knockback stun induced is greater than equal 500ms
    SUBSTATE_WALLSLIDING_LEFT,
    SUBSTATE_WALLSLIDING_RIGHT,
    SUBSTATE_ATTACKING_NEUTRAL_NORMAL,  // Player is performing a jab
    SUBSTATE_ATTACKING_NEUTRAL_SPECIAL,
    SUBSTATE_ATTACKING_DASH_ATTACK,     // Player is performing a dash attack
    SUBSTATE_ATTACKING_NEUTRAL_AIR,
    SUBSTATE_ATTACKING_UP_TILT,
    SUBSTATE_ATTACKING_DOWN_TILT;

    public static State[] ATTACKING_STATES = new State[] {
            State.SUBSTATE_ATTACKING_NEUTRAL_NORMAL,
            State.SUBSTATE_ATTACKING_NEUTRAL_SPECIAL,
            State.SUBSTATE_ATTACKING_DASH_ATTACK,
            State.SUBSTATE_ATTACKING_NEUTRAL_AIR,
            State.SUBSTATE_ATTACKING_UP_TILT,
            State.SUBSTATE_ATTACKING_DOWN_TILT
    };

    public static State[] KNOCKBACK_STATES = new State[] {
            State.SUBSTATE_KNOCKBACK,
            State.SUBSTATE_TUMBLE
    };

    public static State[] concat(State[] s1, State[] s2) {
        return Stream.concat(Arrays.stream(s1), Arrays.stream(s2)).toArray(State[]::new);
    }
}
