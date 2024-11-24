package com.enthusiasm.plurecore.command;

import java.util.Set;

import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;


public class CommandRegistry {
    public static void build(String modId) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(Scanners.TypesAnnotated)
                .addClassLoaders(FabricLauncherBase.getLauncher().getTargetClassLoader())
                .forPackage("com.enthusiasm." + modId)
        );

        Set<Class<?>> modClasses = reflections.getTypesAnnotatedWith(Command.class);

//        new HashSet<>(modClasses)
//                .forEach(modClass -> {
//                    PlureCoreEntrypoint.LOGGER.info("Registering command: {}", modClass.getName());
//                });
    }
}
