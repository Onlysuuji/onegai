package org.example2.onegai.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public final class EnchantmentBadgeUtil {
    private static final float[] LEVELS_1_TO_5 = {0.0F, 0.2F, 0.4F, 0.6F, 0.8F, 1.0F};

    private EnchantmentBadgeUtil() {
    }

    public static float getLeftBadgeLevel(ItemStack stack, ClientWorld world) {
        if (isSupportedSword(stack)) {
            return normalizeMappedLevel(getEnchantmentLevel(stack, world, Enchantments.SHARPNESS), LEVELS_1_TO_5);
        }
        if (isSupportedTool(stack)) {
            return normalizeMappedLevel(getEnchantmentLevel(stack, world, Enchantments.EFFICIENCY), LEVELS_1_TO_5);
        }
        return 0.0F;
    }

    public static float getRightBadgeLevel(ItemStack stack, ClientWorld world) {
        if (isSupportedSword(stack)) {
            return normalizeMappedLevel(getEnchantmentLevel(stack, world, Enchantments.FIRE_ASPECT), LEVELS_1_TO_5);
        }
        if (isSupportedTool(stack)) {
            return normalizeMappedLevel(getEnchantmentLevel(stack, world, Enchantments.FORTUNE), LEVELS_1_TO_5);
        }
        return 0.0F;
    }

    public static float getTopLeftBadgeLevel(ItemStack stack, ClientWorld world) {
        if (isSupportedSword(stack)) {
            return normalizeMappedLevel(getEnchantmentLevel(stack, world, Enchantments.KNOCKBACK), LEVELS_1_TO_5);
        }
        return 0.0F;
    }

    public static int getProtectionArmorLevel(ItemStack stack, ClientWorld world) {
        if (!(stack.getItem() instanceof ArmorItem)) {
            return 0;
        }
        return clampLevel(getEnchantmentLevel(stack, world, Enchantments.PROTECTION), 4);
    }

    private static boolean isSupportedSword(ItemStack stack) {
        return stack.isOf(Items.DIAMOND_SWORD)
                || stack.isOf(Items.IRON_SWORD)
                || stack.isOf(Items.GOLDEN_SWORD);
    }

    private static boolean isSupportedTool(ItemStack stack) {
        return stack.isOf(Items.DIAMOND_PICKAXE)
                || stack.isOf(Items.IRON_PICKAXE)
                || stack.isOf(Items.GOLDEN_PICKAXE)
                || stack.isOf(Items.DIAMOND_SHOVEL)
                || stack.isOf(Items.IRON_SHOVEL)
                || stack.isOf(Items.GOLDEN_SHOVEL);
    }

    private static int getEnchantmentLevel(ItemStack stack, ClientWorld world, RegistryKey<Enchantment> enchantmentKey) {
        ClientWorld actualWorld = resolveWorld(world);
        if (actualWorld == null) {
            return 0;
        }

        return EnchantmentHelper.getLevel(
                actualWorld.getRegistryManager()
                        .get(RegistryKeys.ENCHANTMENT)
                        .getEntry(enchantmentKey)
                        .orElseThrow(),
                stack
        );
    }

    private static float normalizeMappedLevel(int level, float[] mapping) {
        if (level <= 0) {
            return 0.0F;
        }
        if (level >= mapping.length) {
            return mapping[mapping.length - 1];
        }
        return mapping[level];
    }

    private static int clampLevel(int level, int maxLevel) {
        if (level <= 0) {
            return 0;
        }
        return Math.min(level, maxLevel);
    }

    private static ClientWorld resolveWorld(ClientWorld world) {
        return world != null ? world : MinecraftClient.getInstance().world;
    }
}
