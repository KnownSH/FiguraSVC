package net.knownsh.figurasvc.neoforge;

import net.neoforged.fml.loading.FMLPaths;
import java.nio.file.Path;

public class NeoForgeExpectPlatformImpl {
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}