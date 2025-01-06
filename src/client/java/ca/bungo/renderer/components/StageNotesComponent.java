package ca.bungo.renderer.components;

import ca.bungo.renderer.Renderable;
import ca.bungo.renderer.data.ServerData;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class StageNotesComponent implements Renderable {

    int componentWidth = 500;

    int basePosX = 0;
    int basePosY = 15;

    private ServerData serverData;
    public static boolean isRendered = false;

    public StageNotesComponent() {
        serverData = new ServerData();
        new Timer(50, e -> {
            serverData.updateDataIfNeeded();
        }).start();
    }

    @Override
    public void renderObject(Graphics g, int windowWidth, int windowHeight) {
        if(serverData == null || !isRendered) return;
        basePosX = windowWidth - componentWidth - 5;
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

        g2d.setColor(Color.WHITE);
        offset = drawWrappedText(g2d, "Stage Notes:", (int) (basePosX/1.5f), offset, metrics, scaledWidth);

        for(String message : serverData.getServerData()){
            offset = drawWrappedText(g2d, message, (int) (basePosX/1.5f), offset, metrics, scaledWidth);
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
