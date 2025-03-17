package ca.bungo.renderer.data;

import ca.bungo.MetaOverlay;
import ca.bungo.utility.NetworkUtility;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerData {

    String playerUsername;
    String playerUUID;

    final List<String> playerNotes;

    public PlayerData(String playerUsername, String playerUUID) {
        this.playerUsername = playerUsername;
        this.playerUUID = playerUUID;

        playerNotes = new ArrayList<>();
        new Thread(this::fetchPlayerNotes).start();
    }

    public String getPlayerUsername() {
        return playerUsername;
    }

    public void setPlayerUsername(String playerUsername) {
        this.playerUsername = playerUsername;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(String playerUUID) {
        this.playerUUID = playerUUID;
    }

    private void fetchPlayerNotes() {
        try {
            NetworkUtility.getTypedNotes(NetworkUtility.NoteType.PLAYER, playerUUID).thenAccept((notes) -> {
                synchronized (playerNotes) {
                    if(playerNotes.isEmpty()) {
                        playerNotes.addAll(notes);
                    } else {
                        playerNotes.clear();
                        playerNotes.addAll(notes);
                    }
                }
            });
        } catch (URISyntaxException | IOException | InterruptedException e) {
            MetaOverlay.LOGGER.error(e.getMessage());
            MetaOverlay.LOGGER.error(Arrays.toString(e.getStackTrace()));
            MetaOverlay.LOGGER.info("Failed to fetch player notes from {}", playerUUID);
        }
    }

    public List<String> getPlayerNotes() {
        synchronized (playerNotes) {
            return playerNotes;
        }
    }
}
