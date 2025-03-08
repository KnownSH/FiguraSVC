package net.knownsh.figurasvc.legacy;

import com.mojang.datafixers.util.Pair;
import de.maxhenkel.voicechat.api.events.ClientSoundEvent;
import net.knownsh.figurasvc.EventAccessor;
import net.knownsh.figurasvc.FiguraSVC;
import org.figuramc.figura.FiguraMod;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.entries.FiguraEvent;
import org.figuramc.figura.entries.annotations.FiguraEventPlugin;
import org.figuramc.figura.lua.LuaWhitelist;
import org.figuramc.figura.lua.api.event.LuaEvent;
import org.figuramc.figura.lua.docs.LuaFieldDoc;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaTable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Legacy FiguraSVC event, maintained to keep old figuraSVC avatars functional on newer servers/clients
 */
@FiguraEventPlugin
public class VoiceChatEventLegacy implements FiguraEvent {
    @LuaWhitelist
    @LuaFieldDoc("svc.microphone")
    public static LuaEvent MICROPHONE = new LuaEvent();

    @Override
    public String getID() {
        return "svc";
    }

    @Override
    public Collection<Pair<String, LuaEvent>> getEvents() {
        return Collections.singleton(new Pair<>("MICROPHONE", MICROPHONE));
    }

    public static void onLocalPlayerSpeakLegacy(ClientSoundEvent event) {
        Avatar avatar = AvatarManager.getAvatarForPlayer(FiguraMod.getLocalPlayerUUID());
        // legacy event handler
        if (!event.getVoicechat().isMuted() && avatar != null) {
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