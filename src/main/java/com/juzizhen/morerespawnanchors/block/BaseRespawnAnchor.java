package com.juzizhen.morerespawnanchors.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

import java.util.Optional;
import java.util.Random;

public class BaseRespawnAnchor extends Block {

    public static final IntProperty CHARGES = IntProperty.of("charges", 0, 12);

    public BaseRespawnAnchor(Settings settings) {
        super(settings);
    }

    public static int getLightLevelFromState(BlockState state) {
        final BaseRespawnAnchor anchor = (BaseRespawnAnchor) state.getBlock();
        return getLightLevel(state.get(anchor.getChargesProperty()), 15, anchor.getMaxCharges());
    }

    public static int getLightLevel(int chargeState, int maxLevel, float maxCharges) {
        return MathHelper.floor(chargeState / maxCharges * (float) maxLevel);
    }

    public int getMaxCharges() {
        return 4;
    }

    public IntProperty getChargesProperty() {
        return CHARGES;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (hand == Hand.MAIN_HAND && !isChargeItem(itemStack) && isChargeItem(player.getStackInHand(Hand.OFF_HAND))) {
            return ActionResult.PASS;
        } else if (isChargeItem(itemStack) && canCharge(state)) {
            charge(world, pos, state);
            if (!player.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }

            return ActionResult.success(world.isClient);
        } else if (state.get(getChargesProperty()) == 0) {
            return ActionResult.PASS;
        } else if (!isDimension(world)) {
            if (!world.isClient) {
                this.explode(world, pos);
            }

            return ActionResult.success(world.isClient);
        } else if (isChargeItem(itemStack) && !canCharge(state)) {
            if (!world.isClient) {
                this.explode(world, pos);
                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }
            }
            return ActionResult.success(world.isClient);
        } else {
            if (!world.isClient) {
                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
                if (serverPlayerEntity.getSpawnPointPosition() == null ||
                        serverPlayerEntity.getSpawnPointDimension() != world.getRegistryKey() ||
                        !serverPlayerEntity.getSpawnPointPosition().equals(pos)) {

                    serverPlayerEntity.setSpawnPoint(world.getRegistryKey(), pos, 0.0F, false, true);
                    world.playSound(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D,
                            SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.CONSUME;
        }
    }

    public boolean isDimension(World world) {
        return world.getDimension().respawnAnchorWorks();
    }

    private boolean canCharge(BlockState state) {
        return state.get(getChargesProperty()) < getMaxCharges();
    }

    protected boolean isChargeItem(ItemStack stack) {
        return stack.getItem() == Items.GLOWSTONE;
    }

    public void charge(World world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state.with(getChargesProperty(), state.get(getChargesProperty()) + 1), 3);
        world.playSound(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return getLightLevel(state.get(getChargesProperty()), 15, getMaxCharges());
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    private void explode(World world, final BlockPos explodedPos) {
        world.removeBlock(explodedPos, false);
        final boolean bl2 = world.getFluidState(explodedPos.up()).isIn(FluidTags.WATER);
        ExplosionBehavior explosionBehavior = new ExplosionBehavior() {
            public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
                return pos.equals(explodedPos) && bl2 ? Optional.of(Blocks.WATER.getBlastResistance()) : super.getBlastResistance(explosion, world, pos, blockState, fluidState);
            }
        };
        world.createExplosion(null, world.getDamageSources().badRespawnPoint(explodedPos.toCenterPos()), explosionBehavior, (double) explodedPos.getX() + 0.5D, (double) explodedPos.getY() + 0.5D, (double) explodedPos.getZ() + 0.5D, 5.0F, true, World.ExplosionSourceType.BLOCK);
    }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(getChargesProperty()) != 0) {
            if (random.nextInt(100) == 0) {
                world.playSound(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.BLOCK_RESPAWN_ANCHOR_AMBIENT, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            double x = (double) pos.getX() + 0.5D + (0.5D - random.nextDouble());
            double y = (double) pos.getY() + 1.0D;
            double z = (double) pos.getZ() + 0.5D + (0.5D - random.nextDouble());
            double velocityY = (double) random.nextFloat() * 0.04D;
            world.addParticle(ParticleTypes.REVERSE_PORTAL, x, y, z, 0.0D, velocityY, 0.0D);
        }
    }
}