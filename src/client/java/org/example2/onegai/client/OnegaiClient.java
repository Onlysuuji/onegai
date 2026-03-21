package org.example2.onegai.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class OnegaiClient implements ClientModInitializer {
    private static final Identifier LEFT_BADGE_LEVEL_ID = Identifier.of("onegai", "left_badge_level");
    private static final Identifier RIGHT_BADGE_LEVEL_ID = Identifier.of("onegai", "right_badge_level");
    private static final Identifier TOP_LEFT_BADGE_LEVEL_ID = Identifier.of("onegai", "top_left_badge_level");

    @Override
    public void onInitializeClient() {
        System.out.println("ONEGAI CLIENT LOADED 2026-03-22 ARMOR OVERLAY 1.21.1");

        registerBadgeProperties(Items.DIAMOND_SWORD);
        registerBadgeProperties(Items.IRON_SWORD);
        registerBadgeProperties(Items.GOLDEN_SWORD);
        registerBadgeProperties(Items.DIAMOND_PICKAXE);
        registerBadgeProperties(Items.IRON_PICKAXE);
        registerBadgeProperties(Items.GOLDEN_PICKAXE);
        registerBadgeProperties(Items.DIAMOND_SHOVEL);
        registerBadgeProperties(Items.IRON_SHOVEL);
        registerBadgeProperties(Items.GOLDEN_SHOVEL);

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityType == EntityType.PLAYER && entityRenderer instanceof PlayerEntityRenderer playerRenderer) {
                @SuppressWarnings("unchecked")
                FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> featureContext =
                        (FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>) playerRenderer;
                registrationHelper.register(new ProtectionArmorOverlayFeatureRenderer(featureContext, context));
            }
        });
    }

    private static void registerBadgeProperties(Item item) {
        ModelPredicateProviderRegistry.register(
                item,
                LEFT_BADGE_LEVEL_ID,
                (stack, world, entity, seed) -> EnchantmentBadgeUtil.getLeftBadgeLevel(stack, world)
        );

        ModelPredicateProviderRegistry.register(
                item,
                RIGHT_BADGE_LEVEL_ID,
                (stack, world, entity, seed) -> EnchantmentBadgeUtil.getRightBadgeLevel(stack, world)
        );

        ModelPredicateProviderRegistry.register(
                item,
                TOP_LEFT_BADGE_LEVEL_ID,
                (stack, world, entity, seed) -> EnchantmentBadgeUtil.getTopLeftBadgeLevel(stack, world)
        );
    }
}
