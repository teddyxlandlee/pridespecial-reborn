package ygp.pridespecial;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import io.github.queerbric.pride.PrideFlag;
import io.github.queerbric.pride.PrideFlags;
import io.github.queerbric.pride.shape.PrideFlagShape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

record PrideOverrideConfig(String caller, List<PrideFlagShape> flags) implements Predicate<StackWalker.StackFrame> {
    PrideOverrideConfig {
        Objects.requireNonNull(caller, "caller");
        Objects.requireNonNull(flags, "flags");
        flags = Collections.unmodifiableList(flags);
    }

    boolean isReverse() {
        return caller.startsWith("!");
    }

    @Override
    public boolean test(StackWalker.StackFrame stackFrame) {
        if (isReverse()) {
            return !implTest(caller.substring(1), stackFrame);
        } else {
            return implTest(caller, stackFrame);
        }
    }

    private static boolean implTest(String caller, StackWalker.StackFrame stackFrame) {
        if (caller.isBlank()) return true;
        return stackFrame.getClassName().equals(caller.replace('/', '.'));
    }

    static PrideOverrideConfig fromJson(JsonElement json) {
        if (!json.isJsonObject()) throw new JsonParseException("Not a json object");
        JsonObject obj = json.getAsJsonObject();

        json = obj.get("caller");
        String caller;
        if (json == null) {
            caller = "";
        } else if (!isJsonString(json)) {
            throw new JsonParseException("Not a string: " + json);
        } else {
            caller = json.getAsString();
        }

        List<PrideFlagShape> flags;
        json = obj.get("flags");

        if (json.isJsonArray()) {
            // multiple flag candidates
            flags = new ArrayList<>();
            json.getAsJsonArray().forEach(e -> flags.add(flagFromJson(e)));
        } else {
            // single flag
            flags = Collections.singletonList(flagFromJson(json));
        }

        return new PrideOverrideConfig(caller, flags);
    }

    private static PrideFlagShape flagFromJson(JsonElement json) {
        if (isJsonString(json)) {
            PrideFlag flag = PrideFlags.getFlag(json.getAsString());
            if (flag == null) throw new JsonParseException("Invalid flag: " + json);
            return flag.getShape();
        } else {
            return PrideFlagShape.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
        }
    }

    private static boolean isJsonString(JsonElement json) {
        return json != null && json.isJsonPrimitive() && json.getAsJsonPrimitive().isString();
    }

    private static final Gson GSON = new Gson();
    private static final JsonArray DEFAULT_ARR = GSON.fromJson("""
            [{"caller":"","flags":[{"colors":["#ff0000","#ffff00","#0000ff","#ffffff","#000000"]}]}]""",
            JsonArray.class
    );
    static JsonArray getDefault() {
        return DEFAULT_ARR;
    }

    static PrideOverrideConfig getDefaultConfig() {
        return fromJson(DEFAULT_ARR.iterator().next());
    }
}
