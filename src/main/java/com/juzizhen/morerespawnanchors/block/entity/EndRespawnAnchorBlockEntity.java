package com.juzizhen.morerespawnanchors.block.entity;

import com.juzizhen.morerespawnanchors.MoreRespawnAnchors;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;

public class EndRespawnAnchorBlockEntity extends BaseRespawnAnchorBlockEntity {
    public EndRespawnAnchorBlockEntity(BlockPos pos, BlockState state) {
        super(MoreRespawnAnchors.END_RESPAWN_ANCHOR_BLOCK_ENTITY, pos, state);
    }

    public EndRespawnAnchorBlockEntity(BlockPos pos, BlockState state, IntProperty charges) {
        super(MoreRespawnAnchors.END_RESPAWN_ANCHOR_BLOCK_ENTITY, pos, state, charges);
    }
}