package com.same.bucket;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loader-independent entry point, called from each platform's own initializer.
 */
public class SameBucketMoreUses {

	public static final String MOD_ID = "same_bucket_more_uses";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static void init() {
		LOGGER.info("Initialize " + MOD_ID);
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
