package com.same.bucket.neoforge;

import com.same.bucket.SameBucketMoreUses;
import com.same.bucket.recipe.SameBucketMoreUsesRecipeSerializers;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(SameBucketMoreUses.MOD_ID)
public class SameBucketMoreUsesNeoForge {

	public SameBucketMoreUsesNeoForge(IEventBus modEventBus) {
		modEventBus.addListener(SameBucketMoreUsesNeoForge::register);

		SameBucketMoreUses.init();
	}

	private static void register(RegisterEvent event) {
		event.register(Registries.RECIPE_SERIALIZER, helper -> helper.register(
				SameBucketMoreUsesRecipeSerializers.POWDER_SNOW_BUCKET_REMAINDER_ID,
				SameBucketMoreUsesRecipeSerializers.POWDER_SNOW_BUCKET_REMAINDER
		));
	}
}
