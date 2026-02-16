package com.juzizhen.morerespawnanchors.block;


import com.juzizhen.morerespawnanchors.block.entity.NetheriteEndRespawnAnchorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class NetheriteEndRespawnAnchor extends BaseRespawnAnchor implements BlockEntityProvider {

    public static final IntProperty CHARGES = IntProperty.of("charges", 0, 12);

    public NetheriteEndRespawnAnchor(Settings settings) {
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
    public IntProperty getChargesProperty() {
        return CHARGES;
    }

    @Override
    public int getMaxCharges() {
        return 12;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(getChargesProperty());
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new NetheriteEndRespawnAnchorBlockEntity(pos, state, getChargesProperty());
    }


}
