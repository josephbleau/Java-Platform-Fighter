package com.nighto.weebu.entity.character;

import com.badlogic.gdx.math.Rectangle;
import com.nighto.weebu.entity.player.State;

import java.util.Map;

public class CharacterData {
    private CharacterAttributes attributes;
    private Map<State, Rectangle> hurtboxes;

    public CharacterAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(CharacterAttributes attributes) {
        this.attributes = attributes;
    }

    public Map<State, Rectangle> getHurtboxes() {
        return hurtboxes;
    }

    public void setHurtboxes(Map<State, Rectangle> hurtboxes) {
        this.hurtboxes = hurtboxes;
    }
}
