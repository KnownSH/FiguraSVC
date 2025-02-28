package net.knownsh.figurasvc.forge;

import net.minecraftforge.fml.loading.FMLPaths;
import java.nio.file.Path;

public class ForgeExpectPlatformImpl {
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
