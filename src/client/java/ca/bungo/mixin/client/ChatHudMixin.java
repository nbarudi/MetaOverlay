package ca.bungo.mixin.client;


import ca.bungo.MetaOverlay;
import ca.bungo.renderer.components.PlayerInfoComponent;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {

    @Unique
    private static final String overlayHelperPrefix = "[MetaOverlayHelper]";

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("HEAD"), cancellable = true)
    private void onAddMessage(Text message, MessageSignatureData signatureData, MessageIndicator indicator, CallbackInfo ci) {
        String messageContent = message.getString();
        if(messageContent.startsWith(overlayHelperPrefix)) {
            ci.cancel();
            String characterUUID = messageContent.substring(20);
            MetaOverlay.LOGGER.info("Found character uuid: {} | {}", messageContent, characterUUID);
            PlayerInfoComponent.updateWithCharacterUUID(characterUUID);
        }
    }


}
