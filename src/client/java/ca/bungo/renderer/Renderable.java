package ca.bungo.renderer;

import javax.swing.*;
import java.awt.*;

public interface Renderable {
    void renderObject(Graphics g, int windowWidth, int windowHeight);
    void toggleRendered();
}
