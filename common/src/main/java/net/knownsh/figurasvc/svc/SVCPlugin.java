package net.knownsh.figurasvc.svc;

import de.maxhenkel.voicechat.api.ForgeVoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.ClientReceiveSoundEvent;
import de.maxhenkel.voicechat.api.events.ClientSoundEvent;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import net.knownsh.figurasvc.EventAccessor;
import net.knownsh.figurasvc.FiguraSVC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;
import org.figuramc.figura.FiguraMod;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.lua.api.event.LuaEvent;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.Varargs;

import static net.knownsh.figurasvc.svc.PCMUtils.pcmLuaDecode;
import static net.knownsh.figurasvc.svc.PCMUtils.pcmLuaEncode;

@ForgeVoicechatPlugin
public class SVCPlugin implements VoicechatPlugin {
    @Override
    public String getPluginId() {
        return FiguraSVC.PLUGIN_ID;
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        FiguraSVC.LOGGER.info("Registering events");
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
        onLocalPlayerSpeakLegacy(event); // Legacy code support (for older avatars)

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

    public static void onLocalPlayerSpeakLegacy(ClientSoundEvent event) {
        Avatar avatar = AvatarManager.getAvatarForPlayer(FiguraMod.getLocalPlayerUUID());
        // legacy event handler
        if (!event.getVoicechat().isMuted() && avatar != null && avatar.luaRuntime != null) {
            LuaTable pCMTable = new LuaTable();
            for (int i = 0; i < event.getRawAudio().length; i++) {
                int mod = event.getRawAudio()[i];
                pCMTable.set(i, LuaNumber.valueOf(mod));
            }
            LuaEvent legacyMicrophoneEvent = ((EventAccessor) avatar.luaRuntime.events).FiguraSVC$getMicrophoneEventLegacy();
            avatar.run(legacyMicrophoneEvent, avatar.tick, pCMTable);
        }
    }
}
