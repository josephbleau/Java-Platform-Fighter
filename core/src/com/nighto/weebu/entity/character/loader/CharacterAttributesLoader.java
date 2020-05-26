package com.nighto.weebu.entity.character.loader;

import com.badlogic.gdx.math.Rectangle;
import com.google.gson.Gson;
import com.nighto.weebu.component.character.CharacterDataComponent;
import com.nighto.weebu.component.character.InitialCharacterAttributes;
import com.nighto.weebu.entity.character.State;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class CharacterAttributesLoader {
    private CharacterAttributesLoader() {}

    public static CharacterDataComponent loadCharacterData() {
        Gson gson = new Gson();

        try {
            String attributesJsonFile = new String(Files.readAllBytes(Paths.get("core/assets/characters/sunflower/attributes.json")));
            String hurtboxesJsonFile = new String(Files.readAllBytes(Paths.get("core/assets/characters/sunflower/hurtboxes.json")));

            InitialCharacterAttributes initialCharacterAttributes = gson.fromJson(attributesJsonFile, InitialCharacterAttributes.class);
            Map<State, Rectangle> hurtboxes = gson.fromJson(hurtboxesJsonFile, Map.class);

            return new CharacterDataComponent(initialCharacterAttributes, hurtboxes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
