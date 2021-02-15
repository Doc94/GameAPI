package com.scarabcoder.gameapi.util.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Inventory click data.
 *
 * @author iSach
 * @since 08-09-2016
 */
public class ClickData {

    private Inventory inventory;
    private Player clicker;
    private InventoryAction action;
    private ClickType clickt;
    private ItemStack clicked;
    private int slot;

    ClickData(Inventory inventory, Player clicker, ClickType clickType, InventoryAction action, ItemStack clicked, int slot) {
        this.inventory = inventory;
        this.clicker = clicker;
        this.action = action;
        this.clicked = clicked;
        this.slot = slot;
        this.clickt = clickType;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public ItemStack getClicked() {
        return clicked;
    }

    public int getSlot() {
        return slot;
    }

    public InventoryAction getAction() {
        return action;
    }

    public ClickType getClickType() {
        return clickt;
    }

    public Player getClicker() {
        return clicker;
    }
}
