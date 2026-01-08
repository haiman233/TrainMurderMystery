package dev.doctor4t.trainmurdermystery.block_entity;

import dev.doctor4t.trainmurdermystery.TMM;
import dev.doctor4t.trainmurdermystery.index.TMMBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CameraBlockEntity extends BlockEntity {

    public CameraBlockEntity(BlockPos pos, BlockState state) {
        super(TMMBlockEntities.CAMERA, pos, state);
    }
}