package com.juzizhen.morerespawnanchors.mixin;

import com.juzizhen.morerespawnanchors.block.BaseRespawnAnchor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

import static com.juzizhen.morerespawnanchors.MoreRespawnAnchors.respawnAfterCredits;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "findRespawnPosition", at = @At("RETURN"), cancellable = true)
    private static void addNewAnchors(ServerWorld world, BlockPos pos, float f, boolean bl, boolean bl2, CallbackInfoReturnable<Optional<Vec3d>> cir) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();

        if (respawnAfterCredits) {
            ServerWorld overworld = world.getServer().getWorld(World.OVERWORLD);
            if (overworld != null) {
                BlockPos overworldSpawnPos = overworld.getSpawnPos();
                respawnAfterCredits = false;
                cir.setReturnValue(Optional.of(new Vec3d(overworldSpawnPos.getX(), overworldSpawnPos.getY(), overworldSpawnPos.getZ())));
            }
        }
        else if (block instanceof BaseRespawnAnchor) {
            BaseRespawnAnchor respawnAnchor = (BaseRespawnAnchor) block;
            if (blockState.get(respawnAnchor.getChargesProperty()) > 0 && respawnAnchor.isDimension(world)) {
                Optional<Vec3d> respawnPosition = RespawnAnchorBlock.findRespawnPosition(EntityType.PLAYER, world, pos);
                if (!bl2 && respawnPosition.isPresent()) {
                    world.setBlockState(pos, blockState.with(respawnAnchor.getChargesProperty(), blockState.get(respawnAnchor.getChargesProperty()) - 1), 3);
                }

                cir.setReturnValue(respawnPosition);
            }
        }
    }
}