package org.example2.onegai.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
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
    private static final float[] BOOK_TYPE_LEVELS_1_TO_8 = {0.0F, 0.125F, 0.25F, 0.375F, 0.5F, 0.625F, 0.75F, 0.875F, 1.0F};

    private EnchantmentBadgeUtil() {
    }

    public static float getLeftBadgeLevel(ItemStack stack, ClientWorld world) {
        if (!ClientFeatureToggle.isEnabled()) {
            return 0.0F;
        }
        if (stack.isOf(Items.ENCHANTED_BOOK)) {
            return normalizeMappedLevel(getSelectedBookEnchantmentLevel(stack, world), LEVELS_1_TO_5);
        }
        if (isSupportedSword(stack)) {
            return normalizeMappedLevel(getEnchantmentLevel(stack, world, Enchantments.SHARPNESS), LEVELS_1_TO_5);
        }
        if (isSupportedTool(stack)) {
            return normalizeMappedLevel(getEnchantmentLevel(stack, world, Enchantments.EFFICIENCY), LEVELS_1_TO_5);
        }
        if (isSupportedArmor(stack)) {
            return normalizeMappedLevel(getEnchantmentLevel(stack, world, Enchantments.PROTECTION), LEVELS_1_TO_5);
        }
        return 0.0F;
    }

    public static float getRightBadgeLevel(ItemStack stack, ClientWorld world) {
        if (!ClientFeatureToggle.isEnabled()) {
            return 0.0F;
        }
        if (isSupportedSword(stack)) {
            return normalizeMappedLevel(getEnchantmentLevel(stack, world, Enchantments.FIRE_ASPECT), LEVELS_1_TO_5);
        }
        if (isSupportedTool(stack)) {
            return normalizeMappedLevel(getEnchantmentLevel(stack, world, Enchantments.FORTUNE), LEVELS_1_TO_5);
        }
        if (isSupportedBoots(stack)) {
            return normalizeMappedLevel(getEnchantmentLevel(stack, world, Enchantments.DEPTH_STRIDER), LEVELS_1_TO_5);
        }
        return 0.0F;
    }

    public static float getTopLeftBadgeLevel(ItemStack stack, ClientWorld world) {
        if (!ClientFeatureToggle.isEnabled()) {
            return 0.0F;
        }
        if (isSupportedSword(stack)) {
            return normalizeMappedLevel(getEnchantmentLevel(stack, world, Enchantments.KNOCKBACK), LEVELS_1_TO_5);
        }
        return 0.0F;
    }

    public static float getBookBadgeType(ItemStack stack, ClientWorld world) {
        if (!ClientFeatureToggle.isEnabled()) {
            return 0.0F;
        }
        if (!stack.isOf(Items.ENCHANTED_BOOK)) {
            return 0.0F;
        }

        SelectedBookEnchantment selected = getSelectedBookEnchantment(stack, world);
        if (selected == null) {
            return 0.0F;
        }
        return normalizeMappedLevel(selected.typeIndex(), BOOK_TYPE_LEVELS_1_TO_8);
    }

    public static int getProtectionArmorLevel(ItemStack stack, ClientWorld world) {
        if (!ClientFeatureToggle.isEnabled()) {
            return 0;
        }
        if (!(stack.getItem() instanceof ArmorItem)) {
            return 0;
        }
        return clampLevel(getEnchantmentLevel(stack, world, Enchantments.PROTECTION), 4);
    }

    private static SelectedBookEnchantment getSelectedBookEnchantment(ItemStack stack, ClientWorld world) {
        int depthStrider = getEnchantmentLevel(stack, world, Enchantments.DEPTH_STRIDER);
        if (depthStrider > 0) {
            return new SelectedBookEnchantment(8, depthStrider);
        }

        int fortune = getEnchantmentLevel(stack, world, Enchantments.FORTUNE);
        if (fortune > 0) {
            return new SelectedBookEnchantment(7, fortune);
        }

        int fireAspect = getEnchantmentLevel(stack, world, Enchantments.FIRE_ASPECT);
        if (fireAspect > 0) {
            return new SelectedBookEnchantment(5, fireAspect);
        }

        int looting = getEnchantmentLevel(stack, world, Enchantments.LOOTING);
        if (looting > 0) {
            return new SelectedBookEnchantment(4, looting);
        }

        int protection = getEnchantmentLevel(stack, world, Enchantments.PROTECTION);
        if (protection > 0) {
            return new SelectedBookEnchantment(3, protection);
        }

        int sharpness = getEnchantmentLevel(stack, world, Enchantments.SHARPNESS);
        if (sharpness > 0) {
            return new SelectedBookEnchantment(1, sharpness);
        }

        int efficiency = getEnchantmentLevel(stack, world, Enchantments.EFFICIENCY);
        if (efficiency > 0) {
            return new SelectedBookEnchantment(6, efficiency);
        }

        int unbreaking = getEnchantmentLevel(stack, world, Enchantments.UNBREAKING);
        if (unbreaking > 0) {
            return new SelectedBookEnchantment(2, unbreaking);
        }

        return null;
    }

    private static int getSelectedBookEnchantmentLevel(ItemStack stack, ClientWorld world) {
        SelectedBookEnchantment selected = getSelectedBookEnchantment(stack, world);
        return selected != null ? selected.level() : 0;
    }

    private static boolean isSupportedSword(ItemStack stack) {
        return stack.isOf(Items.DIAMOND_SWORD)
                || stack.isOf(Items.IRON_SWORD)
                || stack.isOf(Items.GOLDEN_SWORD)
                || stack.isOf(Items.STONE_SWORD);
    }

    private static boolean isSupportedTool(ItemStack stack) {
        return stack.isOf(Items.DIAMOND_PICKAXE)
                || stack.isOf(Items.IRON_PICKAXE)
                || stack.isOf(Items.GOLDEN_PICKAXE)
                || stack.isOf(Items.DIAMOND_SHOVEL)
                || stack.isOf(Items.IRON_SHOVEL)
                || stack.isOf(Items.GOLDEN_SHOVEL);
    }

    private static boolean isSupportedArmor(ItemStack stack) {
        return stack.isOf(Items.DIAMOND_HELMET)
                || stack.isOf(Items.DIAMOND_CHESTPLATE)
                || stack.isOf(Items.DIAMOND_LEGGINGS)
                || stack.isOf(Items.DIAMOND_BOOTS)
                || stack.isOf(Items.IRON_HELMET)
                || stack.isOf(Items.IRON_CHESTPLATE)
                || stack.isOf(Items.IRON_LEGGINGS)
                || stack.isOf(Items.IRON_BOOTS)
                || stack.isOf(Items.GOLDEN_HELMET)
                || stack.isOf(Items.GOLDEN_CHESTPLATE)
                || stack.isOf(Items.GOLDEN_LEGGINGS)
                || stack.isOf(Items.GOLDEN_BOOTS);
    }

    private static boolean isSupportedBoots(ItemStack stack) {
        return stack.isOf(Items.DIAMOND_BOOTS)
                || stack.isOf(Items.IRON_BOOTS)
                || stack.isOf(Items.GOLDEN_BOOTS);
    }

    private static int getEnchantmentLevel(ItemStack stack, ClientWorld world, RegistryKey<Enchantment> enchantmentKey) {
        ClientWorld actualWorld = resolveWorld(world);
        if (actualWorld == null) {
            return 0;
        }

        var enchantmentEntry = actualWorld.getRegistryManager()
                .get(RegistryKeys.ENCHANTMENT)
                .getEntry(enchantmentKey)
                .orElseThrow();

        if (stack.isOf(Items.ENCHANTED_BOOK)) {
            ItemEnchantmentsComponent storedEnchantments = stack.getOrDefault(
                    DataComponentTypes.STORED_ENCHANTMENTS,
                    ItemEnchantmentsComponent.DEFAULT
            );
            return storedEnchantments.getLevel(enchantmentEntry);
        }

        return EnchantmentHelper.getLevel(enchantmentEntry, stack);
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

    private record SelectedBookEnchantment(int typeIndex, int level) {
    }
}
