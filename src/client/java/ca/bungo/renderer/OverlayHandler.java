package ca.bungo.renderer;

import ca.bungo.renderer.components.PlayerInfoComponent;
import ca.bungo.renderer.components.StageNotesComponent;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import net.minecraft.client.MinecraftClient;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;;

public class OverlayHandler {

    public static final String CLIENT_NAME = "Minecraft + Bungo's Overlay";
    public static JFrame frame;
    public static JPanel panel;

    public static final List<Renderable> renderableList = new ArrayList<>();

    private static void registerRenderables() {
        renderableList.add(new PlayerInfoComponent());
        renderableList.add( new StageNotesComponent());
    }

    public static void addRenderable(Renderable r) {
        synchronized (renderableList) {
            renderableList.add(r);
        }
    }

    public static void removeRenderable(Renderable r) {
        synchronized (renderableList) {
            renderableList.remove(r);
        }
    }

    public static void initializeOverlay() {
        registerRenderables();
        System.setProperty("java.awt.headless", "false");

        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Meta-Overlay");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setUndecorated(true);
            frame.setAlwaysOnTop(true);
            frame.setBackground(new Color(0, 0, 0, 0));

            panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);

                    MinecraftClient mc = MinecraftClient.getInstance();
                    if (mc != null && mc.getWindow() != null) {
                        List<Renderable> currentRenderables;
                        synchronized (renderableList) {
                            currentRenderables = new ArrayList<>(renderableList);
                        }
                        for (Renderable renderable : currentRenderables) {
                            renderable.renderObject(g, frame.getBounds().width, frame.getBounds().height);
                        }
                    }
                }
            };
            panel.setOpaque(false);

            frame.add(panel);
            frame.setVisible(true);

            makeOverlayClickThrough();
            trackMinecraftWindow();
        });
    }

    private static void makeOverlayClickThrough() {
        new Thread(() -> {
            try {
                Thread.sleep(500);
                WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, "Meta-Overlay");
                if (hwnd != null) {
                    int style = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
                    User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, style | WinUser.WS_EX_LAYERED | WinUser.WS_EX_TRANSPARENT);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void trackMinecraftWindow() {
        MinecraftClient mc = MinecraftClient.getInstance();
        new Timer(50, e -> {
            if (mc != null && mc.getWindow() != null) {
                mc.getWindow().setTitle(CLIENT_NAME);

                WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, CLIENT_NAME);
                if (hwnd != null) {
                    WinDef.RECT rect = new WinDef.RECT();
                    User32.INSTANCE.GetWindowRect(hwnd, rect);

                    SwingUtilities.invokeLater(() -> {
                        frame.setBounds(rect.left, rect.top, rect.toRectangle().width, rect.toRectangle().height);
                        frame.repaint();
                    });
                }
            }
        }).start();
    }
}
