package com.juzizhen.morerespawnanchors.mixin;

import com.juzizhen.morerespawnanchors.block.dispenser.NewGlowstoneDispenserBehavior;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin {
    @Shadow @Final private static Map<Item, DispenserBehavior> BEHAVIORS;

    @Inject(method = "registerBehavior", at = @At("HEAD"), cancellable = true)
    private static void overwriteGlowstoneBehavior(ItemConvertible provider, DispenserBehavior behavior, CallbackInfo ci) {
        if (provider.asItem() == Items.GLOWSTONE) {
            FallibleItemDispenserBehavior newBehavior = new NewGlowstoneDispenserBehavior();
            BEHAVIORS.put(provider.asItem(), newBehavior);
            ci.cancel();
        }
    }
}
