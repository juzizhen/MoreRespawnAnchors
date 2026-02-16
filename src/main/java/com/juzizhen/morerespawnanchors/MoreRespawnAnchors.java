package com.juzizhen.morerespawnanchors;

import com.juzizhen.morerespawnanchors.block.BaseRespawnAnchor;
import com.juzizhen.morerespawnanchors.block.EndRespawnAnchor;
import com.juzizhen.morerespawnanchors.block.NetheriteEndRespawnAnchor;
import com.juzizhen.morerespawnanchors.block.NetheriteRepawnAnchor;
import com.juzizhen.morerespawnanchors.block.entity.EndRespawnAnchorBlockEntity;
import com.juzizhen.morerespawnanchors.block.entity.NetheriteEndRespawnAnchorBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MoreRespawnAnchors implements ModInitializer {

    public static final NetheriteRepawnAnchor NETHERITE_RESPAWN_ANCHOR = new NetheriteRepawnAnchor(FabricBlockSettings.of().mapColor(MapColor.STONE_GRAY).requiresTool().strength(50.0F, 1200.0F)
            .luminance(BaseRespawnAnchor::getLightLevelFromState));

    public static final EndRespawnAnchor END_RESPAWN_ANCHOR = new EndRespawnAnchor(FabricBlockSettings.of().mapColor(MapColor.STONE_GRAY).requiresTool().strength(50.0F, 1200.0F)
            .luminance(BaseRespawnAnchor::getLightLevelFromState));

    public static final NetheriteEndRespawnAnchor NETHERITE_END_RESPAWN_ANCHOR = new NetheriteEndRespawnAnchor(FabricBlockSettings.of().mapColor(MapColor.STONE_GRAY).requiresTool().strength(50.0F, 1200.0F)
            .luminance(BaseRespawnAnchor::getLightLevelFromState));
    public static final ItemGroup ITEM_GROUP = Registry.register(
            Registries.ITEM_GROUP,
            new Identifier("morerespawnanchors", "general"),
            FabricItemGroup.builder().icon(() -> new ItemStack(NETHERITE_RESPAWN_ANCHOR))
                    .displayName(Text.translatable("itemGroup.morerespawnanchors.general"))
                    .entries((displayContext, entries) -> {
                        entries.add(NETHERITE_RESPAWN_ANCHOR);
                        entries.add(END_RESPAWN_ANCHOR);
                        entries.add(NETHERITE_END_RESPAWN_ANCHOR);
                    })
                    .build()
    );
    public static BlockEntityType<EndRespawnAnchorBlockEntity> END_RESPAWN_ANCHOR_BLOCK_ENTITY;
    public static BlockEntityType<NetheriteEndRespawnAnchorBlockEntity> NETHERITE_END_RESPAWN_ANCHOR_BLOCK_ENTITY;
    public static boolean respawnAfterCredits = false;

    @Override
    public void onInitialize() {

        Registry.register(Registries.BLOCK, new Identifier("morerespawnanchors", "netherite_respawn_anchor"),
                NETHERITE_RESPAWN_ANCHOR);
        Registry.register(Registries.ITEM, new Identifier("morerespawnanchors", "netherite_respawn_anchor"),
                new BlockItem(NETHERITE_RESPAWN_ANCHOR, new Item.Settings().fireproof()));

        Registry.register(Registries.BLOCK, new Identifier("morerespawnanchors", "end_respawn_anchor"),
                END_RESPAWN_ANCHOR);
        Registry.register(Registries.ITEM, new Identifier("morerespawnanchors", "end_respawn_anchor"),
                new BlockItem(END_RESPAWN_ANCHOR, new Item.Settings()));

        Registry.register(Registries.BLOCK, new Identifier("morerespawnanchors", "netherite_end_respawn_anchor"),
                NETHERITE_END_RESPAWN_ANCHOR);
        Registry.register(Registries.ITEM, new Identifier("morerespawnanchors", "netherite_end_respawn_anchor"),
                new BlockItem(NETHERITE_END_RESPAWN_ANCHOR, new Item.Settings().fireproof()));

        END_RESPAWN_ANCHOR_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("morerespawnanchors", "end_respawn_anchor"),
                FabricBlockEntityTypeBuilder.create(EndRespawnAnchorBlockEntity::new, END_RESPAWN_ANCHOR).build(null));

        NETHERITE_END_RESPAWN_ANCHOR_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("morerespawnanchors", "netherite_end_respawn_anchor"),
                FabricBlockEntityTypeBuilder.create(NetheriteEndRespawnAnchorBlockEntity::new, NETHERITE_END_RESPAWN_ANCHOR).build(null));

        DispenserBlock.registerBehavior(Items.ENDER_PEARL, new FallibleItemDispenserBehavior() {
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
                BlockPos blockPos = pointer.getPos().offset(direction);
                World world = pointer.getWorld();
                BlockState blockState = world.getBlockState(blockPos);
                Block block = blockState.getBlock();
                this.setSuccess(true);
                if (blockState.isOf(END_RESPAWN_ANCHOR) || blockState.isOf(NETHERITE_END_RESPAWN_ANCHOR)) {
                    BaseRespawnAnchor respawnAnchor = (BaseRespawnAnchor) block;
                    if (blockState.get(respawnAnchor.getChargesProperty()) != respawnAnchor.getMaxCharges()) {
                        respawnAnchor.charge(world, blockPos, blockState);
                        stack.decrement(1);
                    } else {
                        this.setSuccess(false);
                    }

                    return stack;
                } else {
                    return super.dispenseSilently(pointer, stack);
                }
            }
        });


    }
}
