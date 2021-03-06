package dev.j3fftw.litexpansion;

import dev.j3fftw.litexpansion.resources.ThoriumResource;
import dev.j3fftw.litexpansion.ticker.PassiveElectricRemovalTicker;
import dev.j3fftw.litexpansion.utils.Constants;
import dev.j3fftw.litexpansion.utils.Log;
import dev.j3fftw.litexpansion.utils.Reflections;
import dev.j3fftw.litexpansion.uumatter.UUMatter;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.researching.Research;
//import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
//import me.mrCookieSlime.Slimefun.cscorelib2.updater.GitHubBuildsUpdater;
//import org.bstats.MetricsBase;
//import org.bstats.bukkit.Metrics;
//import org.bstats.charts.AdvancedPie;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LiteXpansion extends JavaPlugin implements SlimefunAddon {

    private static LiteXpansion instance;

    @Override
    public void onEnable() {
        setInstance(this);

        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }

        //final Metrics metrics = new Metrics(this, 7111);
        // TODO: Disabled for the min, seems to have caused a spike on some servers.
        // Probably due to the immutable copy made by getRawStorage
        // This could be switched back to reflection if we want it to be quick again
//        setupCustomMetrics(metrics);

        /*if (getConfig().getBoolean("options.auto-update") && getDescription().getVersion().startsWith("DEV - ")) {
            new GitHubBuildsUpdater(this, getFile(), "J3fftw1/LiteXpansion/master").start();
        }*/

        registerEnchantments();

        getServer().getScheduler().runTask(this, this::changeSfValues);

        ItemSetup.INSTANCE.init();

        getServer().getPluginManager().registerEvents(new Events(), this);

        UUMatter.INSTANCE.register();

        setupResearches();
        new ThoriumResource().register();

        final PassiveElectricRemovalTicker perTicker = new PassiveElectricRemovalTicker();
        getServer().getScheduler().runTaskTimerAsynchronously(this, perTicker, 20, 20);

