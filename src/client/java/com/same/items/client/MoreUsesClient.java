package com.same.items.client;

import com.same.items.block.GunpowderTrailBlock;
import com.same.items.block.MoreUsesBlocks;
import com.same.items.entity.MoreUsesEntityTypes;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class MoreUsesClient implements ClientModInitializer {

	private static final int GUNPOWDER_TRAIL_COLOR = 0xFF2B2B2B;
	private static final int GUNPOWDER_TRAIL_LIT_COLOR = 0xFFFF7A00;

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(MoreUsesEntityTypes.SLIME_PROJECTILE, ThrownItemRenderer::new);
		EntityRendererRegistry.register(MoreUsesEntityTypes.MAGMA_CREAM_PROJECTILE, ThrownItemRenderer::new);
		BlockColorRegistry.register(List.of(new BlockTintSource() {
			@Override
			public int color(final BlockState state) {
				return state.getValue(GunpowderTrailBlock.LIT) ? GUNPOWDER_TRAIL_LIT_COLOR : GUNPOWDER_TRAIL_COLOR;
			}

			@Override
			public Set<Property<?>> relevantProperties() {
				return Set.of(GunpowderTrailBlock.LIT);
			}
		}), MoreUsesBlocks.GUNPOWDER_TRAIL);
	}
}