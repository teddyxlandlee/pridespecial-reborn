package ygp.pridespecial;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.queerbric.pride.data.PrideData;
import io.github.queerbric.pride.shape.PrideFlagShape;
import io.github.queerbric.pride.shape.VerticalPrideFlagShape;
import it.unimi.dsi.fastutil.ints.IntList;
import org.jetbrains.annotations.Contract;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.InstructionAdapter;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.objectweb.asm.commons.InstructionAdapter.OBJECT_TYPE;

public final class SingleColorFlagShapeFactory implements Opcodes {
    static void init() {
        // Load class.
    }

    public static PrideFlagShape.Type getShapeType() {
        return SHAPE_TYPE;
    }

    @Contract("_->new")
    public static PrideFlagShape newFlag(int color) {
        return createFlagInternal(color);
    }

    private SingleColorFlagShapeFactory() {}

    private static final MethodHandles.Lookup lookupInSCFlagShapeClass;
    private static final MapCodec<? extends PrideFlagShape> CODEC = codecFactory();
    public static final String ID = "pridespecial:single";
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

    private static AbstractSingleColorFlagShape createFlagInternal(int color) {
        MethodHandle constructor;
        try {
            constructor = lookupInSCFlagShapeClass.findConstructor(lookupInSCFlagShapeClass.lookupClass(), MethodType.methodType(void.class, int.class));
        } catch (Exception e) {
            throw new IncompatibleClassChangeError();
        }

        try {
            return (AbstractSingleColorFlagShape) constructor.invoke(color);
        } catch (Throwable e) {
            throw new IncompatibleClassChangeError();
        }
    }

    private static MapCodec<? extends AbstractSingleColorFlagShape> codecFactory() {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                PrideData.COLOR_CODEC.fieldOf("color").forGetter(ColorProvider::getColor)
        ).apply(instance, SingleColorFlagShapeFactory::createFlagInternal));
    }

    private static byte[] genShapeClass() {
        List<Method> methodsToOverride = Arrays.stream(PrideFlagShape.class.getMethods())
                .filter(m -> !Modifier.isStatic(m.getModifiers()))
                .toList();

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        InstructionAdapter mv;
        final String superClassName = Type.getInternalName(AbstractSingleColorFlagShape.class);
        final String prideFlagShapeClassName = Type.getInternalName(PrideFlagShape.class);
        cw.visit(
                V19, ACC_SUPER | ACC_SYNTHETIC | ACC_FINAL,
                Type.getInternalName(SingleColorFlagShapeFactory.class).concat("-SingleColorFlagShape"),
                /*signature*/null,
                superClassName,
                new String[]{Type.getInternalName(PrideFlagShape.class), Type.getInternalName(ColorProvider.class)}
        );

        mv = new InstructionAdapter(cw.visitMethod(0, "<init>", "(I)V", null, null));
        {
            mv.visitCode();

            mv.load(0, OBJECT_TYPE);
            mv.load(1, Type.INT_TYPE);
            mv.invokespecial(superClassName, "<init>", "(I)V", false);
            mv.areturn(Type.VOID_TYPE);

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
            } else {
                // delegate
                // "fake this": get wrapped()
                mv.load(0, OBJECT_TYPE);
                mv.invokevirtual(superClassName, "wrapped", "()L" + prideFlagShapeClassName + ';', false);

                // each argument
                final Class<?>[] parameterTypes = method.getParameterTypes();
                for (int i = 0; i < parameterTypes.length; i++) {
                    mv.load(i + 1, Type.getType(parameterTypes[i]));
                }

                mv.invokeinterface(prideFlagShapeClassName, method.getName(), Type.getMethodDescriptor(method));
                mv.areturn(Type.getReturnType(method));
            }

            mv.visitMaxs(-1, -1);
            mv.visitEnd();
        }

        cw.visitEnd();
        return cw.toByteArray();
    }

    private static abstract class AbstractSingleColorFlagShape implements PrideFlagShape, ColorProvider {
        private final int color;
        private final PrideFlagShape wrapped;

        AbstractSingleColorFlagShape(int color) {
            this.color = color;
            this.wrapped = new VerticalPrideFlagShape(IntList.of(color));
        }

        @Override
        public final int getColor() {
            return color;
        }

        @SuppressWarnings("unused") // called by subclasses
        protected final PrideFlagShape wrapped() {
            return this.wrapped;
        }

        @Override
        public String toString() {
            return "SingleColorFlagShape[color=" + color + ']';
        }
    }
}
