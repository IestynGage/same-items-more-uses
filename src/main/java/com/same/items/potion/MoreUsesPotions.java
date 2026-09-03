package com.same.items.potion;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder;
import net.minecraft.world.item.Items;

public class MoreUsesPotions implements ModInitializer {

  @Override
  public void onInitialize() {
    FabricPotionBrewingBuilder.BUILD.register(builder -> {
      builder.addMix(
          net.minecraft.world.item.alchemy.Potions.AWKWARD,
          Items.POISONOUS_POTATO,
          net.minecraft.world.item.alchemy.Potions.POISON
      );
    });
  }
}
