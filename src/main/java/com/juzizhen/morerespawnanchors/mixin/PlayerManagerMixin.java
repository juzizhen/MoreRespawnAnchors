package com.juzizhen.morerespawnanchors.mixin;

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
import net.minecraft.world.TeleportTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Unique
    private final ThreadLocal<TeleportTarget> capturedTarget = new ThreadLocal<>();

    @ModifyVariable(
            method = "respawnPlayer",
            at = @At(value = "STORE"),
            name = "teleportTarget")
    private TeleportTarget captureTeleportTarget(TeleportTarget target) {
        capturedTarget.set(target);
        return target;
    }

    @Inject(method = "respawnPlayer", at = @At("RETURN"))
    public void playDepletedSound(ServerPlayerEntity player, boolean alive, Entity.RemovalReason removalReason,
                                  CallbackInfoReturnable<ServerPlayerEntity> cir) {

        TeleportTarget teleportTarget = capturedTarget.get();
        ServerPlayerEntity newPlayer = cir.getReturnValue();

        try {
            if (teleportTarget != null && !alive && newPlayer != null) {
                Vec3d pos = teleportTarget.pos();
                ServerWorld world = teleportTarget.world();
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
        } finally {
            capturedTarget.remove();
        }
    }
}