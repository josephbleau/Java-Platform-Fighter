package com.nighto.weebu.entity.character;

public enum State {
    DEFAULT,                  // Neutral state. State is cleared to this at the beginning of each input loop.
    SUBSTATE_DEFAULT,
    STANDING,               // Standing still on the level
    RUNNING,                // Moving on the ground
    CROUCHING,              // Player is crouched
    JUMPSQUAT,              // Preparing to jump (enter this state when jumping from the ground, leave it when jump happens)
    EXIT_JUMPSQUAT,         // Player is exiting jumpsquat, next input poll pass will determine short or full hop
    AIRBORNE,               // In the air
    SHIELDING,              // Still, in shield
    HANGING,                // Hanging from a ledge
    WALLSLIDING,            // Sliding down a wall
    SIDESTEPPING,           // Player is invincible during a sidestep (dodge)
    AIRDODGE,               // Player sidestepped in the air
    DIRECTIONAL_AIRDODGE,   // Player sidestepped in the air while holding a direction
    SUBSTATE_ATTACKING,     // Player is in an attack animation
    SUBSTATE_HANGING_LEFT,  // Player is hanging from a ledge on the left
    SUBSTATE_HANGING_RIGHT, // Player is hanging from a ledge on the right
    SUBSTATE_KNOCKBACK,     // Player cannot act while in knockback, exit knockback on landing (for now)
    SUBSTATE_TUMBLE,        // Player enters tumble if knockback stun induced is greater than equal 500ms
    SUBSTATE_WALLSLIDING_LEFT,
    SUBSTATE_WALLSLIDING_RIGHT;
}
