package com.same.items.client;

import com.same.items.entity.MoreUsesEntityTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class MoreUsesClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(MoreUsesEntityTypes.SLIME_PROJECTILE, ThrownItemRenderer::new);
		EntityRendererRegistry.register(MoreUsesEntityTypes.MAGMA_CREAM_PROJECTILE, ThrownItemRenderer::new);
	}
}