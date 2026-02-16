package com.juzizhen.morerespawnanchors.block.dispenser;

import com.juzizhen.morerespawnanchors.block.BaseRespawnAnchor;
import com.juzizhen.morerespawnanchors.block.EndRespawnAnchor;
import com.juzizhen.morerespawnanchors.block.NetheriteEndRespawnAnchor;
import net.minecraft.block.*;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class NewGlowstoneDispenserBehavior extends FallibleItemDispenserBehavior {

    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
        BlockPos blockPos = pointer.getPos().offset(direction);
        World world = pointer.getWorld();
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        this.setSuccess(true);
        if (block instanceof BaseRespawnAnchor respawnAnchor && !(block instanceof EndRespawnAnchor) && !(block instanceof NetheriteEndRespawnAnchor)) {
            if (blockState.get(respawnAnchor.getChargesProperty()) != respawnAnchor.getMaxCharges()) {
                respawnAnchor.charge(world, blockPos, blockState);
                stack.decrement(1);
            } else {
                this.setSuccess(false);
            }

            return stack;
        }
        else if (blockState.isOf(Blocks.RESPAWN_ANCHOR)) {
            if (blockState.get(RespawnAnchorBlock.CHARGES) != 4) {
                RespawnAnchorBlock.charge(null, world, blockPos, blockState);
                stack.decrement(1);
            } else {
                this.setSuccess(false);
            }
            return stack;
        } else {
            return super.dispenseSilently(pointer, stack);
        }

    }

}
