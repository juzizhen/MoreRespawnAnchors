package com.juzizhen.morerespawnanchors.mixin.entity;

import com.juzizhen.morerespawnanchors.block.BaseRespawnAnchor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(
            method = "respawnPlayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;onSpawn()V")
    )
    public void playDepletedSoundOnSpawn(ServerPlayerEntity player, boolean alive, Entity.RemovalReason removalReason,
                                         CallbackInfoReturnable<ServerPlayerEntity> cir) {

        ServerPlayerEntity newPlayer = cir.getReturnValue();
        if (newPlayer == null || alive) return;

        ServerWorld world = newPlayer.getServerWorld();
        Vec3d pos = newPlayer.getPos();
        BlockPos center = BlockPos.ofFloored(pos);

        for (BlockPos bp : BlockPos.iterate(center.add(-2, -1, -2), center.add(2, 1, 2))) {
            BlockState state = world.getBlockState(bp);
            if (state.getBlock() instanceof BaseRespawnAnchor) {
                newPlayer.networkHandler.sendPacket(new PlaySoundS2CPacket(
                        Registries.SOUND_EVENT.entryOf(SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.getKey()
                                .orElseThrow(() -> new IllegalStateException("SoundEvent key not found"))),
                        SoundCategory.BLOCKS,
                        bp.getX(), bp.getY(), bp.getZ(),
                        1.0F, 1.0F,
                        0L
                ));
                break;
            }
        }
    }
}
