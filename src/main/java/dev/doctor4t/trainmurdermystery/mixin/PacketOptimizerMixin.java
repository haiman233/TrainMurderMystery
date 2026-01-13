package dev.doctor4t.trainmurdermystery.mixin;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

public class PacketOptimizerMixin {
//    /**
//     * 优化移动包，减少不必要的精度
//     */
//
//
//
//        /**
//         * 防止坐标溢出或异常值
//         */
//        @Mixin(ServerboundMovePlayerPacket.Pos.class)
//        public static abstract class MovementPacketValidatorMixin {
//
//            @Inject(
//                    method = "read(Lnet/minecraft/network/FriendlyByteBuf;)Lnet/minecraft/network/protocol/game/ServerboundMovePlayerPacket$Pos;",
//                    at = @At("HEAD"),
//                    cancellable = true
//            )
//            private static void validateCoordinates(FriendlyByteBuf buf, CallbackInfoReturnable<ServerboundMovePlayerPacket.Pos> cir) {
//                try {
//                    double x = buf.readDouble();
//                    double y = buf.readDouble();
//                    double z = buf.readDouble();
//                    boolean onGround = buf.readUnsignedByte() != 0;
//
//                    // 验证坐标是否在合理范围内
//                    if (isValidCoordinate(x, y, z)) {
//                        // 坐标有效，正常处理
//                        cir.setReturnValue(new ServerboundMovePlayerPacket.Pos(x, y, z, onGround));
//                        return;
//                    }
//
//                    // 坐标无效，返回null或最后一个有效位置
//                    cir.setReturnValue(null);
//                    cir.cancel();
//
//                } catch (Exception e) {
//                    // 解析错误，取消这个包
//                    cir.setReturnValue(null);
//                    cir.cancel();
//                }
//            }
//            @   Inject(method = "write", at = @At("HEAD"), cancellable = true)
//            private void write(FriendlyByteBuf buf, CallbackInfo ci) {
//                ServerboundMovePlayerPacket.Pos pos = (ServerboundMovePlayerPacket.Pos) (Object) this;
//                buf.writeDouble(pos.x());
//                buf.writeDouble(pos.y());
//                buf.writeDouble(pos.z());
//                buf.writeByte(pos.onGround() ? 1 : 0);
//                ci.cancel();
//            }
//
//            @Unique
//            private static boolean isValidCoordinate(double x, double y, double z) {
//                return !Double.isNaN(x) && !Double.isNaN(y) && !Double.isNaN(z) &&
//                        !Double.isInfinite(x) && !Double.isInfinite(y) && !Double.isInfinite(z) &&
//                        Math.abs(x) < 3.0E7 && Math.abs(z) < 3.0E7 && y >= -4096 && y <= 4096;
//            }
//        }
    }
