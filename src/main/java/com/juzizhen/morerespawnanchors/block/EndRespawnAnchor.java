package com.juzizhen.morerespawnanchors.block;

import com.juzizhen.morerespawnanchors.block.entity.EndRespawnAnchorBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EndRespawnAnchor extends BaseRespawnAnchor implements BlockEntityProvider {
    public EndRespawnAnchor(Settings settings) {
        super(settings);
        setDefaultState(this.getDefaultState().with(getChargesProperty(), 0));
    }

    @Override
    protected boolean isChargeItem(ItemStack stack) {
        return stack.getItem() == Items.ENDER_PEARL;
    }

    @Override
    public boolean isDimension(World world) {
        return world.getRegistryKey().equals(World.END);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(getChargesProperty());
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EndRespawnAnchorBlockEntity(pos, state, getChargesProperty());
    }
}