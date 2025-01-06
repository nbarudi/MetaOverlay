package ca.bungo.renderer.data;

import ca.bungo.utility.NetworkUtility;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class PlayerData {

    String playerUsername;
    String playerUUID;

    final List<String> playerNotes;
    private String hash = null;

    public PlayerData(String playerUsername, String playerUUID) {
        this.playerUsername = playerUsername;
        this.playerUUID = playerUUID;

        playerNotes = new ArrayList<>();
        fetchPlayerNotes();
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
            NetworkUtility.getHash(NetworkUtility.NoteType.PLAYER).thenAccept(hash -> {
               if(this.hash == null || !this.hash.equals(hash)) {
                   try {
                       this.hash = hash;
                       playerNotes.clear();
                       NetworkUtility.getTypedNotes(NetworkUtility.NoteType.PLAYER, playerUUID).thenAccept(playerNotes::addAll);
                   } catch (URISyntaxException | IOException | InterruptedException e) {
                       throw new RuntimeException(e);
                   }
               }
            });
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getPlayerNotes() {
        synchronized (playerNotes) {
            return playerNotes;
        }
    }
}
