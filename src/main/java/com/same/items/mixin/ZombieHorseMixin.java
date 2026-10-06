package com.same.items.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.equine.ZombieHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Lets a zombie horse be cured the same way as a zombie villager: give it weakness, then feed it a golden apple.
 */
@Mixin(ZombieHorse.class)
public abstract class ZombieHorseMixin extends AbstractHorse {
  @Unique
  private static final int NOT_CONVERTING = -1;
  @Unique
  private static final String CONVERSION_TIME_KEY = "MoreUsesConversionTime";

  @Unique
  private int horseConversionTime = NOT_CONVERTING;

  protected ZombieHorseMixin(final EntityType<? extends AbstractHorse> type, final Level level) {
    super(type, level);
  }

  @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
  private void moreUses$cureWithGoldenApple(final Player player, final InteractionHand hand, final CallbackInfoReturnable<InteractionResult> cir) {
    ItemStack itemStack = player.getItemInHand(hand);
    if (!itemStack.is(Items.GOLDEN_APPLE)) {
      return;
    }

    if (this.hasEffect(MobEffects.WEAKNESS)) {
      itemStack.consume(1, player);
      if (!this.level().isClientSide()) {
        this.moreUses$startConverting(this.random.nextInt(2401) + 3600);
      }

      cir.setReturnValue(InteractionResult.SUCCESS_SERVER);
    } else {
      // Stop an untamed zombie horse from rearing up at the player for offering it a golden apple.
      cir.setReturnValue(InteractionResult.CONSUME);
    }
  }

  @Inject(method = "removeWhenFarAway", at = @At("HEAD"), cancellable = true)
  private void moreUses$keepWhileConverting(final double distSqr, final CallbackInfoReturnable<Boolean> cir) {
    if (this.moreUses$isConverting()) {
      cir.setReturnValue(false);
    }
  }

  @Override
  public void tick() {
    if (this.level() instanceof ServerLevel serverLevel && this.isAlive() && this.moreUses$isConverting()) {
      this.horseConversionTime--;
      if (this.horseConversionTime <= 0) {
        this.moreUses$finishConversion(serverLevel);
      }
    }

    super.tick();
  }

  @Override
  protected void addAdditionalSaveData(final ValueOutput output) {
    super.addAdditionalSaveData(output);
    output.putInt(CONVERSION_TIME_KEY, this.horseConversionTime);
  }

  @Override
  protected void readAdditionalSaveData(final ValueInput input) {
    super.readAdditionalSaveData(input);
    this.horseConversionTime = input.getIntOr(CONVERSION_TIME_KEY, NOT_CONVERTING);
  }

  @Unique
  private boolean moreUses$isConverting() {
    return this.horseConversionTime != NOT_CONVERTING;
  }

  @Unique
  private void moreUses$startConverting(final int time) {
    this.horseConversionTime = time;
    this.setPersistenceRequired();
    this.removeEffect(MobEffects.WEAKNESS);
    this.addEffect(new MobEffectInstance(MobEffects.STRENGTH, time, 0));
    if (!this.isSilent()) {
      this.level().playSound(
        null,
        this.getX(),
        this.getEyeY(),
        this.getZ(),
        SoundEvents.ZOMBIE_VILLAGER_CURE,
        this.getSoundSource(),
        1.0F + this.random.nextFloat(),
        this.random.nextFloat() * 0.7F + 0.3F
      );
    }
  }

  @Unique
  private void moreUses$finishConversion(final ServerLevel level) {
    this.convertTo(EntityTypes.HORSE, ConversionParams.single(this, true, false), horse -> {
      horse.finalizeSpawn(level, level.getCurrentDifficultyAt(horse.blockPosition()), EntitySpawnReason.CONVERSION, null);
      horse.setHealth(horse.getMaxHealth());
      horse.setTamed(this.isTamed());
      horse.setTemper(this.getTemper());
      ((AbstractHorseAccessor) horse).moreUses$setOwnerReference(this.getOwnerReference());
      horse.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 200, 0));
      if (!this.isSilent()) {
        level.levelEvent(null, 1027, this.blockPosition(), 0);
      }
    });
  }
}
