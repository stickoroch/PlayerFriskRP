package stickoroch.playerfriskrp;

import com.google.common.collect.Lists;
import com.sun.tools.javac.jvm.Items;
import jdk.internal.icu.text.UnicodeSet;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class FriskSession {

    public ItemStack[] invBefore;

    public Player frisker;
    public Player suspect;
    public int timer;
    public boolean friskInProcess = false;
    public Inventory pidoriNaBukkite;

    public FriskSession(Player frisker, Player suspect){
        timer = PlayerFriskRP.getInstance().getConfig().getInt("friskTime");
        this.frisker = frisker;
        this.suspect = suspect;

        List<ItemStack> a = new ArrayList<>();
        for (int i = 0; i < 36; i++) {
            ItemStack b = suspect.getInventory().getContents()[i];
            if(b == null || b.getType() == Material.AIR) continue;
            a.add(b.clone());
        }
        invBefore = a.toArray(new ItemStack[a.size()]);

    }

    public void updateTimer() {
        String f =PlayerFriskRP.getInstance().getConfig().getString("progressBarFr")
                .replaceAll("%time%", timer + "").replace("&", "\u00a7");
        frisker.sendActionBar(f);
        String s =PlayerFriskRP.getInstance().getConfig().getString("progressBarSus")
                .replaceAll("%time%", timer + "").replace("&", "\u00a7");
        suspect.sendActionBar(s);
    }


    public void friskZalupa(){
        String s =PlayerFriskRP.getInstance().getConfig().getString("zalupafrisk")
                .replaceAll("&", "\u00a7");
        frisker.sendActionBar(s);
        suspect.sendActionBar(s);
    }

    public void startFrisk() {
        pidoriNaBukkite = Bukkit.createInventory(suspect, 36, PlayerFriskRP.getInstance().getConfig()
                .getString("titleOfInv").replace('&', '\u00a7').replaceAll("%player%", suspect.getName()));
        suspect.closeInventory(InventoryCloseEvent.Reason.CANT_USE);
        pidoriNaBukkite.setContents(Arrays.copyOfRange(suspect.getInventory().getContents(), 0, 36));
        frisker.openInventory(pidoriNaBukkite);

    }


    public void friskCanceled() {
        if(pidoriNaBukkite == null) return;
        //replace inv
        for (int i = 0; i < 36; i++) {
            suspect.getInventory().setItem(i, pidoriNaBukkite.getItem(i));
        }

        ItemStack[] invBefore = this.invBefore.clone();
        ItemStack[] invAfter = pidoriNaBukkite.getContents().clone();

        List<ItemStack> invBeforeList = new ArrayList<>();
        List<ItemStack> invAfterList = new ArrayList<>();
        for (int i = 0; i < invBefore.length; i++) {
            if(invBefore[i] != null && invBefore[i].getType() != Material.AIR){

                int amount = invBefore[i].getAmount();
                for (int j = 0; j < invBefore.length; j++) {
                    if(i == j)continue;
                    if(invBefore[j] != null && invBefore[j].getType() != Material.AIR
                            && invBefore[i].isSimilar(invBefore[j])){
                        amount += invBefore[j].getAmount();
                        invBefore[j] = null;
                    }
                }
                invBefore[i].setAmount(amount);
                invBeforeList.add(invBefore[i].clone());
            }
        }
        for (int i = 0; i < invAfter.length; i++) {
            if (invAfter[i] != null && invAfter[i].getType() != Material.AIR) {

                int amount = invAfter[i].getAmount();
                for (int j = 0; j < invAfter.length; j++) {
                    if (i == j) continue;
                    if (invAfter[j] != null && invAfter[j].getType() != Material.AIR
                            && invAfter[i].isSimilar(invAfter[j])) {
                        amount += invAfter[j].getAmount();
                        invAfter[j] = null;
                    }
                }
                invAfter[i].setAmount(amount);
                invAfterList.add(invAfter[i].clone());
            }
        }

        List<ItemStack> addedItems = new ArrayList<>();
        List<ItemStack> removedItems = new ArrayList<>();
        //detect items changes
        for (int i = 0; i < invBeforeList.size(); i++) {
            for (int j = 0; j < invAfterList.size(); j++) {
                if(invBeforeList.get(i).isSimilar(invAfterList.get(j))){
                    int am = invBeforeList.get(i).getAmount() - invAfterList.get(j).getAmount();
                    invBeforeList.get(i).setAmount(Math.abs(am));

                    if(am > 0) removedItems.add(invBeforeList.get(i));
                    else if(am < 0) addedItems.add(invBeforeList.get(i));

                    invBeforeList.set(i, new ItemStack(Material.AIR));
                    invAfterList.set(j, new ItemStack(Material.AIR));
                }
            }
        }

        addedItems.addAll(invAfterList
                .stream().filter(x -> x.getType() != Material.AIR).collect(Collectors.toList()));
        removedItems.addAll(invBeforeList
                .stream().filter(x -> x.getType() != Material.AIR).collect(Collectors.toList()));


        if(addedItems.size() == 0 && removedItems.size() == 0) return;

        String[] ms = PlayerFriskRP.getInstance().getConfig().getString("titleOfList")
                .replaceAll("&", "\u00a7").split("!n");
        for (String s : ms) {
            suspect.sendMessage(s);
        }

        if(addedItems.size() > 0){
            for (ItemStack i:addedItems) {
                ms = getItemData(i, PlayerFriskRP.getInstance().getConfig()
                        .getString("addedItem")).split("!n");
                for (String s : ms) {
                    suspect.sendMessage(s);
                }
            }
        }

        if(removedItems.size() > 0){
            for (ItemStack i:removedItems) {
                ms = getItemData(i, PlayerFriskRP.getInstance().getConfig()
                        .getString("removedItem")).split("!n");

                for (String s : ms) {
                    suspect.sendMessage(s);
                }
            }
        }


    }

    public String getItemData(ItemStack i, String itemFormat){
        String str = itemFormat.replaceAll("&", "\u00a7");
        List<String> enchs = new ArrayList<>();
        String enchF = PlayerFriskRP.getInstance().getConfig().getString("enchFormat")
                .replace('&', '\u00a7');

        if(i.getEnchantments().size() > 0){
            for (Map.Entry<Enchantment, Integer> e : i.getEnchantments().entrySet()){
                enchs.add(enchF.replaceAll("%ench%", PlayerFriskRP.getName(e.getKey().getKey()) + "")
                        .replaceAll("%lvl%", PlayerFriskRP.getLvl(e.getValue())));
            }
        }else if(i.hasItemMeta() && i.getItemMeta() instanceof EnchantmentStorageMeta){
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) i.getItemMeta();

            for (Map.Entry<Enchantment, Integer> e : meta.getStoredEnchants().entrySet()){
                enchs.add(enchF.replaceAll("%ench%", PlayerFriskRP.getName(e.getKey().getKey()) + "")
                        .replaceAll("%lvl%", PlayerFriskRP.getLvl(e.getValue())));
            }
        }
        if(i.hasItemMeta() && i.getItemMeta() instanceof PotionMeta){
            PotionMeta meta = (PotionMeta) i.getItemMeta();
            String name = (i.hasItemMeta() && i.getItemMeta().hasDisplayName()) ?
                    i.getItemMeta().getDisplayName() : PlayerFriskRP.getNameOfPotion(meta, i.getType());
            if(name == null) name = i.getType().toString();

            str = str.replaceAll("%itemName%", name).replaceAll("%amount%", i.getAmount()+"");

            for (String s : enchs) {
                str += s;
            }
       }else{
            String name = (i.hasItemMeta() && i.getItemMeta().hasDisplayName()) ?
                    i.getItemMeta().getDisplayName() : PlayerFriskRP.getName(i.getType());
            if(name == null) name = i.getType().toString();


            str = str.replaceAll("%itemName%", name).replaceAll("%amount%", i.getAmount()+"");

            for (String s : enchs) {
                str += s;
            }
        }

        return str;
    }


}
