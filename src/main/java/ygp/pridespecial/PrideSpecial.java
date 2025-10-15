package ygp.pridespecial;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import io.github.queerbric.pride.PrideFlag;
import io.github.queerbric.pride.shape.PrideFlagShape;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.random.RandomGenerator;

public final class PrideSpecial {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger LOGGER = LogManager.getLogger();
    private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    private static List<PrideOverrideConfig> prideOverrideConfigs;
    private PrideSpecial() {}

    private static void init0() {
        BuiltinFlagShapes.init();

        var configFile = Platform.getConfigDir().resolve("pridespecial.json");
        try (var reader = Files.newBufferedReader(configFile)) {
            var arr = GSON.fromJson(reader, JsonArray.class);
            List<PrideOverrideConfig> configs = new ArrayList<>();
            arr.forEach(e -> configs.add(PrideOverrideConfig.fromJson(e)));
            prideOverrideConfigs = configs;
        } catch (Exception e) {
            if (e instanceof NoSuchFileException) {
                JsonArray defaultArr = PrideOverrideConfig.getDefault();
                try (var writer = Files.newBufferedWriter(configFile)) {
                    GSON.toJson(defaultArr, writer);
                } catch (Exception e2) {
                    e.addSuppressed(e2);
                }
            }
            LOGGER.warn("Failed to load {}", configFile, e);
            prideOverrideConfigs = Collections.singletonList(PrideOverrideConfig.getDefaultConfig());
        }
    }

    @Contract("null->null")
    @SuppressWarnings("unused") // entrypoint
    public static @Nullable PrideFlag select(@Nullable RandomGenerator random) {
        if (random == null) return null;

        List<PrideOverrideConfig> configs = prideOverrideConfigs;
        if (configs == null || configs.isEmpty()) return null;

        PrideOverrideConfig config = configs.stream()
                .filter(cfg -> STACK_WALKER.walk(
                        cfg.isReverse() ? s -> s.noneMatch(cfg) : s -> s.anyMatch(cfg)
                ))
                .findFirst()
                .orElse(null);

        List<PrideFlagShape> shapes;
        if (config == null || (shapes = config.flags()).isEmpty()) return null;
        PrideFlagShape shape = shapes.get(random.nextInt(shapes.size()));
        return new PrideFlag("pridespecial", shape) {
            @Override
            public String toString() {
                return super.toString() + " [pridespecial]";
            }
        };
    }

    static {
        init0();
    }

    @SuppressWarnings("unused")
    public static void init() {
        // just load the class
    }
}
