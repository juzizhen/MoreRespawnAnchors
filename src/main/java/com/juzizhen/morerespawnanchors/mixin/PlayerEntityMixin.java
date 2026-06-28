package com.juzizhen.morerespawnanchors.mixin;

import com.juzizhen.morerespawnanchors.block.BaseRespawnAnchor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Constructor;
import java.util.Optional;

import static com.juzizhen.morerespawnanchors.MoreRespawnAnchors.respawnAfterCredits;

@Mixin(ServerPlayerEntity.class)
public class PlayerEntityMixin {

    private static Constructor<?> respawnPosConstructor;

    private static Object createRespawnPos(Vec3d pos, float yaw) {
        try {
            if (respawnPosConstructor == null) {
                for (Class<?> inner : ServerPlayerEntity.class.getDeclaredClasses()) {
                    for (Constructor<?> ctor : inner.getDeclaredConstructors()) {
                        Class<?>[] params = ctor.getParameterTypes();
                        if (params.length == 2 && params[0] == Vec3d.class && params[1] == float.class) {
                            ctor.setAccessible(true);
                            respawnPosConstructor = ctor;
                            break;
                        }
                    }
                    if (respawnPosConstructor != null) break;
                }
                if (respawnPosConstructor == null) {
                    throw new RuntimeException("RespawnPos constructor not found");
                }
            }
            return respawnPosConstructor.newInstance(pos, yaw);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create RespawnPos", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "findRespawnPosition", at = @At("RETURN"), cancellable = true)
    private static void addNewAnchors(ServerWorld world, BlockPos pos, float f, boolean bl, boolean bl2, CallbackInfoReturnable cir) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();

        if (respawnAfterCredits) {
            ServerWorld overworld = world.getServer().getWorld(World.OVERWORLD);
            if (overworld != null) {
                BlockPos overworldSpawnPos = overworld.getSpawnPos();
                respawnAfterCredits = false;
                Vec3d spawnVec = new Vec3d(overworldSpawnPos.getX(), overworldSpawnPos.getY(), overworldSpawnPos.getZ());
                cir.setReturnValue(Optional.of(createRespawnPos(spawnVec, 0.0F)));
            }
        }
        else if (block instanceof BaseRespawnAnchor) {
            BaseRespawnAnchor respawnAnchor = (BaseRespawnAnchor) block;
            if (blockState.get(respawnAnchor.getChargesProperty()) > 0 && respawnAnchor.isDimension(world)) {
                Optional<Vec3d> respawnPosition = RespawnAnchorBlock.findRespawnPosition(EntityType.PLAYER, world, pos);
                if (!bl2 && respawnPosition.isPresent()) {
                    world.setBlockState(pos, blockState.with(respawnAnchor.getChargesProperty(), blockState.get(respawnAnchor.getChargesProperty()) - 1), 3);
                }

                cir.setReturnValue(respawnPosition.map(v -> createRespawnPos(v, f)));
            }
        }
    }
}
