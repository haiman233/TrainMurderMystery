package net.exmo.tmm.block;

import dev.doctor4t.trainmurdermystery.block_entity.TrimmedBedBlockEntity;
import dev.doctor4t.trainmurdermystery.index.TMMBlockEntities;
import dev.doctor4t.trainmurdermystery.index.TMMItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.Dismounting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3;
import net.minecraft.util.shape.VoxelShape;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class TrimmedBedBlock extends BedBlock {
    public static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 8, 16);

    public TrimmedBedBlock(Settings settings) {
        super(DyeColor.WHITE, settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(PART, BedPart.FOOT).with(OCCUPIED, false));
    }

    @Nullable
    public static Direction getDirection(BlockView Level, BlockPos pos) {
        BlockState blockState = Level.getBlockState(pos);
        return blockState.getBlock() instanceof TrimmedBedBlock ? blockState.get(FACING) : null;
    }

    private static Direction getDirectionTowardsOtherPart(BedPart part, Direction direction) {
        return part == BedPart.FOOT ? direction : direction.getOpposite();
    }

    private static boolean isBedBelow(BlockView Level, BlockPos pos) {
        return Level.getBlockState(pos.down()).getBlock() instanceof TrimmedBedBlock;
    }

    public static Optional<Vec3> findWakeUpPosition(EntityType<?> type, CollisionView Level, BlockPos pos, Direction bedDirection, float spawnAngle) {
        Direction direction = bedDirection.rotateYClockwise();
        Direction direction2 = direction.pointsTo(spawnAngle) ? direction.getOpposite() : direction;
        if (TrimmedBedBlock.isBedBelow(Level, pos)) {
            return TrimmedBedBlock.findWakeUpPosition(type, Level, pos, bedDirection, direction2);
        }
        int[][] is = TrimmedBedBlock.getAroundAndOnBedOffsets(bedDirection, direction2);
        Optional<Vec3> optional = TrimmedBedBlock.findWakeUpPosition(type, Level, pos, is, true);
        if (optional.isPresent()) {
            return optional;
        }
        return TrimmedBedBlock.findWakeUpPosition(type, Level, pos, is, false);
    }

    private static Optional<Vec3> findWakeUpPosition(EntityType<?> type, CollisionView Level, BlockPos pos, Direction bedDirection, Direction respawnDirection) {
        int[][] is = TrimmedBedBlock.getAroundBedOffsets(bedDirection, respawnDirection);
        Optional<Vec3> optional = TrimmedBedBlock.findWakeUpPosition(type, Level, pos, is, true);
        if (optional.isPresent()) {
            return optional;
        }
        BlockPos blockPos = pos.down();
        Optional<Vec3> optional2 = TrimmedBedBlock.findWakeUpPosition(type, Level, blockPos, is, true);
        if (optional2.isPresent()) {
            return optional2;
        }
        int[][] js = TrimmedBedBlock.getOnBedOffsets(bedDirection);
        Optional<Vec3> optional3 = TrimmedBedBlock.findWakeUpPosition(type, Level, pos, js, true);
        if (optional3.isPresent()) {
            return optional3;
        }
        Optional<Vec3> optional4 = TrimmedBedBlock.findWakeUpPosition(type, Level, pos, is, false);
        if (optional4.isPresent()) {
            return optional4;
        }
        Optional<Vec3> optional5 = TrimmedBedBlock.findWakeUpPosition(type, Level, blockPos, is, false);
        if (optional5.isPresent()) {
            return optional5;
        }
        return TrimmedBedBlock.findWakeUpPosition(type, Level, pos, js, false);
    }

    private static Optional<Vec3> findWakeUpPosition(EntityType<?> type, CollisionView Level, BlockPos pos, int[][] possibleOffsets, boolean ignoreInvalidPos) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int[] is : possibleOffsets) {
            mutable.set(pos.getX() + is[0], pos.getY(), pos.getZ() + is[1]);
            Vec3 Vec3 = Dismounting.findRespawnPos(type, Level, mutable, ignoreInvalidPos);
            if (Vec3 == null) continue;
            return Optional.of(Vec3);
        }
        return Optional.empty();
    }

    private static int[][] getAroundAndOnBedOffsets(Direction bedDirection, Direction respawnDirection) {
        return ArrayUtils.addAll(TrimmedBedBlock.getAroundBedOffsets(bedDirection, respawnDirection), TrimmedBedBlock.getOnBedOffsets(bedDirection));
    }

    private static int[][] getAroundBedOffsets(Direction bedDirection, Direction respawnDirection) {
        return new int[][]{{respawnDirection.getOffsetX(), respawnDirection.getOffsetZ()}, {respawnDirection.getOffsetX() - bedDirection.getOffsetX(), respawnDirection.getOffsetZ() - bedDirection.getOffsetZ()}, {respawnDirection.getOffsetX() - bedDirection.getOffsetX() * 2, respawnDirection.getOffsetZ() - bedDirection.getOffsetZ() * 2}, {-bedDirection.getOffsetX() * 2, -bedDirection.getOffsetZ() * 2}, {-respawnDirection.getOffsetX() - bedDirection.getOffsetX() * 2, -respawnDirection.getOffsetZ() - bedDirection.getOffsetZ() * 2}, {-respawnDirection.getOffsetX() - bedDirection.getOffsetX(), -respawnDirection.getOffsetZ() - bedDirection.getOffsetZ()}, {-respawnDirection.getOffsetX(), -respawnDirection.getOffsetZ()}, {-respawnDirection.getOffsetX() + bedDirection.getOffsetX(), -respawnDirection.getOffsetZ() + bedDirection.getOffsetZ()}, {bedDirection.getOffsetX(), bedDirection.getOffsetZ()}, {respawnDirection.getOffsetX() + bedDirection.getOffsetX(), respawnDirection.getOffsetZ() + bedDirection.getOffsetZ()}};
    }

    private static int[][] getOnBedOffsets(Direction bedDirection) {
        return new int[][]{{0, 0}, {-bedDirection.getOffsetX(), -bedDirection.getOffsetZ()}};
    }

    @Override
    protected ActionResult onUse(BlockState state, Level Level, BlockPos pos, Player player, BlockHitResult hit) {
        if (Level.isClient) {
            return ActionResult.CONSUME;
        } else {
            if (!player.isCreative() && player.getStackInHand(Hand.MAIN_HAND).isOf(TMMItems.SCORPION)) {
                TrimmedBedBlockEntity blockEntity = null;

                if (Level.getBlockEntity(pos) instanceof TrimmedBedBlockEntity firstBlockEntity) {
                    if (Level.getBlockState(pos).get(PART) == BedPart.HEAD)
                        blockEntity = firstBlockEntity;
                    else {
                        BlockPos headPos = pos.offset(Level.getBlockState(pos).get(FACING));
                        if (Level.getBlockEntity(headPos) instanceof TrimmedBedBlockEntity foundBlockEntity)
                            blockEntity = foundBlockEntity;
                    }
                }

                if (blockEntity != null) {
                    if (!blockEntity.hasScorpion()) {
                        blockEntity.setHasScorpion(true, player.getUuid());
                        player.getStackInHand(Hand.MAIN_HAND).decrement(1);

                        return ActionResult.SUCCESS;
                    }
                }
            }

            if (state.get(PART) != BedPart.HEAD) {
                pos = pos.offset(state.get(FACING));
                state = Level.getBlockState(pos);
                if (!state.isOf(this)) {
                    return ActionResult.CONSUME;
                }
            }

            if (state.get(OCCUPIED)) {
                if (!this.wakePlayers(Level, pos)) {
                    player.sendMessage(Text.translatable("block.minecraft.bed.occupied"), true);
                }

                return ActionResult.SUCCESS;
            } else {
                player.trySleep(pos).ifLeft(reason -> {
                    if (reason.getMessage() != null) {
                        player.sendMessage(reason.getMessage(), true);
                    }
                });
                return ActionResult.SUCCESS;
            }
        }
    }

    private boolean wakePlayers(Level Level, BlockPos pos) {
        List<Player> list = Level.getEntitiesByClass(Player.class, new Box(pos), LivingEntity::isSleeping);
        if (list.isEmpty()) {
            return false;
        } else {
            (list.get(0)).wakeUp();
            return true;
        }
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level Level, BlockState state, BlockEntityType<T> type) {
        if (!Level.isClient || !type.equals(TMMBlockEntities.TRIMMED_BED)) {
            return null;
        }
        return TrimmedBedBlockEntity::clientTick;
    }

    @Override
    public void onLandedUpon(Level Level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        super.onLandedUpon(Level, state, pos, entity, fallDistance * 0.5f);
    }

    @Override
    public void onEntityLand(BlockView Level, Entity entity) {
        if (entity.bypassesLandingEffects()) {
            super.onEntityLand(Level, entity);
        } else {
            this.bounceEntity(entity);
        }
    }

    private void bounceEntity(Entity entity) {
        Vec3 Vec3 = entity.getVelocity();
        if (Vec3.y < 0.0) {
            double d = entity instanceof LivingEntity ? 1.0 : 0.8;
            entity.setVelocity(Vec3.x, -Vec3.y * (double) 0.66f * d, Vec3.z);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess Level, BlockPos pos, BlockPos neighborPos) {
        if (direction == TrimmedBedBlock.getDirectionTowardsOtherPart(state.get(PART), state.get(FACING))) {
            if (neighborState.isOf(this) && neighborState.get(PART) != state.get(PART)) {
                return state.with(OCCUPIED, neighborState.get(OCCUPIED));
            }
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, Level, pos, neighborPos);
    }

    @Override
    public BlockState onBreak(Level Level, BlockPos pos, BlockState state, Player player) {
        if (!Level.isClient && player.isCreative()) {
            BedPart bedPart = state.get(PART);
            if (bedPart == BedPart.FOOT) {
                BlockPos blockPos = pos.offset(getDirectionTowardsOtherPart(bedPart, state.get(FACING)));
                BlockState blockState = Level.getBlockState(blockPos);
                if (blockState.isOf(this) && blockState.get(PART) == BedPart.HEAD) {
                    Level.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.SKIP_DROPS);
                    Level.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, blockPos, Block.getRawIdFromState(blockState));
                }
            }
        }

        return super.onBreak(Level, pos, state, player);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getHorizontalPlayerFacing();
        BlockPos blockPos = ctx.getBlockPos();
        BlockPos blockPos2 = blockPos.offset(direction);
        Level Level = ctx.getWorld();
        if (Level.getBlockState(blockPos2).canReplace(ctx) && Level.getWorldBorder().contains(blockPos2)) {
            return this.getDefaultState().with(FACING, direction);
        }
        return null;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView Level, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, OCCUPIED);
    }

    @Override
    public void onPlaced(Level Level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(Level, pos, state, placer, itemStack);
        if (!Level.isClient) {
            BlockPos blockPos = pos.offset(state.get(FACING));
            Level.setBlockState(blockPos, state.with(PART, BedPart.HEAD), Block.NOTIFY_ALL);
            Level.updateNeighbors(pos, Blocks.AIR);
            state.updateNeighbors(Level, pos, Block.NOTIFY_ALL);
        }
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TrimmedBedBlockEntity(TMMBlockEntities.TRIMMED_BED, pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
