package net.knownsh.figurasvc.svc;

import net.knownsh.figurasvc.FiguraSVC;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.lua.FiguraLuaRuntime;
import org.figuramc.figura.lua.LuaNotNil;
import org.figuramc.figura.lua.LuaWhitelist;
import org.figuramc.figura.lua.docs.LuaMethodDoc;
import org.figuramc.figura.lua.docs.LuaMethodOverload;
import org.figuramc.figura.lua.docs.LuaTypeDoc;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

@LuaWhitelist
@LuaTypeDoc(name = "FiguraSVC", value = "svc")
public class FiguraSVCAPI {
    private final FiguraLuaRuntime runtime;
    private final Avatar owner;

    public FiguraSVCAPI(FiguraLuaRuntime runtime) {
        this.runtime = runtime;
        this.owner = runtime.owner;
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = {
                    @LuaMethodOverload(
                            argumentTypes = {LuaTable.class, Number.class},
                            argumentNames = {"pcm", "amplification"}
                    ),
            },
            value = "svc.amplify"
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
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "svc.test")
    public void test() {
        FiguraSVC.LOGGER.info("svc.test Logged!");
    }

    @Override
    public String toString() {
        return "FiguraSVCAPI";
    }
}