package com.same.bucket.recipe;

import com.same.bucket.SameBucketMoreUses;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * Recipe serializers added by this mod. Each loader registers these in its own way, under the ids
 * given here.
 */
public class SameBucketMoreUsesRecipeSerializers {

  public static final Identifier POWDER_SNOW_BUCKET_REMAINDER_ID = SameBucketMoreUses.id("powder_snow_bucket_remainder");

  public static final RecipeSerializer<PowderSnowBucketRecipe> POWDER_SNOW_BUCKET_REMAINDER =
      new RecipeSerializer<>(PowderSnowBucketRecipe.MAP_CODEC, PowderSnowBucketRecipe.STREAM_CODEC);
}
