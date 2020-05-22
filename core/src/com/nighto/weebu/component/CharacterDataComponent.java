package com.nighto.weebu.component;

import com.badlogic.gdx.math.Rectangle;
import com.nighto.weebu.component.character.ActiveCharacterAttributes;
import com.nighto.weebu.component.character.CharacterTimers;
import com.nighto.weebu.component.character.InitialCharacterAttributes;
import com.nighto.weebu.entity.player.State;

import java.util.Map;

/**
 * Character-specific data attributes
 */
public class CharacterDataComponent extends Component {
    private InitialCharacterAttributes initialAttributes;
    private ActiveCharacterAttributes activeAttributes;

    private CharacterTimers timers;
    private Map<State, Rectangle> hurtboxes;

    public CharacterDataComponent(InitialCharacterAttributes initialAttributes, Map<State, Rectangle> hurtboxes) {
        this.initialAttributes = initialAttributes;
        this.activeAttributes = new ActiveCharacterAttributes(initialAttributes);
        this.hurtboxes = hurtboxes;
        this.timers = new CharacterTimers(this); // sus
    }

    public ActiveCharacterAttributes getActiveAttributes() {
        return activeAttributes;
    }

    public InitialCharacterAttributes getInitialAttributes() {
        return initialAttributes;
    }

    public CharacterTimers getTimers() {
        return timers;
    }

    public Map<State, Rectangle> getHurtboxes() {
        return hurtboxes;
    }
}
