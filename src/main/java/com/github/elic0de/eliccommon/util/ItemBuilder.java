package pakira.util;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    private ItemBuilder(@NotNull Material type, int amount) {
        Preconditions.checkArgument(type != null, "Cannot create ItemBuilder for null Material");
        Preconditions.checkArgument(type.isItem(), "Illegal material!");

        this.item = new ItemStack(type);
        this.meta = item.getItemMeta();
        this.amount(amount);
    }

    private ItemBuilder(@NotNull ItemStack item) {
        Preconditions.checkArgument(item != null, "Cannot modify a null item");
        Preconditions.checkArgument(item.getType().isItem(), "Illegal material!");

        this.item = item.clone();
        this.meta = item.getItemMeta();
    }

    @NotNull
    public static ItemBuilder of(@NotNull Material type, int amount) {
        return new ItemBuilder(type, amount);
    }

    @NotNull
    public static ItemBuilder of(@NotNull Material type) {
        return new ItemBuilder(type, 1);
    }


    @NotNull
    public static ItemBuilder modify(@NotNull ItemStack item) {
        return new ItemBuilder(item);
    }

    public boolean isSupportedMeta(@NotNull Class<? extends ItemMeta> type) {
        return type != null && type.isInstance(meta);
    }

    @NotNull
    public <T extends ItemMeta> ItemBuilder specific(@NotNull Class<@NotNull T> type, @NotNull Consumer<@NotNull T> applier) {
        Preconditions.checkArgument(type != null, "Cannot apply meta for type null");
        Preconditions.checkArgument(isSupportedMeta(type), "The specified ItemMeta type is not supported by this ItemBuilder instance");
        Preconditions.checkArgument(applier != null, "Application function must not be null");

        applier.accept(type.cast(meta));
        return this;
    }

    @NotNull
    public ItemBuilder applyPersistentData(@NotNull Consumer<@NotNull PersistentDataContainer> applier) {
        Preconditions.checkArgument(applier != null, "Application function must not be null");

        applier.accept(meta.getPersistentDataContainer());
        return this;
    }

    @NotNull
    public ItemBuilder name(@NotNull String name) {
        final String coloredName = ChatColor.translateAlternateColorCodes('&', name);
        this.meta.displayName(Component.text(coloredName));
        return this;
    }

    @NotNull
    public ItemBuilder lore(@Nullable String... lore) {
        if (lore == null || lore.length > 0) {
            this.meta.lore(Arrays.stream(lore).map(s -> Component.text(ChatColor.translateAlternateColorCodes('&', s))).collect(Collectors.toList()));
        }

        return this;
    }

    @NotNull
    public ItemBuilder lore(@NotNull List<String> lore) {
        this.meta.lore(lore.stream().map(s -> Component.text(ChatColor.translateAlternateColorCodes('&', s))).collect(Collectors.toList()));
        return this;
    }

    @NotNull
    public ItemBuilder damage(int damage) {
        ((Damageable) meta).damage(damage);
        return this;
    }

    @NotNull
    public ItemBuilder amount(int amount) {
        this.item.setAmount(amount);
        return this;
    }

    @NotNull
    public ItemBuilder enchantment(@NotNull Enchantment enchantment, int level) {
        this.meta.addEnchant(enchantment, level, true);
        return this;
    }

    @NotNull
    public ItemBuilder attribute(@NotNull Attribute attribute, @NotNull AttributeModifier modifier) {
        this.meta.addAttributeModifier(attribute, modifier);
        return this;
    }

    @NotNull
    public ItemBuilder flags(@Nullable ItemFlag @NotNull... flags) {
        if (flags.length > 0) {
            this.meta.addItemFlags(flags);
        }

        return this;
    }

    @NotNull
    public ItemBuilder unbreakable() {
        this.meta.setUnbreakable(true);
        return this;
    }

    @NotNull
    public ItemBuilder localizedName(@NotNull String name) {
        this.meta.setLocalizedName(name);
        return this;
    }

    @NotNull
    public ItemBuilder modelData(int data) {
        this.meta.setCustomModelData(data);
        return this;
    }

    @NotNull
    public ItemStack build() {
        this.item.setItemMeta(meta);
        return item;
    }
}
