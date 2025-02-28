package net.knownsh.figurasvc.forge;

import net.knownsh.figurasvc.FiguraSVC;
import net.minecraftforge.fml.common.Mod;

@Mod(FiguraSVC.PLUGIN_ID)
public class FiguraSVCForge {
    public FiguraSVCForge() {
        FiguraSVC.init();
    }
}
