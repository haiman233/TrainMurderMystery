package dev.doctor4t.trainmurdermystery.client.render.entity;

import dev.doctor4t.ratatouille.client.lib.render.helpers.Easing;
import dev.doctor4t.trainmurdermystery.TMM;
import dev.doctor4t.trainmurdermystery.client.TMMClient;
import dev.doctor4t.trainmurdermystery.client.model.TMMModelLayers;
import dev.doctor4t.trainmurdermystery.client.model.entity.PlayerSkeletonEntityModel;
import dev.doctor4t.trainmurdermystery.entity.FirecrackerEntity;
import dev.doctor4t.trainmurdermystery.entity.PlayerBodyEntity;
import dev.doctor4t.trainmurdermystery.game.GameConstants;
import dev.doctor4t.trainmurdermystery.index.TMMItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class FirecrackerEntityRenderer extends EntityRenderer<FirecrackerEntity> {
    private final ItemRenderer itemRenderer;
    private final float scale;

    public FirecrackerEntityRenderer(EntityRendererFactory.Context ctx, float scale) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
        this.scale = scale;
    }

    public FirecrackerEntityRenderer(EntityRendererFactory.Context context) {
        this(context, 1.0F);
    }

    @Override
    public void render(FirecrackerEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (entity.age >= 2 || !(this.dispatcher.camera.getFocusedEntity().squaredDistanceTo(entity) < 12.25)) {
            matrices.push();
            matrices.scale(this.scale, this.scale, this.scale);
            matrices.translate(0, entity.hashCode()/1000f, 0); // prevent z-fighting
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.getYaw()));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
            this.itemRenderer
                    .renderItem(
                            TMMItems.FIRECRACKER.getDefaultStack(), ModelTransformationMode.GROUND, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), entity.getId()
                    );
            matrices.pop();
            super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        }
    }

    @Override
    public Identifier getTexture(FirecrackerEntity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}
