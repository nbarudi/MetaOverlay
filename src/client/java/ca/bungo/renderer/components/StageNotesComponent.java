package ca.bungo.renderer.components;

import ca.bungo.MetaOverlay;
import ca.bungo.MetaOverlayClient;
import ca.bungo.renderer.Renderable;
import ca.bungo.renderer.data.ServerData;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class StageNotesComponent implements Renderable {

    int componentWidth = 500;

    int basePosX = 0;
    int basePosY = 15;

    private final ServerData serverData;
    public static boolean isRendered = false;

    private boolean isFirst = true;

    private List<JTextPane> labels = new ArrayList<>();

    public StageNotesComponent() {
        serverData = new ServerData();
        startDataUpdateThread();
    }

    private void startDataUpdateThread(){
        new Thread(() -> {
            while(true) {
                serverData.updateDataIfNeeded(isFirst);
                if(isFirst)
                    isFirst = false;
            }
        }).start();
    }

    public List<JTextPane> getLabels() {
        return labels;
    }

    @Override
    public void renderObject(Graphics g, int windowWidth, int windowHeight) {
        labels.clear();
        if(serverData == null || !isRendered) return;
        basePosX = windowWidth - componentWidth - 5;

        int offset = 10;

        g.setColor(new Color(50, 50, 50, 160));
        g.fillRect(basePosX, basePosY, componentWidth, windowHeight-basePosY);

        /*
        JTextPane titleLabel = createTextPane("<html><body style='font-size: 16px;color: white;font-family: sans-serif;'><b>Stage Notes:</body></html>", componentWidth);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(basePosX, offset, componentWidth, titleLabel.getPreferredSize().height);
        labels.add(titleLabel);

        offset = basePosY + titleLabel.getPreferredSize().height + 10;*/

        for(String message : serverData.getServerData()){
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
