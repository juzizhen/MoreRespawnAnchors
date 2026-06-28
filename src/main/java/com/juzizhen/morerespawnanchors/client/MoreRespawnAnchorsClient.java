package com.juzizhen.morerespawnanchors.client;

import com.juzizhen.morerespawnanchors.MoreRespawnAnchors;
import com.juzizhen.morerespawnanchors.client.renderer.EndRespawnAnchorBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

@Environment(EnvType.CLIENT)
public class MoreRespawnAnchorsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), MoreRespawnAnchors.END_RESPAWN_ANCHOR);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), MoreRespawnAnchors.NETHERITE_END_RESPAWN_ANCHOR);
        BlockEntityRendererFactories.register(MoreRespawnAnchors.END_RESPAWN_ANCHOR_BLOCK_ENTITY, EndRespawnAnchorBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(MoreRespawnAnchors.NETHERITE_END_RESPAWN_ANCHOR_BLOCK_ENTITY, EndRespawnAnchorBlockEntityRenderer::new);
    }
}
