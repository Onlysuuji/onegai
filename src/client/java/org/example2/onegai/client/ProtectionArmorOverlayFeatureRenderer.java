package org.example2.onegai.client;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ProtectionArmorOverlayFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    private static final String MOD_ID = "onegai";

    private final BipedEntityModel<AbstractClientPlayerEntity> innerModel;
    private final BipedEntityModel<AbstractClientPlayerEntity> outerModel;

    public ProtectionArmorOverlayFeatureRenderer(
            FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context,
            EntityRendererFactory.Context rendererContext
    ) {
        super(context);
        this.innerModel = new BipedEntityModel<>(rendererContext.getPart(EntityModelLayers.PLAYER_INNER_ARMOR));
        this.outerModel = new BipedEntityModel<>(rendererContext.getPart(EntityModelLayers.PLAYER_OUTER_ARMOR));
    }

    @Override
    public void render(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            AbstractClientPlayerEntity player,
            float limbAngle,
            float limbDistance,
            float tickDelta,
            float animationProgress,
            float headYaw,
            float headPitch
    ) {
        renderPiece(matrices, vertexConsumers, light, player, EquipmentSlot.HEAD, player.getEquippedStack(EquipmentSlot.HEAD));
        renderPiece(matrices, vertexConsumers, light, player, EquipmentSlot.CHEST, player.getEquippedStack(EquipmentSlot.CHEST));
        renderPiece(matrices, vertexConsumers, light, player, EquipmentSlot.LEGS, player.getEquippedStack(EquipmentSlot.LEGS));
        renderPiece(matrices, vertexConsumers, light, player, EquipmentSlot.FEET, player.getEquippedStack(EquipmentSlot.FEET));
    }

    private void renderPiece(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            AbstractClientPlayerEntity player,
            EquipmentSlot slot,
            ItemStack stack
    ) {
        if (stack.isEmpty()) {
            return;
        }

        int protectionLevel = EnchantmentBadgeUtil.getProtectionArmorLevel(stack, player.clientWorld);
        if (protectionLevel <= 0) {
            return;
        }

        BipedEntityModel<AbstractClientPlayerEntity> armorModel = usesInnerModel(slot) ? this.innerModel : this.outerModel;
        this.getContextModel().copyBipedStateTo(armorModel);
        armorModel.setAngles(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        armorModel.setVisible(false);
        setPartVisibility(armorModel, slot);

        Identifier texture = Identifier.of(
                MOD_ID,
                "textures/models/armor/generic_protection_" + protectionLevel + "_layer_" + (usesInnerModel(slot) ? 2 : 1) + ".png"
        );
        VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(
                vertexConsumers,
                RenderLayer.getArmorCutoutNoCull(texture),
                stack.hasGlint()
        );
        armorModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
    }

    private static boolean usesInnerModel(EquipmentSlot slot) {
        return slot == EquipmentSlot.LEGS;
    }

    private static void setPartVisibility(BipedEntityModel<AbstractClientPlayerEntity> model, EquipmentSlot slot) {
        switch (slot) {
            case HEAD -> {
                model.head.visible = true;
                model.hat.visible = true;
            }
            case CHEST -> {
                model.body.visible = true;
                model.rightArm.visible = true;
                model.leftArm.visible = true;
            }
            case LEGS -> {
                model.body.visible = true;
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
            }
            case FEET -> {
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
            }
            default -> {
            }
        }
    }
}
