package net.knownsh.figurasvc.voice.event;

import de.maxhenkel.voicechat.api.events.ClientSoundEvent;
import net.knownsh.figurasvc.FiguraSVC;
import net.knownsh.figurasvc.voice.AudioUtils;
import org.figuramc.figura.lua.LuaNotNil;
import org.figuramc.figura.lua.LuaWhitelist;
import org.figuramc.figura.lua.docs.LuaFieldDoc;
import org.figuramc.figura.lua.docs.LuaMethodDoc;
import org.figuramc.figura.lua.docs.LuaTypeDoc;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;

import java.util.function.Supplier;

@LuaWhitelist
@LuaTypeDoc(name = "ClientSoundEventData", value = "sound_event")
public class ClientSoundEventData implements ISoundEvent {
    private final Supplier<Boolean> isWhispering;
    private final short[] rawAudio;

    @LuaWhitelist
    @LuaFieldDoc("sound_event.audio")
    public LuaTable audio;

    public ClientSoundEventData(ClientSoundEvent event) {
        this.rawAudio = event.getRawAudio();
        this.audio = AudioUtils.pcmLuaEncode(event.getRawAudio());
        this.isWhispering = event::isWhispering;
    }

    @Override
    public short[] getAudio() {
        return this.rawAudio;
    }

    @LuaWhitelist
    @LuaMethodDoc("sound_event.is_whispering")
    public boolean isWhispering() {
        return this.isWhispering.get();
    }

    @LuaWhitelist
    @LuaMethodDoc("sound_event.get_sound_level")
    public double getSoundLevel() {
        return AudioUtils.getLevelPercent(rawAudio);
    }

    @LuaWhitelist
    public Object __index(String arg) {
        return "audio".equals(arg) ? audio : null;
    }

    @LuaWhitelist
    public void __newindex(@LuaNotNil String key, Object value) {
        throw new LuaError("Cannot assign value on key \"" + key + "\". \nSince FiguraSVC version 2.1, raw audio data is accessed through the \"voice_event.audio\" key.");
    }
}