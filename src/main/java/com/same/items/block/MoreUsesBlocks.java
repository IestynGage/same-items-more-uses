package com.same.items.block;

import com.same.items.MoreUses;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

public class MoreUsesBlocks {

  public static final Block GUNPOWDER_TRAIL = Blocks.register(
      ResourceKey.create(Registries.BLOCK, MoreUses.id("gunpowder_trail")),
      GunpowderTrailBlock::new,
      BlockBehaviour.Properties.of().noCollision().instabreak().pushReaction(PushReaction.DESTROY)
  );

  // Not obtainable directly (no recipe, not in a creative tab) - exists so GUNPOWDER_TRAIL has a
  // valid BlockItem to place it through when a player uses vanilla gunpowder on a block.
  public static final BlockItem GUNPOWDER_TRAIL_ITEM = registerItem(
      "gunpowder_trail", properties -> new BlockItem(GUNPOWDER_TRAIL, properties)
  );

  private static <T extends Item> T registerItem(final String name, final Function<Item.Properties, T> factory) {
    ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, MoreUses.id(name));
    return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(new Item.Properties().setId(key)));
  }

  public static void register() {
    // Referencing the class is enough to trigger the static initializer above.
  }
}
