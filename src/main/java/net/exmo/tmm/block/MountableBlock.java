package net.exmo.tmm.block;


import net.exmo.tmm.block.entity.SeatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


import javax.swing.text.html.BlockView;

public abstract class MountableBlock extends Block {


    public MountableBlock(Properties p_49795_) {
        super(p_49795_);
    }


    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return super.getShape(state, level, pos, context);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack p_316304_, BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand p_316595_, BlockHitResult p_316140_) {
        float radius = 1;
        if (!player.isShiftKeyDown()
                && player.getPos().subtract(pos.toCenterPos()).length() <= 1.5f
                && !(player.getMainHandStack().getItem() instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof MountableBlock)
                && level.getEntitiesByClass(SeatEntity.class, Box.of(pos.toCenterPos(), radius, radius, radius), Entity::isAlive).isEmpty()) {

            if (level.isClient) {
                return ItemInteractionResult.success(true);
            }

            SeatEntity seatEntity = TMMEntities.SEAT.create(level);

            if (seatEntity == null) {
                return ItemInteractionResult.PASS;
            }

            Vec3 sitPos = this.getSitPos(level, blockState, pos);
            Vec3 vec3 = Vec3.atCenterOf(pos).add(sitPos);
            seatEntity.refreshPositionAndAngles(Vec3.x, Vec3.y, Vec3.z, 0, 0);
            seatEntity.setSeatPos(pos);
            level.addFreshEntity(seatEntity);
            player.startRiding(seatEntity);

            return ItemInteractionResult.sidedSuccess(false);
        } else {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }    }



    public abstract Vec3 getSitPos(Level level, BlockState state, BlockPos pos);
}
