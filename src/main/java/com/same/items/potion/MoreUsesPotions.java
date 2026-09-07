package com.same.items.potion;

import net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;

public class MoreUsesPotions {

  public static void register() {
    FabricPotionBrewingBuilder.BUILD.register(builder -> {
      builder.addMix(
          Potions.AWKWARD,
          Items.POISONOUS_POTATO,
          Potions.POISON
      );
    });
  }
}
