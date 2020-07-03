package dev.j3fftw.litexpansion.items;

import dev.j3fftw.litexpansion.Items;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.ChargableItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.ItemUseHandler;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FoodSynthesizer extends ChargableItem {

    public FoodSynthesizer() {
        super(Items.LITEXPANSION, Items.FOOD_SYNTHESIZER, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.PLASTIC_SHEET, new ItemStack(Material.COOKED_BEEF), SlimefunItems.PLASTIC_SHEET,
            new ItemStack(Material.APPLE), SlimefunItems.COOLER, new ItemStack(Material.APPLE),
            SlimefunItems.PLASTIC_SHEET, new ItemStack(Material.COOKED_BEEF), SlimefunItems.PLASTIC_SHEET
        });
    }

    @Override
    public void preRegister() {
        addItemHandler((ItemUseHandler) PlayerRightClickEvent::cancel);
    }
}
