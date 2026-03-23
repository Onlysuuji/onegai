package org.example2.onegai.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class OnegaiClient implements ClientModInitializer {
    private static final Identifier LEFT_BADGE_LEVEL_ID = Identifier.of("onegai", "left_badge_level");
    private static final Identifier RIGHT_BADGE_LEVEL_ID = Identifier.of("onegai", "right_badge_level");
    private static final Identifier TOP_LEFT_BADGE_LEVEL_ID = Identifier.of("onegai", "top_left_badge_level");
    private static final Identifier BOOK_BADGE_TYPE_ID = Identifier.of("onegai", "book_badge_type");

    private static final String KEY_CATEGORY = "category.onegai";
    private static final String TOGGLE_KEY_TRANSLATION = "key.onegai.toggle_badges";

    private static KeyBinding toggleBadgesKey;

    @Override
    public void onInitializeClient() {
        System.out.println("ONEGAI CLIENT LOADED 2026-03-22 STONE SWORD + KEYBIND PATCH");

        toggleBadgesKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                TOGGLE_KEY_TRANSLATION,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                KEY_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleBadgesKey.wasPressed()) {
                boolean enabled = ClientFeatureToggle.toggle();
                if (client.player != null) {
                    client.player.sendMessage(
                            Text.translatable(enabled ? "message.onegai.enabled" : "message.onegai.disabled"),
                            true
                    );
                }
            }
        });

        registerBadgeProperties(Items.DIAMOND_SWORD);
        registerBadgeProperties(Items.IRON_SWORD);
        registerBadgeProperties(Items.GOLDEN_SWORD);
        registerBadgeProperties(Items.STONE_SWORD);
        registerBadgeProperties(Items.DIAMOND_PICKAXE);
        registerBadgeProperties(Items.IRON_PICKAXE);
        registerBadgeProperties(Items.GOLDEN_PICKAXE);
        registerBadgeProperties(Items.DIAMOND_SHOVEL);
        registerBadgeProperties(Items.IRON_SHOVEL);
        registerBadgeProperties(Items.GOLDEN_SHOVEL);

        registerBadgeProperties(Items.DIAMOND_HELMET);
        registerBadgeProperties(Items.DIAMOND_CHESTPLATE);
        registerBadgeProperties(Items.DIAMOND_LEGGINGS);
        registerBadgeProperties(Items.DIAMOND_BOOTS);
        registerBadgeProperties(Items.IRON_HELMET);
        registerBadgeProperties(Items.IRON_CHESTPLATE);
        registerBadgeProperties(Items.IRON_LEGGINGS);
        registerBadgeProperties(Items.IRON_BOOTS);
        registerBadgeProperties(Items.GOLDEN_HELMET);
        registerBadgeProperties(Items.GOLDEN_CHESTPLATE);
        registerBadgeProperties(Items.GOLDEN_LEGGINGS);
        registerBadgeProperties(Items.GOLDEN_BOOTS);

        registerBadgeProperties(Items.ENCHANTED_BOOK);
        registerBookTypeProperty(Items.ENCHANTED_BOOK);

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

    private static void registerBookTypeProperty(Item item) {
        ModelPredicateProviderRegistry.register(
                item,
                BOOK_BADGE_TYPE_ID,
                (stack, world, entity, seed) -> EnchantmentBadgeUtil.getBookBadgeType(stack, world)
        );
    }
}
