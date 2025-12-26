package net.exmo.tmm.block_entity;

import dev.doctor4t.trainmurdermystery.index.TMMBlockEntities;
import dev.doctor4t.trainmurdermystery.index.TMMParticles;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.Level.Level;

public class ChimneyBlockEntity extends SyncingBlockEntity {

    public ChimneyBlockEntity(BlockPos pos, BlockState state) {
        super(TMMBlockEntities.CHIMNEY, pos, state);
    }

    public static <T extends BlockEntity> void clientTick(Level Level, BlockPos pos, BlockState state, T t) {
        Level.addParticle(TMMParticles.BLACK_SMOKE, pos.getX() + .5f, pos.getY(), pos.getZ() + .5f, 0, 0, 0);
    }
}
