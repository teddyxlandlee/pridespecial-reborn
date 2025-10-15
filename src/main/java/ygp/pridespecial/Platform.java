package ygp.pridespecial;

import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.file.Path;

final class Platform {
    private Platform() {}

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final Path CONFIG_DIR;
    private static final MethodHandle IDENTIFIER_TRY_PARSE_FACTORY;   // (String, String) -> T

    static {
        Class<?> fabricLoader = null;
        Object loaderInstance = null;
        Class<?> mojIdentifier = null;

        try {
            // Mojang went crazy but that's what's going on in the post-obf era
            mojIdentifier = Class.forName("net.minecraft.resources.Identifier");
        } catch (ClassNotFoundException ignore) {
        }

        try {
            fabricLoader = Class.forName("net.fabricmc.loader.api.FabricLoader");
        } catch (ClassNotFoundException ignore) {
        }

        var lookup = LOOKUP;
        try {
            if (fabricLoader != null) {    // is Fabric
                loaderInstance = lookup.findStatic(fabricLoader, "getInstance", MethodType.methodType(fabricLoader)).invoke();
                CONFIG_DIR = (Path) lookup.findVirtual(fabricLoader, "getConfigDir", MethodType.methodType(Path.class)).invoke(loaderInstance);
            } else {
                Class<?> c = Class.forName("net.neoforged.fml.loading.FMLPaths");
                Object enumPath = lookup.findStaticVarHandle(c, "CONFIGDIR", c).get();
                CONFIG_DIR = (Path) lookup.findVirtual(c, "get", MethodType.methodType(Path.class)).invoke(enumPath);
            }

            if (mojIdentifier != null) {
                IDENTIFIER_TRY_PARSE_FACTORY = lookup.findStatic(mojIdentifier, "tryParse", MethodType.methodType(mojIdentifier, String.class));
            } else if (fabricLoader != null) {
                Class<?> classMappingResolver = Class.forName("net.fabricmc.loader.api.MappingResolver");
                Object mappingResolver = lookup.findVirtual(fabricLoader, "getMappingResolver", MethodType.methodType(classMappingResolver)).invoke(loaderInstance);

                String mappedIdentifierClassName = (String) lookup.findVirtual(
                        classMappingResolver, "mapClassName", MethodType.methodType(String.class, String.class, String.class)
                ).invoke(mappingResolver, "intermediary", "net.minecraft.class_2960");
                Class<?> mappedIdentifierClass = lookup.findClass(mappedIdentifierClassName);
                String mappedIdentifierFactoryMethodName = (String) lookup.findVirtual(
                        classMappingResolver, "mapMethodName", MethodType.methodType(String.class, String.class, String.class, String.class, String.class)
                ).invoke(mappingResolver, "intermediary", "net.minecraft.class_2960", "method_12829", "(Ljava/lang/String;)Lnet/minecraft/class_2960;");
                IDENTIFIER_TRY_PARSE_FACTORY = lookup.findStatic(mappedIdentifierClass, mappedIdentifierFactoryMethodName, MethodType.methodType(mappedIdentifierClass, String.class));
            } else {
                Class<?> identifierClass = lookup.findClass("net.minecraft.resources.ResourceLocation");
                IDENTIFIER_TRY_PARSE_FACTORY = lookup.findStatic(identifierClass, "tryParse", MethodType.methodType(identifierClass, String.class));
            }
        } catch (Throwable t) {
            throw new ExceptionInInitializerError(t);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> Error sneakyThrow0(Throwable t) throws T {
        throw (T) t;
    }

    static Error sneakyThrow(Throwable t) {
        throw sneakyThrow0(t);
    }

    static Path getConfigDir() {
        return CONFIG_DIR;
    }

    static @Nullable Object tryParseIdentifier(String s) {
        try {
            return IDENTIFIER_TRY_PARSE_FACTORY.invoke(s);
        } catch (Throwable t) {
            throw sneakyThrow(t);
        }
    }

    static Class<?> classOfIdentifier() {
        return IDENTIFIER_TRY_PARSE_FACTORY.type().returnType();
    }
}
