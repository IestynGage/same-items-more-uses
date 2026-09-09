package com.same.items.client;

import com.same.items.block.MoreUsesBlocks;
import com.same.items.entity.MoreUsesEntityTypes;
import java.util.List;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class MoreUsesClient implements ClientModInitializer {

	private static final int GUNPOWDER_TRAIL_COLOR = 0xFF2B2B2B;

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(MoreUsesEntityTypes.SLIME_PROJECTILE, ThrownItemRenderer::new);
		EntityRendererRegistry.register(MoreUsesEntityTypes.MAGMA_CREAM_PROJECTILE, ThrownItemRenderer::new);
		BlockColorRegistry.register(List.of(BlockTintSources.constant(GUNPOWDER_TRAIL_COLOR)), MoreUsesBlocks.GUNPOWDER_TRAIL);
	}
}