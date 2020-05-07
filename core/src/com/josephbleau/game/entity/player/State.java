package com.josephbleau.game.entity.player;

public enum State {
    CLEAR,                  // Neutral state. State is cleared to this at the beginning of each input loop.
    SUBSTATE_CLEAR,
    STANDING,               // Standing still on the level
    RUNNING,                // Moving on the ground
    AIRBORNE,               // In the air
    SHIELDING,              // Still, in shield
    HANGING,                // Hanging from a ledge
    SUBSTATE_HANGING_LEFT,
    SUBSTATE_HANGING_RIGHT
}
