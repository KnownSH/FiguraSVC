package net.knownsh.figurasvc.voice;

import net.knownsh.figurasvc.FiguraSVC;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaTable;

public class AudioUtils {
    protected static double calculateRMS(short[] pcm, int samples) {
        if (pcm == null || samples == 0) {
            return -127D;
        }
        double sumOfSquares = 0.0D;
        for (int i = 0; i < samples; i++) {
            sumOfSquares += pcm[i] * pcm[i];
        }

        return Math.sqrt(sumOfSquares / ((double) samples / 2));
    }

    // db full short
    protected static double rmsToDBFS(double rms) {
        if (rms < 0.0D) {
            return -127D;
        }
        return Math.min(Math.max(20.0D * Math.log10(rms / Short.MAX_VALUE), -127D), 0D);
    }

    // db dynamic short
    protected static double rmsToRDB(double rms, double reference) {
        if (rms < 0.0D) {
            return -127D;
        }
        return Math.min(Math.max(20.0D * Math.log10(rms / reference), -127D), 0D);
    }

    public static LuaTable pcmLuaEncode(short[] rawAudio) {
        LuaTable pcmTable = new LuaTable();
        for (int i = 0; i < rawAudio.length; i++) {
            int mod = rawAudio[i];
            pcmTable.set(i + 1, LuaNumber.valueOf(mod));
        }
        return pcmTable;
    }

    public static short[] pcmLuaDecode(LuaTable pcmTable) {
        short[] pcm = new short[pcmTable.length()];
        for (int i = 0; i < pcmTable.length(); i++) {
            pcm[i] = pcmTable.get(i + 1).toshort();
        }
        return pcm;
    }

    public static double getLevelPercent(short[] pcm) {
        double rms = calculateRMS(pcm, pcm.length);
        double dbfs = rmsToDBFS(rms);
        return (dbfs + 127D) / 127D;
    }
}
