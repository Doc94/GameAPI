package com.scarabcoder.gameapi.util.gui;

import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 29-09-2017 for gothapi-parent.
 *
 * @author Doc
 */
public abstract class MenuGUI<PI extends JavaPlugin> implements Listener {

    /**
     * Click Runnables maps.
     * <p>
     * Key: Item
     * Value: ClickRunnable to call when item is clicked.
     */
    private Map<Inventory, Map<ItemStack, ClickRunnable>> clickRunnableMap = new HashMap<>();

    public MenuGUI(PI instancePlugin) {
        Bukkit.getServer().getPluginManager().registerEvents(this, instancePlugin);
        Bukkit.getServer().getLogger().info("[MenuLib] " + getClass().getSimpleName() + " instance of MenuGUI registered.");
    }

    /**
     * Open inventory for player using items preloaded.
     *
     * @param player Player to open inventory
     */
    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(null, getSize(), getName());
        putItems(inventory, player);
        player.openInventory(inventory);
    }

    /**
     * put Item to load inventory
     *
     * @param inventory     Inventory to put item
     * @param slot          Slot of inventory to put item
     * @param itemStack     Item to put
     * @param clickRunnable runnable for click item.
     */
    protected void putItem(Inventory inventory, int slot, ItemStack itemStack, ClickRunnable clickRunnable) {
        Validate.notNull(itemStack);
        Validate.notNull(clickRunnable);

        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;
            itemMeta.addItemFlags(ItemFlag.values());
            itemStack.setItemMeta(itemMeta);
        }

        inventory.setItem(slot, itemStack);
        if (clickRunnableMap.containsKey(inventory)) {
            Map<ItemStack, ClickRunnable> map = clickRunnableMap.get(inventory);
            map.put(itemStack, clickRunnable);
        } else {
            Map<ItemStack, ClickRunnable> map = new HashMap<>();
            map.put(itemStack, clickRunnable);
            clickRunnableMap.put(inventory, map);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        // Check Item clicked isn't null
        if (event.getCurrentItem() == null) {
            return;
        }

        // Check clicker is player
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        // Check Inventory is the good one
        if (!event.getView().getTitle().contains(getName())) {
            return;
        }

        // Check that Inventory is valid.
        if (!clickRunnableMap.containsKey(event.getInventory())) {
            return;
        }

        // Check that Item is meant to do an action.
        if (!clickRunnableMap.get(event.getInventory()).containsKey(event.getCurrentItem())) {
            return;
        }

        event.setCancelled(true);

        ClickRunnable clickRunnable = clickRunnableMap.get(event.getInventory()).get(event.getCurrentItem());

        // Check clickrunnable isn't null.
        if (clickRunnable == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        clickRunnable.run(new ClickData(event.getInventory(), player, event.getClick(), event.getAction(), event.getCurrentItem(), event.getSlot()));
        ((Player) event.getWhoClicked()).updateInventory();
    }


    protected abstract void putItems(Inventory inventory, Player player);

    protected abstract int getSize();

    protected abstract String getName();

}