//        if (Wrench.wrenchFailChance.getValue() < 0
//            || Wrench.wrenchFailChance.getValue() > 1
//        ) {
//            getLogger().log(Level.SEVERE, "The wrench failure chance must be or be between 0 and 1!");
//            getServer().getPluginManager().disablePlugin(this);
//        }

        //forceMetricsPush(metrics);
    }

    @Override
    public void onDisable() {
        setInstance(null);
    }

    private void registerEnchantments() {
        if (!Enchantment.isAcceptingRegistrations()) {
            Reflections.setStaticField(Enchantment.class, "acceptingNew", true);
        }

        Enchantment glowEnchantment = new GlowEnchant(Constants.GLOW_ENCHANT, new String[] {
            "ADVANCED_CIRCUIT", "NANO_BLADE", "GLASS_CUTTER", "LAPOTRON_CRYSTAL",
            "ADVANCEDLX_SOLAR_HELMET", "HYBRID_SOLAR_HELMET", "ULTIMATE_SOLAR_HELMET",
            "DIAMOND_DRILL"
        });

        // Prevent double-registration errors
        if (Enchantment.getByKey(glowEnchantment.getKey()) == null) {
            Enchantment.registerEnchantment(glowEnchantment);
        }
    }

    private void changeSfValues() {
        // Vanilla SF
        final SlimefunItem energizedPanel = SlimefunItem.getByID("SOLAR_GENERATOR_3");
        if (energizedPanel != null) {
            Reflections.setField(energizedPanel, "dayEnergy", 64);
            Reflections.setField(energizedPanel, "nightEnergy", 32);
        }

        // InfinityExpansion - Halved all values and made infinite panel much less
        Reflections.setField(SlimefunItem.getByID("ADVANCED_PANEL"), "generation", 75);
        Reflections.setField(SlimefunItem.getByID("CELESTIAL_PANEL"), "generation", 250);
        Reflections.setField(SlimefunItem.getByID("VOID_PANEL"), "generation", 1200);
        Reflections.setField(SlimefunItem.getByID("INFINITE_PANEL"), "generation", 20_000);
    }

    private void setupResearches() {
        new Research(new NamespacedKey(this, "sanitizing_foots"),
            696969, "消毒腳, 因為 2k10", 45)
            .addItems(Items.FOOD_SYNTHESIZER)
            .register();

        new Research(new NamespacedKey(this, "superalloys"),
            696970, "超級合金", 35)
            .addItems(Items.THORIUM, Items.MAG_THOR, Items.IRIDIUM, Items.ADVANCED_ALLOY, Items.MIXED_METAL_INGOT,
                Items.REFINED_IRON)
            .register();

        new Research(new NamespacedKey(this, "super_hot_fire"),
            696971, "超級熱火", 31)
            .addItems(Items.NANO_BLADE, Items.ELECTRIC_CHESTPLATE)
            .register();

        new Research(new NamespacedKey(this, "machinereee"),
            696972, "機器reeeeee", 30)
            .addItems(Items.METAL_FORGE, Items.REFINED_SMELTERY, Items.RUBBER_SYNTHESIZER_MACHINE, Items.MANUAL_MILL,
                Items.GENERATOR)
            .register();

        new Research(new NamespacedKey(this, "the_better_panel"),
            696973, "這些是更好的面板", 45)
            .addItems(Items.ADVANCED_SOLAR_PANEL, Items.ULTIMATE_SOLAR_PANEL, Items.HYBRID_SOLAR_PANEL)
            .register();

        new Research(new NamespacedKey(this, "does_this_even_matter"),
            696974, "這有關係嗎(matter)", 150)
            .addItems(Items.UU_MATTER, Items.SCRAP, Items.MASS_FABRICATOR_MACHINE, Items.RECYCLER)
            .register();

        new Research(new NamespacedKey(this, "what_a_configuration"),
            696975, "什麼配置", 39)
            .addItems(Items.CARGO_CONFIGURATOR)
            .register();

        new Research(new NamespacedKey(this, "platings"),
            696976, "電鍍", 40)
            .addItems(Items.IRIDIUM_PLATE, Items.COPPER_PLATE, Items.TIN_PLATE, Items.DIAMOND_PLATE, Items.IRON_PLATE,
                Items.GOLD_PLATE, Items.THORIUM_PLATE)
            .register();

        new Research(new NamespacedKey(this, "rubber"),
            696977, "橡膠", 25)
            .addItems(Items.RUBBER)
            .register();

        new Research(new NamespacedKey(this, "circuits"),
            696978, "電子電路", 25)
            .addItems(Items.ELECTRONIC_CIRCUIT, Items.ADVANCED_CIRCUIT)
            .register();

        new Research(new NamespacedKey(this, "reinforcement_is_coming"),
            696979, "加固即將來臨", 15)
            .addItems(Items.REINFORCED_DOOR, Items.REINFORCED_GLASS, Items.REINFORCED_STONE)
            .register();

        new Research(new NamespacedKey(this, "only_glass"),
            696980, "只有玻璃", 40)
            .addItems(Items.GLASS_CUTTER)
            .register();

        new Research(new NamespacedKey(this, "machine_blocks"),
            696981, "機械外殼", 35)
            .addItems(Items.MACHINE_BLOCK, Items.ADVANCED_MACHINE_BLOCK)
            .register();

        new Research(new NamespacedKey(this, "coal_mesh"),
            696982, "碳纖維網", 30)
            .addItems(Items.COAL_DUST, Items.RAW_CARBON_MESH, Items.RAW_CARBON_FIBRE, Items.CARBON_PLATE)
            .register();

        new Research(new NamespacedKey(this, "what_are_these_cables"),
            696983, "這些電線是甚麼", 25)
            .addItems(Items.UNINSULATED_COPPER_CABLE, Items.COPPER_CABLE,
                Items.UNINSULATED_COPPER_CABLE, Items.TIN_CABLE)
            .register();

        new Research(new NamespacedKey(this, "triple_a"),
            696984, "三重 a", 20)
            .addItems(Items.RE_BATTERY)
            .register();

        new Research(new NamespacedKey(this, "casing"),
            696985, "S 340", 20)
            .addItems(Items.TIN_ITEM_CASING, Items.COPPER_ITEM_CASING)
            .register();

        new Research(new NamespacedKey(this, "solar_helmets"),
            696986, "更多太陽能頭盔", 30)
            .addItems(Items.HYBRID_SOLAR_HELMET, Items.ADVANCED_SOLAR_HELMET, Items.ADVANCEDLX_SOLAR_HELMET,
                Items.CARBONADO_SOLAR_HELMET, Items.ENERGIZED_SOLAR_HELMET, Items.ULTIMATE_SOLAR_HELMET)
            .register();
    }

    /*private void setupCustomMetrics(@Nonnull Metrics metrics) {
        metrics.addCustomChart(new AdvancedPie("blocks_placed", () -> {
            Log.info("--------------------------------");
            Log.info("Starting Metrics collection");
            Log.info("--                            --");
            long start = System.currentTimeMillis();
            final Map<String, Integer> data = new HashMap<>();
            for (World world : Bukkit.getWorlds()) {
                long a = System.currentTimeMillis();
                final BlockStorage storage = BlockStorage.getStorage(world);
                long b = System.currentTimeMillis();
                Log.info("Took {}ms to get storage for {}", b - a, world.getName());
                if (storage == null) {
                    continue;
                }

                long c = System.currentTimeMillis();
                final Map<Location, Config> rawStorage = storage.getRawStorage();
                for (Map.Entry<Location, Config> entry : rawStorage.entrySet()) {
                    final SlimefunItem item = SlimefunItem.getByID(entry.getValue().getString("id"));
                    if (item == null || !(item.getAddon() instanceof LiteXpansion)) {
                        continue;
                    }

                    data.merge(item.getId(), 1, Integer::sum);
                }
                long d = System.currentTimeMillis();
                Log.info("Took {}ms to look through {} items", d - c, rawStorage.size());
            }
            long end = System.currentTimeMillis();
            Log.info("Took {}ms to log metrics", end - start);
            return data;
        }));
    }*/

    /*private void forceMetricsPush(@Nonnull Metrics metrics) {
        MetricsBase base = (MetricsBase) Reflections.getField(Metrics.class, metrics, "metricsBase");
        Reflections.invoke(MetricsBase.class, base, "submitData");
    }*/

    @Nonnull
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    public String getBugTrackerURL() {
        return "https://github.com/Slimefun-Addon-Community/LiteXpansion/issues";
    }

    private static void setInstance(LiteXpansion ins) {
        instance = ins;
    }

    public static LiteXpansion getInstance() {
        return instance;
    }
}
