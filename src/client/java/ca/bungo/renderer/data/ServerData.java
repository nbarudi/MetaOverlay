package ca.bungo.renderer.data;

import ca.bungo.renderer.components.StageNotesComponent;
import ca.bungo.utility.NetworkUtility;
import org.jetbrains.annotations.NotNull;

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

            synchronized (this) {
                NetworkUtility.getTypedNotes(NetworkUtility.NoteType.STAGE, "dummy").thenAccept((result) -> {
                    if(result == null) return;
                    if(beenUpdated(result)) { //Data Been updated? Let's force show then Menu and update the list
                        StageNotesComponent.isRendered = true;
                        serverData.clear();
                        serverData.addAll(result);
                    }
                });
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public List<String> getServerData() {
        return serverData;
    }

    private boolean beenUpdated(@NotNull List<String> result) {
        if(result.size() != serverData.size()) return true;
        for(String s : result) {
            if(!serverData.contains(s)) return true;
        }
        return false;
    }


}
