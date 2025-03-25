package net.knownsh.figurasvc.voice;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.knownsh.figurasvc.voice.event.ISoundEvent;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.lua.FiguraLuaRuntime;
import org.figuramc.figura.lua.LuaNotNil;
import org.figuramc.figura.lua.LuaWhitelist;
import org.figuramc.figura.lua.docs.LuaMethodDoc;
import org.figuramc.figura.lua.docs.LuaMethodOverload;
import org.figuramc.figura.lua.docs.LuaTypeDoc;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@LuaWhitelist
@LuaTypeDoc(name = "VoiceAPI", value = "voice")
public class VoiceAPI {
    private final FiguraLuaRuntime runtime;
    private final Avatar owner;
    private final boolean isHost;

    public VoiceAPI(FiguraLuaRuntime runtime) {
        this.runtime = runtime;
        this.owner = runtime.owner;
        this.isHost = runtime.owner.isHost;
    }

    private final LoadingCache<UUID, Double> smoothingCache =
            CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.SECONDS).build(CacheLoader.from(() -> 0D));

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaMethodOverload(
                    argumentTypes = {Double.class, Double.class},
                    argumentNames = {"audioLevel", "sensitivity"}
            ),
            value = "voice.smoothing"
    )
    public double smoothing(Double audioLevel, Double sensitivity) {
        try {
            Double smoothingCacheValue = smoothingCache.get(owner.owner);
            smoothingCache.put(owner.owner, sensitivity * audioLevel + (1 - sensitivity) * smoothingCacheValue);
            return smoothingCache.get(owner.owner);
        } catch (Exception e) {
            return 0;
        }
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaMethodOverload(
                    argumentTypes = {LuaTable.class, Number.class},
                    argumentNames = {"pcm", "pitchFactor"}
            ),
            value = "voice.change_pitch"
    )
    public LuaTable changePitch(@LuaNotNil LuaTable pcmTable, @LuaNotNil Number pitchFactor) {
        short[] pcmArray = AudioUtils.pcmLuaDecode(pcmTable);
        short[] newPcmArray = new short[960];

        for (int i = 0; i < newPcmArray.length; i++) {
            double inputIndexDouble = i / pitchFactor.doubleValue();
            int inputIndex = (int) inputIndexDouble;

            if (inputIndex >= 0 && inputIndex < pcmArray.length) { // Check if inputIndex is within bounds
                newPcmArray[i] = pcmArray[inputIndex];
            } else {
                newPcmArray[i] = 0;
            }
        }

        return AudioUtils.pcmLuaEncode(newPcmArray);
    }

    /*
    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaMethodOverload(
                    argumentTypes = {LuaTable.class, Number.class},
                    argumentNames = {"pcm", "amplification"}
            ),
            value = "voice.amplify"
    )
    public LuaTable amplify(@LuaNotNil LuaTable table, Number amplification) {
        LuaTable result = new LuaTable();
        for (int i = 0; i < table.length(); i++) {
            short sample = table.get(i).toshort();
            int amplifiedSample = sample * amplification.shortValue();

            amplifiedSample = Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, amplifiedSample));
            result.set(i + 1, LuaValue.valueOf((short) amplifiedSample));
        }
        return result;
    }*/

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = {
                    @LuaMethodOverload(argumentTypes = LuaTable.class),
                    @LuaMethodOverload(argumentTypes = ISoundEvent.class),
            },
            value = "voice.get_level"
    )
    public double getLevel(@LuaNotNil Object pcm) {
        short[] pcmArray;
        if (pcm instanceof LuaTable pcmTable) {
            pcmArray = AudioUtils.pcmLuaDecode(pcmTable.checktable());
        } else if (pcm instanceof ISoundEvent eventData) {
            pcmArray = eventData.getAudio();
        } else {
            throw new LuaError("Unsupported pcm type: " + pcm.getClass());
        }
        return AudioUtils.getLevelPercent(pcmArray);
    }

    @Override
    public String toString() {
        return "VoiceAPI";
    }
}