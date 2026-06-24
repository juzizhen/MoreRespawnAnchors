package com.juzizhen.morerespawnanchors.mixin;

import com.juzizhen.morerespawnanchors.block.BaseRespawnAnchor;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "respawnPlayer", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void playDepletedSound(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir,
                                  BlockPos blockPos, float f, boolean bl, ServerWorld serverWorld,
                                  Optional optional2, ServerWorld serverWorld2, ServerPlayerEntity serverPlayerEntity) {

        if (blockPos != null && serverWorld != null) {
            BlockState blockState = serverWorld2.getBlockState(blockPos);
            if (blockState.getBlock() instanceof BaseRespawnAnchor && !alive) {

                serverPlayerEntity.networkHandler.sendPacket(new PlaySoundS2CPacket(
                        Registries.SOUND_EVENT.entryOf(SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.getKey()
                                .orElseThrow(() -> new IllegalStateException("SoundEvent key not found"))),
                        SoundCategory.BLOCKS,
                        blockPos.getX(), blockPos.getY(), blockPos.getZ(),
                        1.0F, 1.0F,
                        0L
                ));
            }
        }
    }
}