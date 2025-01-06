package ca.bungo.renderer.data;

import ca.bungo.renderer.components.StageNotesComponent;
import ca.bungo.utility.NetworkUtility;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServerData {

    private final List<String> serverData;
    private String hash = null;

    public ServerData() {
        serverData = new ArrayList<>();
    }

    public void updateDataIfNeeded() {
        try {
            NetworkUtility.getHash(NetworkUtility.NoteType.STAGE).thenAccept(hash -> {
                if(this.hash == null || !this.hash.equals(hash)) {
                    StageNotesComponent.isRendered = true;
                    try {
                        synchronized (this) {
                            this.hash = hash;
                            serverData.clear();
                            NetworkUtility.getTypedNotes(NetworkUtility.NoteType.STAGE, "dummy").thenAccept(serverData::addAll);
                        }
                    } catch (URISyntaxException | IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public List<String> getServerData() {
        return serverData;
    }


}
