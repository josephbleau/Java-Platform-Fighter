package com.nighto.weebu.entity.attack;

import com.badlogic.gdx.math.Vector2;
import com.nighto.weebu.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class AttackData {
    public float damage;
    public float animSpeed;
    public boolean usesPlayerDirection;
    public Vector2 knockback;

    /** List of entities that this attack has already collided with to prevent the same
     * entity from being affected by the same attack more than once. If a move should hiot
     * multiple times then it needs to have multiple hitboxes that link.
     *
     * This is cleared every time this attack is spawned
     *
     * TODO: Gonna need to keep track of this per character, because if multiple players of the same type
     * TODO: select the same character they may share AttackData objects and will not be able to both attack the same
     * TODO: player with the same hitbox from the same move at the same time (edge case for sure).
     *
     * ^^ Thats never gonna get fixed. :P
     */
    public List<Entity> hasCollidedWith = new ArrayList<>();
}
