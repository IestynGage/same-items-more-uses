package com.same.items.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.NormalCraftingRecipe;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

/**
 * A shapeless recipe that behaves exactly like vanilla's, except it leaves an empty bucket
 * behind for any powder snow bucket consumed as an ingredient.
 */
public class PowderSnowBucketRecipe extends NormalCraftingRecipe {

  public static final MapCodec<PowderSnowBucketRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
              Recipe.CommonInfo.MAP_CODEC.forGetter(o -> o.commonInfo),
              CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(o -> o.bookInfo),
              ItemStackTemplate.CODEC.fieldOf("result").forGetter(o -> o.result),
              Ingredient.CODEC.listOf(1, 9).fieldOf("ingredients").forGetter(o -> o.ingredients)
          )
          .apply(i, PowderSnowBucketRecipe::new)
  );

  public static final StreamCodec<RegistryFriendlyByteBuf, PowderSnowBucketRecipe> STREAM_CODEC = StreamCodec.composite(
      Recipe.CommonInfo.STREAM_CODEC,
      o -> o.commonInfo,
      CraftingRecipe.CraftingBookInfo.STREAM_CODEC,
      o -> o.bookInfo,
      ItemStackTemplate.STREAM_CODEC,
      o -> o.result,
      Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
      o -> o.ingredients,
      PowderSnowBucketRecipe::new
  );

  private final ItemStackTemplate result;
  private final List<Ingredient> ingredients;

  public PowderSnowBucketRecipe(final Recipe.CommonInfo commonInfo, final CraftingRecipe.CraftingBookInfo bookInfo, final ItemStackTemplate result, final List<Ingredient> ingredients) {
    super(commonInfo, bookInfo);
    this.result = result;
    this.ingredients = ingredients;
  }

  @Override
  public RecipeSerializer<PowderSnowBucketRecipe> getSerializer() {
    return MoreUsesRecipeSerializers.POWDER_SNOW_BUCKET_REMAINDER;
  }

  @Override
  protected PlacementInfo createPlacementInfo() {
    return PlacementInfo.create(this.ingredients);
  }

  public boolean matches(final CraftingInput input, final Level level) {
    if (input.ingredientCount() != this.ingredients.size()) {
      return false;
    }
    return input.size() == 1 && this.ingredients.size() == 1
        ? this.ingredients.getFirst().test(input.getItem(0))
        : input.stackedContents().canCraft(this, null);
  }

  public ItemStack assemble(final CraftingInput input) {
    return this.result.create();
  }

  @Override
  public List<RecipeDisplay> display() {
    return List.of(
        new ShapelessCraftingRecipeDisplay(
            this.ingredients.stream().map(Ingredient::display).toList(),
            new SlotDisplay.ItemStackSlotDisplay(this.result),
            new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
        )
    );
  }

  @Override
  public NonNullList<ItemStack> getRemainingItems(final CraftingInput input) {
    NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);
    for (int slot = 0; slot < input.size(); slot++) {
      if (input.getItem(slot).is(Items.POWDER_SNOW_BUCKET)) {
        remaining.set(slot, new ItemStack(Items.BUCKET));
      }
    }
    return remaining;
  }
}
