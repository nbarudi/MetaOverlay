package ca.bungo.utility;

import ca.bungo.MetaOverlay;
import ca.bungo.utility.config.ModConfigs;
import com.google.gson.Gson;
import com.mojang.authlib.minecraft.client.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NetworkUtility {

    //To Be Fetched with Configs
    private static final String NOTE_API_URL = ModConfigs.API_URL;
    private static final String PASSWORD = ModConfigs.API_PASSWORD;

    private static final Gson gson = new Gson();

    public enum NoteType{
        STAGE, PLAYER;
    }

    public static CompletableFuture<List<String>> getTypedNotes(NoteType noteType, String data) throws URISyntaxException, IOException, InterruptedException{
        try(HttpClient client = HttpClient.newHttpClient()){
            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(new URI(NOTE_API_URL + "/" + noteType.name().toUpperCase() + "/" + data + "?password=" + PASSWORD))
                    .GET()
                    .timeout(Duration.ofMinutes(5))
                    .build();
            return client.sendAsync(postRequest, HttpResponse.BodyHandlers.ofString()).thenApply(
                    HttpResponse::body
            ).thenApply((body) -> {
                MetaOverlay.LOGGER.info(body);
                if(body.contains("error code:"))
                    return List.of();
                return Arrays.asList(gson.fromJson(body, String[].class));
            });
        }
    }

}
