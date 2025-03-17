package ca.bungo.renderer.data;

import ca.bungo.MetaOverlay;
import ca.bungo.renderer.components.StageNotesComponent;
import ca.bungo.utility.NetworkUtility;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ServerData {

    private final List<String> serverData;

    private static int updateFailCounter = 0;

    public ServerData() {
        serverData = new ArrayList<>();
    }

    public void updateDataIfNeeded(boolean isFirst) {
        try {
            synchronized (this) {
                List<String> result = NetworkUtility.getTypedNotes(NetworkUtility.NoteType.STAGE, isFirst ? "immediate" : "poll").get();
                if(result == null) return;
                StageNotesComponent.isRendered = true;
                serverData.clear();
                serverData.addAll(result);
            }

        } catch (URISyntaxException | IOException | InterruptedException | ExecutionException e) {
            MetaOverlay.LOGGER.error(e.getMessage());
            updateFailCounter++;
            int delay = (int) (1000 * Math.pow(2, updateFailCounter));
            if(delay > 300000)
                delay = 300000; // 5 minute max delay
            MetaOverlay.LOGGER.error("Failed to make connection to server.. Delaying for {} second(s)", delay/1000);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ignored) {}
        }
    }


    public List<String> getServerData() {
        return serverData;
    }

}
