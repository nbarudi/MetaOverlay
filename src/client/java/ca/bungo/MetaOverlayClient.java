package ca.bungo;

import ca.bungo.keybinding.ToggleKeybinds;
import ca.bungo.renderer.OverlayHandler;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import net.fabricmc.api.ClientModInitializer;
import org.lwjgl.glfw.GLFW;

import javax.swing.*;
import java.awt.*;

public class MetaOverlayClient implements ClientModInitializer {
	private JFrame frame;

	@Override
	public void onInitializeClient() {
		System.setProperty("java.awt.headless", "false");
		new Thread(OverlayHandler::initializeOverlay).start();
		ToggleKeybinds.initKeybinds();
	}

}
