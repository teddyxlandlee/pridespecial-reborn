package ygp.pridespecial;

import com.mojang.serialization.MapCodec;
import io.github.queerbric.pride.shape.PrideFlagShape;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.InstructionAdapter;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.objectweb.asm.commons.InstructionAdapter.OBJECT_TYPE;

public final class BlankFlagShapeFactory implements Opcodes {
    static void init() {
        // Load class.
    }

    public static PrideFlagShape.Type getShapeType() {
        return SHAPE_TYPE;
    }

    @Contract("->new")
    public static PrideFlagShape create() {
        try {
            return (PrideFlagShape) lookupInSCFlagShapeClass.findConstructor(
                    lookupInSCFlagShapeClass.lookupClass(), MethodType.methodType(void.class)
            ).invoke();
        } catch (Throwable e) {
            throw new IncompatibleClassChangeError();
        }
    }

    private BlankFlagShapeFactory() {}

    private static final MethodHandles.Lookup lookupInSCFlagShapeClass;
    private static final MapCodec<? extends PrideFlagShape> CODEC = codecFactory();
    public static final String ID = "pridespecial:blank";
    private static final PrideFlagShape.Type SHAPE_TYPE;

    static {
        PrideFlagShape.Type type;
        var lookup = MethodHandles.lookup();
        try {
            MethodHandle handleRegister = lookup.findStatic(PrideFlagShape.Type.class, "register", MethodType.methodType(
                    PrideFlagShape.Type.class,
                    Platform.classOfIdentifier(),
                    MapCodec.class
            ));
            type = (PrideFlagShape.Type) handleRegister.invoke(
                    Objects.requireNonNull(Platform.tryParseIdentifier(ID), "Invalid identifier: " + ID),
                    CODEC
            );
        } catch (Throwable t) {
            throw new ExceptionInInitializerError(t);
        }

        try {
            lookupInSCFlagShapeClass = lookup.defineHiddenClassWithClassData(
                    genShapeClass(), type, true,
                    MethodHandles.Lookup.ClassOption.NESTMATE, MethodHandles.Lookup.ClassOption.STRONG
            );
        } catch (IllegalAccessException e) {
            throw new IncompatibleClassChangeError("Cannot define class");
        }

        SHAPE_TYPE = type;
    }

    private static MapCodec<? extends PrideFlagShape> codecFactory() {
        return MapCodec.unit(BlankFlagShapeFactory::create);
    }

    private static byte[] genShapeClass() {
        List<Method> methodsToOverride = Arrays.stream(PrideFlagShape.class.getMethods())
                .filter(m -> !isDerivedFromObject(m) && !Modifier.isStatic(m.getModifiers()) && Modifier.isAbstract(m.getModifiers()))
                .toList();

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        InstructionAdapter mv;
        cw.visit(
                V19, ACC_SUPER | ACC_SYNTHETIC | ACC_FINAL,
                Type.getInternalName(BlankFlagShapeFactory.class).concat("-BlankFlagShape"),
                /*signature*/null,
                "java/lang/Object",
                new String[]{Type.getInternalName(PrideFlagShape.class)}
        );

        mv = new InstructionAdapter(cw.visitMethod(0, "<init>", "()V", null, null));
        {
            mv.visitCode();

            mv.load(0, OBJECT_TYPE);
            mv.invokespecial("java/lang/Object", "<init>", "()V", false);
            mv.areturn(Type.VOID_TYPE);

            mv.visitMaxs(-1, -1);
            mv.visitEnd();
        }

        mv = new InstructionAdapter(cw.visitMethod(ACC_PUBLIC | ACC_FINAL, "toString", "()Ljava/lang/String;", null, null));
        {
            mv.visitCode();

            mv.aconst("BlankFlagShape");
            mv.areturn(OBJECT_TYPE);

            mv.visitMaxs(-1, -1);
            mv.visitEnd();
        }

        // in case problematic override
        mv = new InstructionAdapter(cw.visitMethod(
                ACC_PUBLIC | ACC_FINAL, "clone", "()Ljava/lang/Object;",
                null, new String[]{Type.getInternalName(CloneNotSupportedException.class)}
        ));
        {
            mv.visitCode();

            mv.load(0, OBJECT_TYPE);
            mv.invokespecial("java/lang/Object", "clone", "()Ljava/lang/Object;", false);
            mv.areturn(OBJECT_TYPE);

            mv.visitMaxs(-1, -1);
            mv.visitEnd();
        }

        for (Method method : methodsToOverride) {
            mv = new InstructionAdapter(cw.visitMethod(
                    ACC_PUBLIC | ACC_FINAL, method.getName(), Type.getMethodDescriptor(method),
                    /*signature*/null, /*exceptions*/null
            ));
            mv.visitCode();

            if ("type".equals(method.getName()) && method.getParameterCount() == 0) {
                // @NotNull Type type()
                mv.cconst(BuiltinFlagShapes.CONDY_CLASS_DATA);
                mv.checkcast(Type.getType(method.getReturnType()));
                mv.areturn(OBJECT_TYPE);
            } else if ("render".equals(method.getName()) && method.getReturnType() == void.class) {
                // leave blank
                mv.nop();
                mv.areturn(Type.VOID_TYPE);
            } else {
                // unexpected method, will throw IncompatibleClassChangeError
                Type errorType = Type.getType(IncompatibleClassChangeError.class);
                mv.anew(errorType);
                mv.dup();
                mv.aconst(PrideFlagShape.class + " defines an unexpected abstract method: " + method);
                mv.invokespecial(errorType.getInternalName(), "<init>", "(Ljava/lang/String;)V", false);
                mv.athrow();
            }

            mv.visitMaxs(-1, -1);
            mv.visitEnd();
        }

        cw.visitEnd();
        return cw.toByteArray();
    }

    private static boolean isDerivedFromObject(@NotNull Method method) {
        return switch (method.getParameterCount()) {
            // equals(Ljava/lang/Object;)Z
            case 1 -> method.getReturnType() == boolean.class && method.getParameterTypes()[0] == Object.class;
            case 0 -> switch (method.getName()) {
                // clone()Ljava/lang/Object;
                case "clone" -> method.getReturnType() == Object.class;
                // hashCode()I
                case "hashCode" -> method.getReturnType() == int.class;
                // toString()Ljava/lang/String;
                case "toString" -> method.getReturnType() == String.class;
                default -> false;
            };
            default -> false;
        };
    }
}
