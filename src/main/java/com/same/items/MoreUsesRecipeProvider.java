package com.same.items;


import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;

public class MoreUsesRecipeProvider extends RecipeProvider {

  public MoreUsesRecipeProvider(final HolderLookup.Provider registries, final RecipeOutput output) {
    super(registries, output);
  }

  @Override
  public void buildRecipes() {
    SimpleCookingRecipeBuilder.smelting(
        Ingredient.of(Items.POWDER_SNOW_BUCKET),
        RecipeCategory.MISC,
        CookingBookCategory.MISC,
        Items.WATER_BUCKET,
        0.7f,
        50
        )
        .unlockedBy("has_powder_snow_bucket", has(Items.POWDER_SNOW_BUCKET))
        .save(this.output, "something.smelt_powder_snow_bucket");
  }
}
