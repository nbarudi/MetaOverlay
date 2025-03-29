package ca.bungo;

import ca.bungo.keybinding.ToggleKeybinds;
import ca.bungo.renderer.OverlayHandler;
import ca.bungo.utility.config.ModConfigs;
import net.fabricmc.api.ClientModInitializer;

import javax.swing.*;

public class MetaOverlayClient implements ClientModInitializer {
	private JFrame frame;



	@Override
	public void onInitializeClient() {
		ModConfigs.registerConfigs();
		System.setProperty("java.awt.headless", "false");
		new Thread(OverlayHandler::initializeOverlay).start();
		ToggleKeybinds.initKeybinds();
	}

}
