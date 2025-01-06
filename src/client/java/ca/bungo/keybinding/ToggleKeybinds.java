package ca.bungo.keybinding;

import ca.bungo.renderer.components.PlayerInfoComponent;
import ca.bungo.renderer.components.StageNotesComponent;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ToggleKeybinds {

    public static void initKeybinds() {

        KeyBinding togglePlayerInfo = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.metaoverlay.toggleplayerinfo",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_BACKSLASH,
                ""
        ));

        KeyBinding toggleStageNotes = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.metaoverlay.togglestagenotes",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_BRACKET,
                ""
        ));


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
           if(togglePlayerInfo.wasPressed()) {
               PlayerInfoComponent.isRendered = !PlayerInfoComponent.isRendered;
           }

            if(toggleStageNotes.wasPressed()) {
                StageNotesComponent.isRendered = !StageNotesComponent.isRendered;
            }
        });

    }

}
