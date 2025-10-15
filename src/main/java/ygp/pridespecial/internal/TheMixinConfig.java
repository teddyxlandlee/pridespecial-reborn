package ygp.pridespecial.internal;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class TheMixinConfig implements IMixinConfigPlugin {
    @Override
    public void onLoad(String s) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String s, String s1) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {

    }

    @Override
    public List<String> getMixins() {
        return Collections.emptyList();
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
    }

    @Override
    public void postApply(String target, ClassNode classNode, String mixin, IMixinInfo iMixinInfo) {
        if (mixin.endsWith("Stub") && classNode.invisibleAnnotations != null) {
            if (classNode.invisibleAnnotations.stream().anyMatch(an -> Type.getDescriptor(Proud.class).equals(an.desc))) {
                PrideSpecialImpl.modifyNode(classNode);
            }
        }
    }
}
