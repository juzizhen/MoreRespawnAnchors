package com.juzizhen.morerespawnanchors.mixin;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.juzizhen.morerespawnanchors.MoreRespawnAnchors.respawnAfterCredits;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "getSpawnPointDimension", at = @At("RETURN"), cancellable = true)
    public void changeSpawnDimension(CallbackInfoReturnable<RegistryKey<World>> cir){
        if (respawnAfterCredits) {
            cir.setReturnValue(World.OVERWORLD);
        }
    }
}