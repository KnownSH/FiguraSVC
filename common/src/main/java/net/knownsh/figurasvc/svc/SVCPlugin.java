package net.knownsh.figurasvc.svc;

import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.ClientReceiveSoundEvent;
import de.maxhenkel.voicechat.api.events.ClientSoundEvent;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import net.knownsh.figurasvc.EventAccessor;
import net.knownsh.figurasvc.FiguraSVC;
import net.knownsh.figurasvc.legacy.VoiceChatEventLegacy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;
import org.figuramc.figura.FiguraMod;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.lua.api.event.LuaEvent;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.Varargs;

import static net.knownsh.figurasvc.svc.PCMUtils.pcmLuaDecode;
import static net.knownsh.figurasvc.svc.PCMUtils.pcmLuaEncode;

public class SVCPlugin implements VoicechatPlugin {
    @Override
    public String getPluginId() {
        return FiguraSVC.PLUGIN_ID;
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(ClientSoundEvent.class, this::onLocalPlayerSpeak);
        registration.registerEvent(ClientReceiveSoundEvent.class, this::onLocalPlayerReceive);
    }

    /**
     * Event gets called when the local player receives a sound from ANOTHER player
     * @param event the ClientReceiveSoundEvent
     */
    private void onLocalPlayerReceive(ClientReceiveSoundEvent event) {
        Avatar speakingPlayer = AvatarManager.getAvatarForPlayer(event.getId());
        if (speakingPlayer == null || speakingPlayer.luaRuntime == null) return;
        LuaEvent microphoneEvent = ((EventAccessor) speakingPlayer.luaRuntime.events).FiguraSVC$getMicrophoneEvent();
        if (microphoneEvent.__len() <= 0) return;
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        Player player = level.getPlayerByUUID(event.getId());
        if (player == null) return;

        speakingPlayer.run(
                microphoneEvent,
                speakingPlayer.render,
                LuaString.valueOf(player.getName().getString()),
                pcmLuaEncode(event.getRawAudio()));
    }

    private void onLocalPlayerSpeak(ClientSoundEvent event) {
        VoiceChatEventLegacy.onLocalPlayerSpeakLegacy(event); // Legacy code support (for older avatars)

        Avatar localPlayer = AvatarManager.getAvatarForPlayer(FiguraMod.getLocalPlayerUUID());
        if (localPlayer == null || localPlayer.luaRuntime == null) return;
        LuaEvent microphoneEvent = ((EventAccessor) localPlayer.luaRuntime.events).FiguraSVC$getHostMicrophoneEvent();
        if (microphoneEvent.__len() <= 0) return;
        Varargs newPCM = localPlayer.run(microphoneEvent, localPlayer.render, pcmLuaEncode(event.getRawAudio()));

        // set new audio PCM
        try {
            if (newPCM != null) {
                LuaTable newPCMTable = newPCM.checktable(1);
                short[] newPCMData = pcmLuaDecode(newPCMTable);
                event.setRawAudio(newPCMData);
            }
        } catch (Exception ignored) {}
    }
}
