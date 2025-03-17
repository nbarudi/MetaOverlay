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
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerInfoComponent implements Renderable {

    int componentWidth = 500;
    int componentHeight = -1;

    int basePosX = 5;
    int basePosY = 15;

    public static boolean isRendered = true;

    private PlayerData activePlayerData = null;

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

    @Override
    public void renderObject(Graphics g, int windowWidth, int windowHeight) {
        if (activePlayerData == null || !isRendered) return;
        MinecraftClient mc = MinecraftClient.getInstance();

        Graphics2D g2d = (Graphics2D) g;

        g.setColor(new Color(50, 50, 50, 160));
        g.fillRect(basePosX, basePosY, componentWidth, windowHeight-basePosY);

        int offset = basePosY + 10;

        // Save original transform for scaling
        AffineTransform ogTransform = g2d.getTransform();
        g2d.scale(1.5, 1.5);

        // Set font and color
        g2d.setColor(Color.WHITE);
        FontMetrics metrics = g2d.getFontMetrics();

        int scaledWidth = (int)(componentWidth/1.5f);

        // Draw wrapped text for username
        offset = drawWrappedText(g2d, "Player Username: " + activePlayerData.getPlayerUsername(), basePosX, offset, metrics, scaledWidth);

        // Draw wrapped text for UUID
        offset = drawWrappedText(g2d, "Player UUID: " + activePlayerData.getPlayerUUID(), basePosX, offset, metrics, scaledWidth);

        // Restore original transform
        //g2d.setTransform(ogTransform);

        // Additional content
        g2d.setColor(Color.WHITE);
        offset += 20;
        g2d.drawString("Player Notes:", basePosX, offset);
        offset += 20;

        for(String note : activePlayerData.getPlayerNotes()) {
            offset = drawWrappedText(g2d, note, basePosX, offset, metrics, scaledWidth);
        }
        g2d.setTransform(ogTransform);
    }

    @Override
    public void toggleRendered() {
        isRendered = !isRendered;
    }

    /**
     * Draws wrapped text within the specified width.
     *
     * @param g2d       Graphics2D object for rendering
     * @param text      The text to render
     * @param x         X-coordinate for the text
     * @param y         Starting Y-coordinate for the text
     * @param metrics   FontMetrics for measuring text
     * @param maxWidth  Maximum width for wrapping text
     * @return The updated Y-coordinate after rendering the text
     */
    private int drawWrappedText(Graphics2D g2d, String text, int x, int y, FontMetrics metrics, int maxWidth) {
        int lineHeight = metrics.getHeight();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            if(word.isEmpty())
                word = "\n";
            String testLine = line + (line.length() > 0 ? " " : "") + word;
            int testWidth = metrics.stringWidth(testLine);

            if (testWidth > maxWidth) {
                // Draw the current line and reset
                g2d.drawString(line.toString(), x, y);
                line = new StringBuilder(word);
                y += lineHeight;
            } else {
                line.append((line.length() > 0 ? " " : "")).append(word);
            }
        }

        // Draw the last line
        if (line.length() > 0) {
            g2d.drawString(line.toString(), x, y);
            y += lineHeight;
        }

        return y; // Return updated Y-coordinate
    }

}
