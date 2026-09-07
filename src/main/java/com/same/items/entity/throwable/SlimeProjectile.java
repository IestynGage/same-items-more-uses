package com.same.items.entity.throwable;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class SlimeProjectile extends ThrowableItemProjectile {
  public SlimeProjectile(final EntityType<? extends Snowball> type, final Level level) {
    super(type, level);
  }

  public SlimeProjectile(final Level level, final LivingEntity mob, final ItemStack itemStack) {
    super(null, mob, level, itemStack);
  }

  public SlimeProjectile(final Level level, final double x, final double y, final double z, final ItemStack itemStack) {
    super(null, x, y, z, level, itemStack);
  }

  @Override
  protected Item getDefaultItem() {
    return Items.SLIME_BALL;
  }

  private ParticleOptions getParticle() {
    ItemStack item = this.getItem();
    return item.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemParticleOption(ParticleTypes.ITEM, ItemStackTemplate.fromNonEmptyStack(item));
  }

  @Override
  public void handleEntityEvent(final byte id) {
    if (id == 3) {
      ParticleOptions particle = this.getParticle();

      for (int i = 0; i < 8; i++) {
        this.level().addParticle(particle, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
      }
    }
  }

  @Override
  protected void onHitEntity(final EntityHitResult hitResult) {
    super.onHitEntity(hitResult);
    Entity entity = hitResult.getEntity();
    // Increase slime heart?
    // Sulphie hearts also increase?
//    int damage = entity instanceof Slime ? 3 : 0;
//    entity.hurt(this.damageSources().thrown(this, this.getOwner()), damage);
  }

  @Override
  protected void onHit(final HitResult hitResult) {
    super.onHit(hitResult);
    if (!this.level().isClientSide()) {
      this.level().broadcastEntityEvent(this, (byte)3);
      this.discard();
    }
  }
}