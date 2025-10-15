package ygp.pridespecial;

import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;

final class BuiltinFlagShapes {
    static final ConstantDynamic CONDY_CLASS_DATA = new ConstantDynamic(
            "$-TYPE", "Ljava/lang/Object;",
            new Handle(
                    Opcodes.H_INVOKESTATIC,
                    "java/lang/invoke/MethodHandles",
                    "classData",
                    "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Object;",
                    false
            )
    );

    public static void init() {
        SingleColorFlagShapeFactory.init();
        BlankFlagShapeFactory.init();
    }
}
