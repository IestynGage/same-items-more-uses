package com.same.items.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

/**
 * A trail block, visually and structurally a clone of vanilla's redstone wire (same dot/line/
 * cross/climb-the-wall connection shape logic against neighboring GunpowderTrailBlock instances),
 * but with all power/signal transmission stripped out - it never conducts or emits a redstone
 * signal. The dark tint is applied client-side via BlockColorRegistry, reusing vanilla's redstone
 * dust textures/models unmodified.
 *
 * Lighting it with flint and steel sets LIT (which BlockColorRegistry tints yellow/red instead of
 * the usual dark gray/black) and schedules a 1-tick fuse: on that tick it ignites any unlit
 * gunpowder trail blocks it's connected to (spreading the fuse outward one block per tick) and
 * then removes itself.
 */
public class GunpowderTrailBlock extends Block {
  private static final int FUSE_TICKS = 3;

  public static final MapCodec<GunpowderTrailBlock> CODEC = simpleCodec(GunpowderTrailBlock::new);
  public static final EnumProperty<RedstoneSide> NORTH = BlockStateProperties.NORTH_REDSTONE;
  public static final EnumProperty<RedstoneSide> EAST = BlockStateProperties.EAST_REDSTONE;
  public static final EnumProperty<RedstoneSide> SOUTH = BlockStateProperties.SOUTH_REDSTONE;
  public static final EnumProperty<RedstoneSide> WEST = BlockStateProperties.WEST_REDSTONE;
  public static final BooleanProperty LIT = BlockStateProperties.LIT;
  public static final Map<Direction, EnumProperty<RedstoneSide>> PROPERTY_BY_DIRECTION = ImmutableMap.copyOf(
      Maps.newEnumMap(Map.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST))
  );

  private final Function<BlockState, VoxelShape> shapes;
  private final BlockState crossState;

  public GunpowderTrailBlock(final BlockBehaviour.Properties properties) {
    super(properties);
    this.registerDefaultState(
        this.stateDefinition
            .any()
            .setValue(NORTH, RedstoneSide.NONE)
            .setValue(EAST, RedstoneSide.NONE)
            .setValue(SOUTH, RedstoneSide.NONE)
            .setValue(WEST, RedstoneSide.NONE)
            .setValue(LIT, false)
    );
    this.shapes = this.makeShapes();
    this.crossState = this.defaultBlockState()
        .setValue(NORTH, RedstoneSide.SIDE)
        .setValue(EAST, RedstoneSide.SIDE)
        .setValue(SOUTH, RedstoneSide.SIDE)
        .setValue(WEST, RedstoneSide.SIDE);
  }

  @Override
  public MapCodec<GunpowderTrailBlock> codec() {
    return CODEC;
  }

  private Function<BlockState, VoxelShape> makeShapes() {
    VoxelShape dot = Block.column(10.0, 0.0, 1.0);
    Map<Direction, VoxelShape> floor = Shapes.rotateHorizontal(Block.boxZ(10.0, 0.0, 1.0, 0.0, 8.0));
    Map<Direction, VoxelShape> up = Shapes.rotateHorizontal(Block.boxZ(10.0, 16.0, 0.0, 1.0));
    return this.getShapeForEachState(state -> {
      VoxelShape shape = dot;

      for (Entry<Direction, EnumProperty<RedstoneSide>> entry : PROPERTY_BY_DIRECTION.entrySet()) {
        shape = switch (state.getValue(entry.getValue())) {
          case UP -> Shapes.or(shape, floor.get(entry.getKey()), up.get(entry.getKey()));
          case SIDE -> Shapes.or(shape, floor.get(entry.getKey()));
          case NONE -> shape;
        };
      }

      return shape;
    }, LIT);
  }

  @Override
  protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
    return this.shapes.apply(state);
  }

  @Override
  public BlockState getStateForPlacement(final BlockPlaceContext context) {
    return this.getConnectionState(context.getLevel(), this.crossState, context.getClickedPos());
  }

  private BlockState getConnectionState(final BlockGetter level, BlockState state, final BlockPos pos) {
    boolean wasDot = isDot(state);
    state = this.getMissingConnections(level, this.defaultBlockState().setValue(LIT, state.getValue(LIT)), pos);
    if (wasDot && isDot(state)) {
      return state;
    }

    boolean north = state.getValue(NORTH).isConnected();
    boolean south = state.getValue(SOUTH).isConnected();
    boolean east = state.getValue(EAST).isConnected();
    boolean west = state.getValue(WEST).isConnected();
    boolean northSouthEmpty = !north && !south;
    boolean eastWestEmpty = !east && !west;
    if (!west && northSouthEmpty) {
      state = state.setValue(WEST, RedstoneSide.SIDE);
    }

    if (!east && northSouthEmpty) {
      state = state.setValue(EAST, RedstoneSide.SIDE);
    }

    if (!north && eastWestEmpty) {
      state = state.setValue(NORTH, RedstoneSide.SIDE);
    }

    if (!south && eastWestEmpty) {
      state = state.setValue(SOUTH, RedstoneSide.SIDE);
    }

    return state;
  }

  private BlockState getMissingConnections(final BlockGetter level, BlockState state, final BlockPos pos) {
    boolean canConnectUp = !level.getBlockState(pos.above()).isRedstoneConductor(level, pos);

    for (Direction direction : Direction.Plane.HORIZONTAL) {
      if (!state.getValue(PROPERTY_BY_DIRECTION.get(direction)).isConnected()) {
        RedstoneSide sideConnection = this.getConnectingSide(level, pos, direction, canConnectUp);
        state = state.setValue(PROPERTY_BY_DIRECTION.get(direction), sideConnection);
      }
    }

    return state;
  }

  @Override
  protected BlockState updateShape(
      final BlockState state,
      final LevelReader level,
      final ScheduledTickAccess ticks,
      final BlockPos pos,
      final Direction directionToNeighbour,
      final BlockPos neighbourPos,
      final BlockState neighbourState,
      final RandomSource random
  ) {
    if (directionToNeighbour == Direction.DOWN) {
      return !this.canSurviveOn(level, neighbourPos, neighbourState) ? Blocks.AIR.defaultBlockState() : state;
    }

    if (directionToNeighbour == Direction.UP) {
      return this.getConnectionState(level, state, pos);
    }

    RedstoneSide sideConnection = this.getConnectingSide(level, pos, directionToNeighbour);
    return sideConnection.isConnected() == state.getValue(PROPERTY_BY_DIRECTION.get(directionToNeighbour)).isConnected() && !isCross(state)
        ? state.setValue(PROPERTY_BY_DIRECTION.get(directionToNeighbour), sideConnection)
        : this.getConnectionState(
            level, this.crossState.setValue(LIT, state.getValue(LIT)).setValue(PROPERTY_BY_DIRECTION.get(directionToNeighbour), sideConnection), pos
        );
  }

  private static boolean isCross(final BlockState state) {
    return state.getValue(NORTH).isConnected() && state.getValue(SOUTH).isConnected() && state.getValue(EAST).isConnected() && state.getValue(WEST).isConnected();
  }

  private static boolean isDot(final BlockState state) {
    return !state.getValue(NORTH).isConnected()
        && !state.getValue(SOUTH).isConnected()
        && !state.getValue(EAST).isConnected()
        && !state.getValue(WEST).isConnected();
  }

  @Override
  protected void updateIndirectNeighbourShapes(
      final BlockState state, final LevelAccessor level, final BlockPos pos, final @Block.UpdateFlags int updateFlags, final int updateLimit
  ) {
    BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

    for (Direction direction : Direction.Plane.HORIZONTAL) {
      RedstoneSide value = state.getValue(PROPERTY_BY_DIRECTION.get(direction));
      if (value != RedstoneSide.NONE && !level.getBlockState(blockPos.setWithOffset(pos, direction)).is(this)) {
        blockPos.move(Direction.DOWN);
        BlockState blockStateDown = level.getBlockState(blockPos);
        if (blockStateDown.is(this)) {
          BlockPos neighborPos = blockPos.relative(direction.getOpposite());
          level.neighborShapeChanged(direction.getOpposite(), blockPos, neighborPos, level.getBlockState(neighborPos), updateFlags, updateLimit);
        }

        blockPos.setWithOffset(pos, direction).move(Direction.UP);
        BlockState blockStateUp = level.getBlockState(blockPos);
        if (blockStateUp.is(this)) {
          BlockPos neighborPos = blockPos.relative(direction.getOpposite());
          level.neighborShapeChanged(direction.getOpposite(), blockPos, neighborPos, level.getBlockState(neighborPos), updateFlags, updateLimit);
        }
      }
    }
  }

  private RedstoneSide getConnectingSide(final BlockGetter level, final BlockPos pos, final Direction direction) {
    return this.getConnectingSide(level, pos, direction, !level.getBlockState(pos.above()).isRedstoneConductor(level, pos));
  }

  private RedstoneSide getConnectingSide(final BlockGetter level, final BlockPos pos, final Direction direction, final boolean canConnectUp) {
    BlockPos relativePos = pos.relative(direction);
    BlockState relativeState = level.getBlockState(relativePos);
    if (canConnectUp) {
      boolean isPlaceableAbove = this.canSurviveOn(level, relativePos, relativeState);
      if (isPlaceableAbove && shouldConnectTo(level.getBlockState(relativePos.above()))) {
        if (relativeState.isFaceSturdy(level, relativePos, direction.getOpposite())) {
          return RedstoneSide.UP;
        }

        return RedstoneSide.SIDE;
      }
    }

    return !shouldConnectTo(relativeState)
            && (relativeState.isRedstoneConductor(level, relativePos) || !shouldConnectTo(level.getBlockState(relativePos.below())))
        ? RedstoneSide.NONE
        : RedstoneSide.SIDE;
  }

  @Override
  protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
    BlockPos below = pos.below();
    return this.canSurviveOn(level, below, level.getBlockState(below));
  }

  private boolean canSurviveOn(final BlockGetter level, final BlockPos relativePos, final BlockState relativeState) {
    return relativeState.isFaceSturdy(level, relativePos, Direction.UP);
  }

  private void checkCornerChangeAt(final Level level, final BlockPos pos) {
    if (level.getBlockState(pos).is(this)) {
      level.updateNeighborsAt(pos, this);

      for (Direction direction : Direction.values()) {
        level.updateNeighborsAt(pos.relative(direction), this);
      }
    }
  }

  @Override
  protected void onPlace(final BlockState state, final Level level, final BlockPos pos, final BlockState oldState, final boolean movedByPiston) {
    if (!oldState.is(state.getBlock()) && !level.isClientSide()) {
      for (Direction direction : Direction.Plane.VERTICAL) {
        level.updateNeighborsAt(pos.relative(direction), this);
      }

      this.updateNeighborsOfNeighboringWires(level, pos);
    }
  }

  @Override
  protected void affectNeighborsAfterRemoval(final BlockState state, final ServerLevel level, final BlockPos pos, final boolean movedByPiston) {
    if (!movedByPiston) {
      for (Direction direction : Direction.values()) {
        level.updateNeighborsAt(pos.relative(direction), this);
      }

      this.updateNeighborsOfNeighboringWires(level, pos);
    }
  }

  private void updateNeighborsOfNeighboringWires(final Level level, final BlockPos pos) {
    for (Direction direction : Direction.Plane.HORIZONTAL) {
      this.checkCornerChangeAt(level, pos.relative(direction));
    }

    for (Direction direction : Direction.Plane.HORIZONTAL) {
      BlockPos target = pos.relative(direction);
      if (level.getBlockState(target).isRedstoneConductor(level, target)) {
        this.checkCornerChangeAt(level, target.above());
      } else {
        this.checkCornerChangeAt(level, target.below());
      }
    }
  }

  @Override
  protected void neighborChanged(
      final BlockState state, final Level level, final BlockPos pos, final Block block, final @Nullable Orientation orientation, final boolean movedByPiston
  ) {
    if (!level.isClientSide() && !state.canSurvive(level, pos)) {
      dropResources(state, level, pos);
      level.removeBlock(pos, false);
    }
  }

  private static boolean shouldConnectTo(final BlockState blockState) {
    return blockState.is(MoreUsesBlocks.GUNPOWDER_TRAIL);
  }

  @Override
  protected BlockState rotate(final BlockState state, final Rotation rotation) {
    return switch (rotation) {
      case CLOCKWISE_180 -> state.setValue(NORTH, state.getValue(SOUTH))
          .setValue(EAST, state.getValue(WEST))
          .setValue(SOUTH, state.getValue(NORTH))
          .setValue(WEST, state.getValue(EAST));
      case COUNTERCLOCKWISE_90 -> state.setValue(NORTH, state.getValue(EAST))
          .setValue(EAST, state.getValue(SOUTH))
          .setValue(SOUTH, state.getValue(WEST))
          .setValue(WEST, state.getValue(NORTH));
      case CLOCKWISE_90 -> state.setValue(NORTH, state.getValue(WEST))
          .setValue(EAST, state.getValue(NORTH))
          .setValue(SOUTH, state.getValue(EAST))
          .setValue(WEST, state.getValue(SOUTH));
      default -> state;
    };
  }

  @Override
  protected BlockState mirror(final BlockState state, final Mirror mirror) {
    return switch (mirror) {
      case LEFT_RIGHT -> state.setValue(NORTH, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(NORTH));
      case FRONT_BACK -> state.setValue(EAST, state.getValue(WEST)).setValue(WEST, state.getValue(EAST));
      default -> super.mirror(state, mirror);
    };
  }

  @Override
  protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(NORTH, EAST, SOUTH, WEST, LIT);
  }

  @Override
  protected InteractionResult useItemOn(
      final ItemStack itemStack,
      final BlockState state,
      final Level level,
      final BlockPos pos,
      final Player player,
      final InteractionHand hand,
      final BlockHitResult hitResult
  ) {
    if (!itemStack.is(Items.FLINT_AND_STEEL) || state.getValue(LIT)) {
      return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
    }

    if (level instanceof ServerLevel serverLevel) {
      ignite(serverLevel, pos, state);
    }

    level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
    itemStack.hurtAndBreak(1, player, hand.asEquipmentSlot());
    player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
    return InteractionResult.SUCCESS;
  }

  private static void ignite(final ServerLevel level, final BlockPos pos, final BlockState state) {
    level.setBlock(pos, state.setValue(LIT, true), 3);
    level.scheduleTick(pos, state.getBlock(), FUSE_TICKS);
  }

  @Override
  protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
    if (!state.getValue(LIT)) {
      return;
    }

    for (Direction direction : Direction.Plane.HORIZONTAL) {
      RedstoneSide side = state.getValue(PROPERTY_BY_DIRECTION.get(direction));
      if (side == RedstoneSide.NONE) {
        continue;
      }

      BlockPos neighborPos = side == RedstoneSide.UP ? pos.relative(direction).above() : pos.relative(direction);
      BlockState neighborState = level.getBlockState(neighborPos);
      if (neighborState.is(this) && !neighborState.getValue(LIT)) {
        ignite(level, neighborPos, neighborState);
      }
    }

    for (Direction direction : Direction.values()) {
      BlockPos neighborPos = pos.relative(direction);
      if (level.getBlockState(neighborPos).is(Blocks.TNT)) {
        primeTnt(level, neighborPos);
      }
    }

    level.removeBlock(pos, false);
  }

  private static void primeTnt(final ServerLevel level, final BlockPos pos) {
    if (TntBlock.prime(level, pos)) {
      level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
    }
  }
}
