package ca.bungo.renderer;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public interface Renderable {
    void renderObject(Graphics g, int windowWidth, int windowHeight);
    void toggleRendered();
    List<JTextPane> getLabels();
}
