package com.same.bucket.recipe;

import com.same.bucket.SameBucketMoreUses;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class SameBucketMoreUsesRecipeSerializers {

  public static final RecipeSerializer<PowderSnowBucketRecipe> POWDER_SNOW_BUCKET_REMAINDER = Registry.register(
      BuiltInRegistries.RECIPE_SERIALIZER,
      SameBucketMoreUses.id("powder_snow_bucket_remainder"),
      new RecipeSerializer<>(PowderSnowBucketRecipe.MAP_CODEC, PowderSnowBucketRecipe.STREAM_CODEC)
  );

  public static void register() {
    // Referencing the class is enough to trigger the static initializer above.
  }
}
