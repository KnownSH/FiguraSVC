package net.knownsh.figurasvc.neoforge;

import net.knownsh.figurasvc.FiguraSVC;
import net.neoforged.fml.common.Mod;

@Mod(FiguraSVC.PLUGIN_ID)
public class FiguraSVCNeoForge {
    public FiguraSVCNeoForge() {
        FiguraSVC.init();
    }
}