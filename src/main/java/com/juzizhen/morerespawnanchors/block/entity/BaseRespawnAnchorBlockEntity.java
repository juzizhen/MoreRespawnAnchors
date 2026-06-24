package com.juzizhen.morerespawnanchors.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;

public class BaseRespawnAnchorBlockEntity extends EndPortalBlockEntity {
    public IntProperty charges;

    public BaseRespawnAnchorBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public BaseRespawnAnchorBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState, IntProperty charges) {
        super(blockEntityType, blockPos, blockState);
        this.charges = charges;
    }
}