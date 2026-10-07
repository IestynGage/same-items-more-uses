package com.same.bucket.datagen;

import com.same.bucket.SameBucketMoreUses;
import com.same.bucket.recipe.PowderSnowBucketRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SameBucketMoreUsesRecipeProvider extends FabricRecipeProvider {

  public SameBucketMoreUsesRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
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
            .save(this.output, "smelt_powder_snow_bucket");

        shapeless(RecipeCategory.MISC, Items.POWDER_SNOW_BUCKET)
            .requires(Items.SNOW_BLOCK)
            .requires(Items.BUCKET)
            .unlockedBy("has_bucket", has(Items.BUCKET))
            .save(this.output, "craft_powder_snow_bucket_from_snow_block");

        shapeless(RecipeCategory.MISC, Items.POWDER_SNOW_BUCKET)
            .requires(Items.SNOWBALL, 8)
            .requires(Items.BUCKET)
            .unlockedBy("has_bucket", has(Items.BUCKET))
            .save(this.output, "craft_powder_snow_bucket_from_snowballs");

        ResourceKey<Recipe<?>> craftSnowballsId = ResourceKey.create(Registries.RECIPE, SameBucketMoreUses.id("craft_snowballs_from_powder_snow_bucket"));
        PowderSnowBucketRecipe craftSnowballsRecipe = new PowderSnowBucketRecipe(
            RecipeBuilder.createCraftingCommonInfo(true),
            RecipeBuilder.createCraftingBookInfo(RecipeCategory.MISC, null),
            new ItemStackTemplate(Items.SNOWBALL, 8),
            List.of(Ingredient.of(Items.POWDER_SNOW_BUCKET))
        );
        RecipeUnlockAdvancementBuilder craftSnowballsAdvancement = new RecipeUnlockAdvancementBuilder();
        craftSnowballsAdvancement.unlockedBy("has_bucket", has(Items.POWDER_SNOW_BUCKET));
        this.output.accept(
            craftSnowballsId,
            craftSnowballsRecipe,
            craftSnowballsAdvancement.build(this.output, craftSnowballsId, RecipeCategory.MISC)
        );
      }
    };
  }

  @Override
  public String getName() {
    return "SameBucketMoreUsesRecipeProvider";
  }
}
