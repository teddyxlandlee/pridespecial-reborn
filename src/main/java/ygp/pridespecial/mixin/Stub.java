package ygp.pridespecial.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import ygp.pridespecial.internal.Proud;

@Pseudo
@Mixin(targets = "io.github.queerbric.pride.PrideFlags", remap = false)
@Proud
abstract class Stub {
}
