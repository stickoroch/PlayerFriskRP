package stickoroch.playerfriskrp;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public final class PlayerFriskRP extends JavaPlugin {

    public static PlayerFriskRP instance;
    public static PlayerFriskRP getInstance(){return instance;}
    public static FriskManager friskManager;
    public static FriskManager getFriskManager(){return friskManager;}
    public static YamlConfiguration lang;
    public static YamlConfiguration getLang(){return lang;}



    @Override
    public void onEnable() {
        File l = new File(getDataFolder(), "lang.yml");
        if (!l.exists()) {
            l.getParentFile().mkdirs();
            saveResource("lang.yml", false);
        }

        lang = new YamlConfiguration();
        try {
            lang.load(l);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        instance = this;
        friskManager = new FriskManager();
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(friskManager,this);
        getCommand("frisk").setExecutor(new CommandFrisk());
        getCommand("reload").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
                sender.sendMessage("Config has bees reloaded!");
                reloadConfig();
                return true;
            }
        });
    }

    public static String getName(Material m){
        return lang.getString(m.getTranslationKey());
    }
    public static String getName(NamespacedKey m){
        return lang.getString(m.toString());
    }
    public static String getNameOfPotion(PotionMeta potion, Material material) {
        String name = "Some Potion";


        if(potion.getBasePotionData().getType().toString().equals("POTION") || potion.getBasePotionData().getType().toString().equals("TIPPED_ARROW")  ){
            name = lang.getString(lang.getCurrentPath()+"."+material.toString().toLowerCase(Locale.ROOT));
            return name;
        }
        name = lang.getString(material.toString().toLowerCase(Locale.ROOT)
                + "." + potion.getBasePotionData().getType().toString().toLowerCase(Locale.ROOT));


        return name;
    }

    public static String getLvl(Integer l){
        switch (l){
            case 1:
                return "";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            case 6:
                return "VI";
            case 7:
                return "VII";
            case 8:
                return "VIII";
            case 9:
                return "IX";
            case 10:
                return "X";
        }
        return l + "";
    }
    @Override
    public void onDisable() {
    }
}
