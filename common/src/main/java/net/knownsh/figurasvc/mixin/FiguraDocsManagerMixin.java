package net.knownsh.figurasvc.mixin;

import net.knownsh.figurasvc.voice.VoiceAPI;
import net.knownsh.figurasvc.voice.event.ClientReceiveSoundEventData;
import net.knownsh.figurasvc.voice.event.ClientSoundEventData;
import org.figuramc.figura.lua.docs.FiguraDocsManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mixin(value = FiguraDocsManager.class, remap = false)
public class FiguraDocsManagerMixin {
    @Shadow @Final
    private static Map<String, Collection<Class<?>>> GLOBAL_CHILDREN;

    static {
        GLOBAL_CHILDREN.put("voice", List.of(
                VoiceAPI.class,
                ClientSoundEventData.class,
                ClientReceiveSoundEventData.class
        ));
    }
}
