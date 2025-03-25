package net.knownsh.figurasvc.mixin;

import net.knownsh.figurasvc.voice.VoiceAPI;
import org.figuramc.figura.lua.docs.FiguraGlobalsDocs;
import org.figuramc.figura.lua.docs.LuaFieldDoc;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = FiguraGlobalsDocs.class, remap = false)
public class FiguraGlobalsDocsMixin {
    @LuaFieldDoc("globals.voice")
    public VoiceAPI voice;
}
