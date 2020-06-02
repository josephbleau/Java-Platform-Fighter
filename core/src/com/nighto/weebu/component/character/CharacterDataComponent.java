package com.nighto.weebu.component.character;

import com.badlogic.gdx.math.Rectangle;
import com.nighto.weebu.component.Component;
import com.nighto.weebu.entity.character.CharacterState;

import java.util.Map;

/**
 * Character-specific data attributes
 */
public class CharacterDataComponent extends Component {
    private InitialCharacterAttributes initialAttributes;
    private ActiveCharacterAttributes activeAttributes;

    private CharacterTimers timers;
    private Map<CharacterState, Rectangle> hurtboxes;

    public CharacterDataComponent(InitialCharacterAttributes initialAttributes, Map<CharacterState, Rectangle> hurtboxes) {
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

    public Map<CharacterState, Rectangle> getHurtboxes() {
        return hurtboxes;
    }

    @Override
    public CharacterDataComponent save() {
        CharacterDataComponent characterDataComponent = new CharacterDataComponent(initialAttributes, hurtboxes);
        characterDataComponent.activeAttributes.resetValues(activeAttributes);

        CharacterTimers characterTimers = new CharacterTimers(characterDataComponent);
        characterTimers.setKnockbackTimeRemaining(this.timers.getKnockbackTimeRemaining());
        characterTimers.setSidestepTimeRemaining(this.timers.getSidestepTimeRemaining());
        characterDataComponent.timers = characterTimers;

        return characterDataComponent;
    }

    @Override
    public void load(Component component) {
        this.activeAttributes = new ActiveCharacterAttributes(((CharacterDataComponent) component).activeAttributes);

        CharacterTimers characterTimers = new CharacterTimers(this);
        characterTimers.setSidestepTimeRemaining(((CharacterDataComponent) component).getTimers().getSidestepTimeRemaining());
        characterTimers.setKnockbackTimeRemaining(((CharacterDataComponent) component).getTimers().getKnockbackTimeRemaining());

        this.timers = characterTimers;
    }
}
