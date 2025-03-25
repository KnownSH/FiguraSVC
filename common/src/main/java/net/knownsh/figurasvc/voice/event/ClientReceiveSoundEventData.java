package net.knownsh.figurasvc.voice.event;

import de.maxhenkel.voicechat.api.events.ClientReceiveSoundEvent;
import net.knownsh.figurasvc.FiguraSVC;
import org.figuramc.figura.lua.LuaNotNil;
import org.figuramc.figura.lua.LuaWhitelist;
import org.figuramc.figura.lua.docs.LuaFieldDoc;
import org.figuramc.figura.lua.docs.LuaTypeDoc;
import org.luaj.vm2.LuaError;

@LuaWhitelist
@LuaTypeDoc(name = "ClientReceiveSoundEventData", value = "receive_sound_event")
public class ClientReceiveSoundEventData implements ISoundEvent {
    @LuaWhitelist
    @LuaFieldDoc("receive_sound_event.audio")
    public short[] audio;

    @LuaWhitelist
    @LuaFieldDoc("receive_sound_event.uuid")
    public String uuid;

    public ClientReceiveSoundEventData(ClientReceiveSoundEvent event) {
        this.audio = event.getRawAudio();
        this.uuid = event.getId().toString();

        if (event instanceof ClientReceiveSoundEvent.EntitySound entitySoundEvent) {
            FiguraSVC.LOGGER.info("Entity sound event: {}", entitySoundEvent.getDistance());
        }
    }

    @Override
    public short[] getAudio() {
        return new short[0];
    }

    @LuaWhitelist
    public Object __index(String arg) {
        return switch (arg) {
            case "audio" -> this.audio;
            case "uuid" -> this.uuid;
            default -> null;
        };
    }

    @LuaWhitelist
    public void __newindex(@LuaNotNil String key, Object value) {
        throw new LuaError("Cannot assign value on key \"" + key + "\". \nSince FiguraSVC version 2.1, raw audio data is accessed through the \"receive_voice_event.audio\" key.");
    }
}