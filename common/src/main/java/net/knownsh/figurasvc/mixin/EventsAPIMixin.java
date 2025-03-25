package net.knownsh.figurasvc.mixin;

// based on what is used in Figura GoofyPlugin, which was licensed under the MIT license when this was written

import net.knownsh.figurasvc.EventAccessor;
import org.figuramc.figura.lua.LuaWhitelist;
import org.figuramc.figura.lua.api.event.EventsAPI;
import org.figuramc.figura.lua.api.event.LuaEvent;
import org.figuramc.figura.lua.docs.LuaFieldDoc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = EventsAPI.class, remap = false)
public class EventsAPIMixin implements EventAccessor {
    @Shadow
    @Final
    private Map<String, LuaEvent> events;

    @Unique
    @LuaWhitelist
    @LuaFieldDoc("events.host_microphone")
    public LuaEvent HOST_MICROPHONE;

    @Unique
    @LuaWhitelist
    @LuaFieldDoc("events.microphone")
    public LuaEvent MICROPHONE;

    @Unique
    @LuaWhitelist
    @LuaFieldDoc("svc.microphone")
    public LuaEvent LEGACY_MICROPHONE;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void FiguraSVC$customEvents(CallbackInfo ci) {
        HOST_MICROPHONE = new LuaEvent();
        MICROPHONE = new LuaEvent();
        LEGACY_MICROPHONE = new LuaEvent();
        events.put("HOST_MICROPHONE", HOST_MICROPHONE);
        events.put("MICROPHONE", MICROPHONE);
        events.put("SVC.MICROPHONE", LEGACY_MICROPHONE);
    }

    @Override
    public LuaEvent FiguraSVC$getHostMicrophoneEvent() {
        return HOST_MICROPHONE;
    }

    @Override
    public LuaEvent FiguraSVC$getMicrophoneEvent() {
        return MICROPHONE;
    }

    @Override
    public LuaEvent FiguraSVC$getMicrophoneEventLegacy() {
        return LEGACY_MICROPHONE;
    }
}
