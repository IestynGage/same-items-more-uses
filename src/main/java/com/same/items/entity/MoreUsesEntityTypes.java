package com.same.items.entity;

import com.same.items.MoreUses;
import com.same.items.entity.throwable.SlimeProjectile;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class MoreUsesEntityTypes {

  public static final EntityType<SlimeProjectile> SLIME_PROJECTILE = register(
      "slime_projectile",
      EntityType.Builder.<SlimeProjectile>of(SlimeProjectile::new, MobCategory.MISC)
          .noLootTable()
          .sized(0.25F, 0.25F)
          .clientTrackingRange(4)
          .updateInterval(10)
  );

  private static <T extends net.minecraft.world.entity.Entity> EntityType<T> register(
      final String name, final EntityType.Builder<T> builder
  ) {
    ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, MoreUses.id(name));
    return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
  }

  public static void register() {
    // Referencing the class is enough to trigger the static initializers above.
  }
}
