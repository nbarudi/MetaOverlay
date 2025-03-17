package ca.bungo.utility.config;

import ca.bungo.MetaOverlay;
import com.mojang.datafixers.util.Pair;

public class ModConfigs {

    public static SimpleConfig CONFIG;
    public static String API_URL;
    public static String API_PASSWORD;
    public static int ACTIVE_PLAYER_SECONDS;

    public static void registerConfigs() {
        CONFIG = SimpleConfig.of(MetaOverlay.MOD_ID + "_config").provider(ModConfigs::provider).request();
        loadConfigs();
    }

    private static String provider(String filename) {
        return """
                #Simple configrable data for the MetaOverlay mod
                api.url=http://localhost:5000/notes
                api.password=password
                render.active_player_seconds=15
                """;

    }

    private static void loadConfigs() {
        API_URL = CONFIG.getOrDefault("api.url", "http://localhost:5000/notes");
        API_PASSWORD = CONFIG.getOrDefault("api.password", "password");
        ACTIVE_PLAYER_SECONDS = CONFIG.getOrDefault("render.active_player_seconds", 15);
    }

}
