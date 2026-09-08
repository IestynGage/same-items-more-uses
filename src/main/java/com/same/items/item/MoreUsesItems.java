package com.same.items.item;

import com.same.items.entity.throwable.SlimeProjectile;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class MoreUsesItems {

  public static void register() {
    UseItemCallback.EVENT.register(MoreUsesItems::throwSlimeBall);
  }

  private static InteractionResult throwSlimeBall(final Player player, final Level level, final InteractionHand hand) {
    ItemStack itemStack = player.getItemInHand(hand);
    if (!itemStack.is(Items.SLIME_BALL)) {
      return InteractionResult.PASS;
    }

    level.playSound(
        null,
        player.getX(),
        player.getY(),
        player.getZ(),
        SoundEvents.SNOWBALL_THROW,
        SoundSource.NEUTRAL,
        0.5F,
        0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
    );

    if (level instanceof ServerLevel serverLevel) {
      Projectile.spawnProjectileFromRotation(SlimeProjectile::new, serverLevel, itemStack, player, 0.0F, 1.5F, 1.0F);
    }

    player.awardStat(Stats.ITEM_USED.get(Items.SLIME_BALL));
    itemStack.consume(1, player);
    return InteractionResult.SUCCESS;
  }
}
