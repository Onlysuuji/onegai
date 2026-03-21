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
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

/**
 * Expanded Fabric 1.21.1 armor badge overlay.
 *
 * This version keeps the "natural follow" approach from the minimal patch:
 * - copy the already-posed player model state
 * - never pose the overlay independently
 * - render only the slot parts that should be visible
 *
 * Expansion from the minimal patch:
 * - HEAD / CHEST / LEGS / FEET
 * - Protection I..IV
 * - slim + wide player armor models
 */
public class ProtectionArmorOverlayFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    private static final String MOD_ID = "onegai";

    private final BipedEntityModel<AbstractClientPlayerEntity> wideInnerModel;
    private final BipedEntityModel<AbstractClientPlayerEntity> wideOuterModel;
    private final BipedEntityModel<AbstractClientPlayerEntity> slimInnerModel;
    private final BipedEntityModel<AbstractClientPlayerEntity> slimOuterModel;

    public ProtectionArmorOverlayFeatureRenderer(
            FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context,
            EntityRendererFactory.Context rendererContext
    ) {
        super(context);
        this.wideInnerModel = new BipedEntityModel<>(rendererContext.getPart(EntityModelLayers.PLAYER_INNER_ARMOR));
        this.wideOuterModel = new BipedEntityModel<>(rendererContext.getPart(EntityModelLayers.PLAYER_OUTER_ARMOR));
        this.slimInnerModel = new BipedEntityModel<>(rendererContext.getPart(EntityModelLayers.PLAYER_SLIM_INNER_ARMOR));
        this.slimOuterModel = new BipedEntityModel<>(rendererContext.getPart(EntityModelLayers.PLAYER_SLIM_OUTER_ARMOR));
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

        Identifier texture = getProtectionTexture(slot, protectionLevel);
        if (texture == null) {
            return;
        }

        BipedEntityModel<AbstractClientPlayerEntity> armorModel = getArmorModel(player, slot);

        // Important:
        // copy the already animated player pose so the overlay stays attached
        // to the armor motion instead of looking like a separately posed layer.
        this.getContextModel().copyBipedStateTo(armorModel);
        armorModel.setVisible(false);
        setPartVisibility(armorModel, slot);

        VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(
                vertexConsumers,
                RenderLayer.getArmorCutoutNoCull(texture),
                stack.hasGlint()
        );
        armorModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
    }

    private BipedEntityModel<AbstractClientPlayerEntity> getArmorModel(AbstractClientPlayerEntity player, EquipmentSlot slot) {
        boolean slim = player.getSkinTextures().model() == SkinTextures.Model.SLIM;
        boolean inner = usesInnerModel(slot);

        if (slim) {
            return inner ? this.slimInnerModel : this.slimOuterModel;
        }
        return inner ? this.wideInnerModel : this.wideOuterModel;
    }

    private static Identifier getProtectionTexture(EquipmentSlot slot, int protectionLevel) {
        int layer = usesInnerModel(slot) ? 2 : 1;
        int clampedLevel = Math.max(1, Math.min(4, protectionLevel));
        return Identifier.of(
                MOD_ID,
                "textures/models/armor/generic_protection_" + clampedLevel + "_layer_" + layer + ".png"
        );
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
