package com.nighto.weebu.entity.character;

import java.util.Arrays;
import java.util.stream.Stream;

public enum CharacterState {
    STATE_DEFAULT,                            // Neutral state. State is cleared to this at the beginning of each input loop.
    STATE_RUNNING,                            // Moving on the ground
    STATE_CROUCHING,                          // Player is crouched
    STATE_JUMPSQUAT,                          // Preparing to jump (enter this state when jumping from the ground, leave it when jump happens)
    STATE_EXIT_JUMPSQUAT,                     // Player is exiting jumpsquat, next input poll pass will determine short or full hop
    STATE_AIRBORNE,                           // In the air
    STATE_SHIELDING,                          // Still, in shield
    STATE_HANGING,                            // Hanging from a ledge
    STATE_WALLSLIDING,                        // Sliding down a wall
    STATE_SIDESTEPPING,                       // Player is invincible during a sidestep (dodge)
    STATE_AIRDODGE,                           // Player sidestepped in the air
    STATE_DIRECTIONAL_AIRDODGE,               // Player sidestepped in the air while holding a direction
    STATE_STANDING,                           // Standing still on the level
    SUBSTATE_DEFAULT,
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

    public static CharacterState[] ATTACKING_STATES = new CharacterState[] {
            CharacterState.SUBSTATE_ATTACKING_NEUTRAL_NORMAL,
            CharacterState.SUBSTATE_ATTACKING_NEUTRAL_SPECIAL,
            CharacterState.SUBSTATE_ATTACKING_DASH_ATTACK,
            CharacterState.SUBSTATE_ATTACKING_NEUTRAL_AIR,
            CharacterState.SUBSTATE_ATTACKING_UP_TILT,
            CharacterState.SUBSTATE_ATTACKING_DOWN_TILT
    };

    public static CharacterState[] KNOCKBACK_STATES = new CharacterState[] {
            CharacterState.SUBSTATE_KNOCKBACK,
            CharacterState.SUBSTATE_TUMBLE
    };

    public static CharacterState[] concat(CharacterState[] s1, CharacterState[] s2) {
        return Stream.concat(Arrays.stream(s1), Arrays.stream(s2)).toArray(CharacterState[]::new);
    }
}
