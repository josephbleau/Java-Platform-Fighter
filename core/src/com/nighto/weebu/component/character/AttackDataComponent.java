package com.nighto.weebu.component.character;

import com.google.gson.Gson;
import com.nighto.weebu.component.Component;
import com.nighto.weebu.entity.attack.AttackData;
import com.nighto.weebu.entity.character.loader.Characters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class AttackDataComponent extends Component {
    public Map<String, AttackData> attacks;

    public static AttackDataComponent loadAttackDataComponent(Characters character) {
        try {
            Gson gson = new Gson();
            String attackJsonFile = new String(Files.readAllBytes(Paths.get("core/assets/characters/" + character.name + "/attacks.json")));

            return gson.fromJson(attackJsonFile, AttackDataComponent.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
