package ca.bungo.renderer.components;

import ca.bungo.renderer.Renderable;
import ca.bungo.renderer.data.PlayerData;
import ca.bungo.utility.PlayerUtility;
import ca.bungo.utility.config.ModConfigs;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerInfoComponent implements Renderable {

    int componentWidth = 500;
    int componentHeight = -1;

    int basePosX = 5;
    int basePosY = 15;

    public static boolean isRendered = true;

    private PlayerData activePlayerData = null;

    private List<JTextPane> labels = new ArrayList<>();

    public PlayerInfoComponent() {
        final int[] timeTracker = {0};
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            PlayerEntity player = PlayerUtility.getTargetPlayer(client, 30);

            if(player == null && timeTracker[0] >= ModConfigs.ACTIVE_PLAYER_SECONDS*20) { //Not looking at a player - Count down until hiding
                activePlayerData = null;
                timeTracker[0] = 0;
                return;
            }
            else if(player == null){ //If I'm looking at a player, don't want a countdown
                timeTracker[0]++;
                return;
            }

            timeTracker[0] = 0;
            if(activePlayerData == null || !activePlayerData.getPlayerUUID().equals(player.getUuidAsString())) //Count down ended || looked at a new player - reload the notes
                activePlayerData = new PlayerData(player.getName().getString(), player.getUuidAsString());
        });
    }

    public List<JTextPane> getLabels() {
        return labels;
    }

    @Override
    public void renderObject(Graphics g, int windowWidth, int windowHeight) {
        labels.clear();
        if (activePlayerData == null || !isRendered) return;

        g.setColor(new Color(50, 50, 50, 160));
        g.fillRect(basePosX, basePosY, componentWidth, windowHeight-basePosY);

        int offset = basePosY + 10;

        /*
        JTextPane playerUsername = createTextPane(
                "<html>" +
                        "<body style='font-size: 16px;color: white;font-family: sans-serif;'>" +
                        "<b>Player Username: " + activePlayerData.getPlayerUsername() + "</b>" +
                        "</body>" +
                        "</html>", componentWidth);
        playerUsername.setForeground(Color.WHITE);
        playerUsername.setBounds(basePosX, offset, componentWidth, playerUsername.getPreferredSize().height);
        labels.add(playerUsername);

        offset += playerUsername.getPreferredSize().height + 10;

        JTextPane playerUUID = createTextPane(
                "<html>" +
                        "<body style='font-size: 16px;color: white;font-family: sans-serif;'>" +
                        "<b>Player UUID: " + activePlayerData.getPlayerUUID() + "</b>" +
                        "</body>" +
                        "</html>", componentWidth);
        playerUUID.setForeground(Color.WHITE);
        playerUUID.setBounds(basePosX, offset, componentWidth, playerUUID.getPreferredSize().height);
        labels.add(playerUUID);

        offset += playerUUID.getPreferredSize().height + 10;*/


        for(String message : activePlayerData.getPlayerNotes()){
            JTextPane label = createTextPane("<html><body style='font-size: 14px;color: white;font-family: sans-serif;'>" + message + "</body></html>", componentWidth);
            label.setForeground(Color.WHITE);
            label.setBounds(basePosX, offset, componentWidth, label.getHeight());
            labels.add(label);
            offset += label.getPreferredSize().height + 10;
        }

    }

    @Override
    public void toggleRendered() {
        isRendered = !isRendered;
    }

    /**
     * Creates a JTextPane with the specified HTML content and width.
     *
     * @param htmlContent The HTML content to display
     * @param width       The width of the text pane
     * @return The configured JTextPane
     */
    private JTextPane createTextPane(String htmlContent, int width) {
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setText(htmlContent);
        textPane.setEditable(false);
        textPane.setOpaque(false);
        textPane.setForeground(Color.WHITE);
        textPane.setSize(width, Short.MAX_VALUE); // Allow the text pane to grow vertically
        return textPane;
    }

}
