package com.same.bucket.fabric;

import com.same.bucket.SameBucketMoreUses;
import com.same.bucket.recipe.SameBucketMoreUsesRecipeSerializers;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class SameBucketMoreUsesFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		Registry.register(
				BuiltInRegistries.RECIPE_SERIALIZER,
				SameBucketMoreUsesRecipeSerializers.POWDER_SNOW_BUCKET_REMAINDER_ID,
				SameBucketMoreUsesRecipeSerializers.POWDER_SNOW_BUCKET_REMAINDER
		);

		SameBucketMoreUses.init();
	}
}
