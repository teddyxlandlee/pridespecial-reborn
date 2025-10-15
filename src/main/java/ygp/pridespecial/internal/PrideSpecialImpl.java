package ygp.pridespecial.internal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

final class PrideSpecialImpl {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String CLASS_PRIDE_FLAG = "io/github/queerbric/pride/PrideFlag";
    private static final String CLASS_PRIDE_SPECIAL = "ygp/pridespecial/PrideSpecial";
    private static final String METHOD_SELECT = "select";
    private static final String DESC_SELECT = "(Ljava/util/random/RandomGenerator;)L" + CLASS_PRIDE_FLAG + ';';
    private static boolean isTargetMethod(MethodNode m) {
        return "getRandomFlag".equals(m.name) && ("(Ljava/util/Random;)L" + CLASS_PRIDE_FLAG + ';').equals(m.desc) && (m.access & Opcodes.ACC_STATIC) != 0;
    }

    PrideSpecialImpl() {}

    public static void modifyNode(ClassNode node) {
        MethodNode m = node.methods.stream().filter(PrideSpecialImpl::isTargetMethod).findAny().orElse(null);
        if (m == null) {
            LOGGER.warn("Cannot find target method");
            return;
        }

        InsnList l = new InsnList();
        final LabelNode L_START, L_RETURN, L_CONTINUE;
        L_START = new LabelNode();
        L_RETURN = new LabelNode();
        L_CONTINUE = new LabelNode();

        l.add(new LineNumberNode(6901, L_START));
        l.add(new LineNumberNode(6902, L_RETURN));
        l.add(new LineNumberNode(6903, L_CONTINUE));
        l.add(L_START);
        l.add(new VarInsnNode(Opcodes.ALOAD, 0));
        l.add(new MethodInsnNode(Opcodes.INVOKESTATIC, CLASS_PRIDE_SPECIAL, METHOD_SELECT, DESC_SELECT, false));
        l.add(new InsnNode(Opcodes.DUP));
        l.add(new JumpInsnNode(Opcodes.IFNULL, L_CONTINUE));

        l.add(L_RETURN);
        l.add(new InsnNode(Opcodes.ARETURN));

        l.add(L_CONTINUE);
        l.add(new FrameNode(Opcodes.F_SAME1, -1, null, 1, new Object[]{"java/lang/Object"}));
        l.add(new InsnNode(Opcodes.POP));

        l.add(m.instructions);
        m.instructions = l;
    }
}
