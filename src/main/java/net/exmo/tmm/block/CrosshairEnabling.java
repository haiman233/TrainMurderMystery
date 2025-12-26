package net.exmo.tmm.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.Level.Level;

public interface CrosshairEnabling {

    boolean shouldShowCrosshair(Level Level, BlockState state, BlockHitResult hit);

}
