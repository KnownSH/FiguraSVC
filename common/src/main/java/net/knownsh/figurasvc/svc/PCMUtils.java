package net.knownsh.figurasvc.svc;

import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaTable;

public class PCMUtils {

    public static LuaTable pcmLuaEncode(short[] rawAudio) {
        LuaTable pcmTable = new LuaTable();
        for (int i = 0; i < rawAudio.length; i++) {
            int mod = rawAudio[i];
            pcmTable.set(i, LuaNumber.valueOf(mod));
        }
        return pcmTable;
    }

    public static short[] pcmLuaDecode(LuaTable pcmTable) {
        short[] pcm = new short[pcmTable.length()];
        for (int i = 0; i < pcmTable.length(); i++) {
            pcm[i] = pcmTable.toshort(i + 1);
        }
        return pcm;
    }
}
