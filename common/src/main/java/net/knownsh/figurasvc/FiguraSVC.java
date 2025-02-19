package net.knownsh.figurasvc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.entries.FiguraAPI;
import org.figuramc.figura.entries.annotations.FiguraAPIPlugin;
import org.figuramc.figura.lua.LuaWhitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FiguraAPIPlugin
@LuaWhitelist
public class FiguraSVC implements FiguraAPI {

    public static final String PLUGIN_ID = "figurasvc";
    public static final Logger LOGGER = LoggerFactory.getLogger(PLUGIN_ID);
    private Avatar avatar;

    public FiguraSVC() {}

    public FiguraSVC(Avatar avatar) {
        this.avatar = avatar;
    }

    public static void init() {
        LOGGER.info("Hello from FiguraSVC");
    }

    @Override
    public FiguraAPI build(Avatar avatar) {
        return new FiguraSVC(avatar);
    }

    @Override
    public String getName() {
        return PLUGIN_ID;
    }

    /**
     * You must whitelist your classes for your Plugin to work correctly! This cannot be null
     */
    @Override
    public Collection<Class<?>> getWhitelistedClasses() {
        List<Class<?>> classesToRegister = new ArrayList<>();
        for (Class<?> aClass : EXAMPLE_PLUGIN_CLASSES) {
            if (aClass.isAnnotationPresent(LuaWhitelist.class)) {
                classesToRegister.add(aClass);
            }
        }
        return classesToRegister;
    }

    /**
     * This can be empty, but not null
     */
    @Override
    public Collection<Class<?>> getDocsClasses() {
        return List.of();
    }

    public static final Class<?>[] EXAMPLE_PLUGIN_CLASSES = new Class[] {
        FiguraSVC.class,
    };
}
