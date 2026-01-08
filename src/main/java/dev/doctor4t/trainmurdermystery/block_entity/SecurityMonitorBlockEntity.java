package dev.doctor4t.trainmurdermystery.block_entity;

import dev.doctor4t.trainmurdermystery.TMM;
import dev.doctor4t.trainmurdermystery.index.TMMBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class SecurityMonitorBlockEntity extends BlockEntity {

    private final List<BlockPos> cameraPositions = new ArrayList<>();

    public SecurityMonitorBlockEntity(BlockPos pos, BlockState state) {
        super(TMMBlockEntities.SECURITY_MONITOR, pos, state);
    }

    public void addCameraPosition(BlockPos pos) {
        if (!cameraPositions.contains(pos)) {
            cameraPositions.add(pos);
            setChanged();
        }
    }

    public List<BlockPos> getCameraPositions() {
        return new ArrayList<>(cameraPositions);
    }

    public boolean removeCameraPosition(BlockPos pos) {
        boolean removed = cameraPositions.remove(pos);
        if (removed) {
            setChanged();
        }
        return removed;
    }

    public void clearCameraPositions() {
        if (!cameraPositions.isEmpty()) {
            cameraPositions.clear();
            setChanged();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);
        // 保存摄像头位置列表
        CompoundTag positionsTag = new CompoundTag();
        positionsTag.putInt("Size", cameraPositions.size());
        for (int i = 0; i < cameraPositions.size(); i++) {
            BlockPos pos = cameraPositions.get(i);
            positionsTag.putIntArray("Pos_" + i, new int[]{pos.getX(), pos.getY(), pos.getZ()});
        }
        compoundTag.put("CameraPositions", positionsTag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("CameraPositions")) {
            CompoundTag positionsTag = tag.getCompound("CameraPositions");
            int size = positionsTag.getInt("Size");
            cameraPositions.clear();
            for (int i = 0; i < size; i++) {
                if (positionsTag.contains("Pos_" + i)) {
                    int[] posArray = positionsTag.getIntArray("Pos_" + i);
                    if (posArray.length == 3) {
                        cameraPositions.add(new BlockPos(posArray[0], posArray[1], posArray[2]));
                    }
                }
            }
        }
    }


}