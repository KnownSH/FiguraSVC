package net.knownsh.figurasvc;

import org.figuramc.figura.lua.api.event.LuaEvent;

// based on what is used in Figura GoofyPlugin, which was licensed under the MIT license when this was created

public interface EventAccessor {
    LuaEvent FiguraSVC$getHostMicrophoneEvent();
    LuaEvent FiguraSVC$getMicrophoneEvent();
    LuaEvent FiguraSVC$getMicrophoneEventLegacy();
}
