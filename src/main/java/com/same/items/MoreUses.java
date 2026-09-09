package com.same.items;

import com.same.items.entity.MoreUsesEntityTypes;
import com.same.items.item.MoreUsesItems;
import com.same.items.potion.MoreUsesPotions;
import com.same.items.recipe.MoreUsesRecipeSerializers;
import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoreUses implements ModInitializer {

  public static final String MOD_ID = "more-uses";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		MoreUsesEntityTypes.register();
		MoreUsesPotions.register();
		MoreUsesItems.register();
		MoreUsesRecipeSerializers.register();

		LOGGER.info("Initialize " + MOD_ID);
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
