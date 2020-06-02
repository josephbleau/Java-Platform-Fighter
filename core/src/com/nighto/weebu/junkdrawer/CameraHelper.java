package com.nighto.weebu.junkdrawer;

import com.badlogic.gdx.math.Vector2;
import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.entity.character.Character;

import java.util.List;

public class CameraHelper {
    public static Vector2 positionalAverageOfCharacters(List<Character> characters) {
        Vector2 average = characters.stream()
                .map(c -> ((PhysicalComponent) c.getComponent(PhysicalComponent.class)).position)
                .reduce(new Vector2(), Vector2::add);

        return new Vector2(average.x * 1/characters.size(), average.y * 1/characters.size());
    }
}
