package com.same.bucket;

import com.same.bucket.recipe.SameBucketMoreUsesRecipeSerializers;
import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SameBucketMoreUses implements ModInitializer {

  public static final String MOD_ID = "same-bucket-more-uses";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		SameBucketMoreUsesRecipeSerializers.register();

		LOGGER.info("Initialize " + MOD_ID);
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
