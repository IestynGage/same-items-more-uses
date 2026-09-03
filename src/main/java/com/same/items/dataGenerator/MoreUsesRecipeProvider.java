package com.same.items.dataGenerator;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import java.util.concurrent.CompletableFuture;

public class MoreUsesRecipeProvider extends FabricRecipeProvider {

  public MoreUsesRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
    super(output, registriesFuture);
  }

  @Override
  protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
    return new RecipeProvider(registryLookup, exporter) {
      @Override
      public void buildRecipes() {
//        TODO: Check other cooking things work as well
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

        FabricPotionBrewingBuilder.BUILD.register(builder -> {
          builder.addMix(
              Potions.AWKWARD,
              Items.POISONOUS_POTATO,
              Potions.POISON
          );
        });
      }
    };
  }

  @Override
  public String getName() {
    return "MoreUsesRecipeProvider";
  }
}
