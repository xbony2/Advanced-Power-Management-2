/*******************************************************************************
 * Copyright (c) 2012-2013 Yancarlo Ramsey and CJ Bowman
 * Licensed as open source with restrictions. Please see attached LICENSE.txt.
 ******************************************************************************/
package com.kaijin.AdvPowerMan;

import ic2.api.item.IC2Items;

import java.io.File;
import java.util.EnumMap;
import java.util.logging.Level;

import org.apache.logging.log4j.Logger;

import com.kaijin.AdvPowerMan.blocks.BlockAdvPwrMan;
import com.kaijin.AdvPowerMan.items.ItemBenchTools;
import com.kaijin.AdvPowerMan.items.ItemBlockAdvPwrMan;
import com.kaijin.AdvPowerMan.items.ItemStorageLinkCard;
import com.kaijin.AdvPowerMan.items.ItemStorageLinkCardCreator;
import com.kaijin.AdvPowerMan.tileentities.TEAdjustableTransformer;
import com.kaijin.AdvPowerMan.tileentities.TEAdvEmitter;
import com.kaijin.AdvPowerMan.tileentities.TEBatteryStation;
import com.kaijin.AdvPowerMan.tileentities.TEChargingBench;
import com.kaijin.AdvPowerMan.tileentities.TEStorageMonitor;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLFingerprintViolationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = "AdvancedPowerManagement", name="Advanced Power Management", version="2.0.0a", dependencies = "required-after:IC2")

public class AdvancedPowerManagement // implements ICraftingHandler
{
	@SidedProxy(clientSide = "com.kaijin.AdvPowerMan.ClientProxy", serverSide = "com.kaijin.AdvPowerMan.CommonProxy")
	public static CommonProxy proxy; //This object will be populated with the class that you choose for the environment

	@Instance("AdvancedPowerManagement")
	public static AdvancedPowerManagement instance; //The instance of the mod that will be defined, populated, and callable

	//Channels for handling packages
	public static EnumMap<Side, FMLEmbeddedChannel> channels;
	public static Logger logger;

	public static Block blockAdvPwrMan;
	public static Item itemBenchTools;
	public static Item itemStorageLinkCard;
	public static Item itemStorageLinkCardCreator;

	@EventHandler
	public static void preInit(FMLPreInitializationEvent event)
	{
		Info.isDebugging = false;
		logger = event.getModLog();
		try
		{
			Configuration configuration = new Configuration(event.getSuggestedConfigurationFile());
			configuration.load();

			// Read or create config file properties, reusing any block and item IDs discovered in old file, if it was present
			Info.isDebugging = configuration.get(configuration.CATEGORY_GENERAL, "debug",  Info.isDebugging).getBoolean(Info.isDebugging);
			configuration.save();
		}
		catch (Exception e)
		{
			logger.warn("Error while trying to access configuration!", e);
			throw new RuntimeException(e);
		}
	}

	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		logger.info("Loading.");

		blockAdvPwrMan = new BlockAdvPwrMan(Material.ground);
		GameRegistry.registerBlock(blockAdvPwrMan, ItemBlockAdvPwrMan.class, "blockAdvPwrMan");

		// Charging Benches
		GameRegistry.registerTileEntity(TEChargingBench.class, "LV Charging Bench"); // Legacy mappings for backward compatibility - we didn't know wtf we were doing when we started this mod :)
		GameRegistry.registerTileEntity(TEChargingBench.class, "MV Charging Bench"); // Legacy
		GameRegistry.registerTileEntity(TEChargingBench.class, "HV Charging Bench"); // Legacy
		GameRegistry.registerTileEntity(TEChargingBench.class, "kaijin.chargingBench"); // Proper mapping

		// Battery Stations
		GameRegistry.registerTileEntity(TEBatteryStation.class, "LV Battery Station"); // Legacy mappings
		GameRegistry.registerTileEntity(TEBatteryStation.class, "MV Battery Station"); // Legacy
		GameRegistry.registerTileEntity(TEBatteryStation.class, "HV Battery Station"); // Legacy
		GameRegistry.registerTileEntity(TEBatteryStation.class, "kaijin.batteryStation"); // Proper mapping

		// Adjustable Transformer
		GameRegistry.registerTileEntity(TEAdjustableTransformer.class, "kaijin.adjTransformer");

		// Storage Monitor
		GameRegistry.registerTileEntity(TEStorageMonitor.class, "kaijin.storageMonitor");

		// Emitters
		GameRegistry.registerTileEntity(TEAdvEmitter.class, "LV Emitter"); // Legacy mappings
		GameRegistry.registerTileEntity(TEAdvEmitter.class, "MV Emitter"); // Legacy
		GameRegistry.registerTileEntity(TEAdvEmitter.class, "HV Emitter"); // Legacy
		GameRegistry.registerTileEntity(TEAdvEmitter.class, "EV Emitter"); // Legacy
		GameRegistry.registerTileEntity(TEAdvEmitter.class, "kaijin.emitter"); // Now legacy as well
		GameRegistry.registerTileEntity(TEAdvEmitter.class, "kaijin.advEmitter"); // Proper mapping

		// Items
		itemBenchTools = new ItemBenchTools("benchTools.toolkit");

		itemStorageLinkCard = new ItemStorageLinkCard("itemStorageLinkCard");

		itemStorageLinkCardCreator = new ItemStorageLinkCardCreator("itemStorageLinkCardCreator");

		//Info.registerTranslations();

		//register channel handler
		channels = NetworkRegistry.INSTANCE.newChannel("IC2NC", ChannelHandler.instance);

		if (event.getSide().isClient())
		{
			FMLEventChannel events = NetworkRegistry.INSTANCE.newEventDrivenChannel("Test");
			events.register(new ClientPacketHandler());
		}
		
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
		proxy.load();

		// For returning charging benches and deconstructing them
		Info.componentCopperCable = IC2Items.getItem("insulatedCopperCableItem").copy();
		Info.componentCopperCable.stackSize = 3;
		Info.componentGoldCable = IC2Items.getItem("insulatedGoldCableItem").copy();
		Info.componentGoldCable.stackSize = 3;
		Info.componentIronCable = IC2Items.getItem("insulatedIronCableItem").copy();
		Info.componentIronCable.stackSize = 3;
		Info.componentBatBox = IC2Items.getItem("batBox").copy();
		Info.componentMFE = IC2Items.getItem("mfeUnit").copy();
		Info.componentMFSU = IC2Items.getItem("mfsUnit").copy();
		Info.componentCircuit = IC2Items.getItem("electronicCircuit").copy();

		// For internal reference to verify items can be placed in inventory.
		Info.ic2overclockerUpg = IC2Items.getItem("overclockerUpgrade").copy();
		Info.ic2transformerUpg = IC2Items.getItem("transformerUpgrade").copy();
		Info.ic2storageUpg = IC2Items.getItem("energyStorageUpgrade").copy();

		Info.ic2WrenchID = Item.getIdFromItem(IC2Items.getItem("wrench").getItem());
		Info.ic2ElectricWrenchID = Item.getIdFromItem(IC2Items.getItem("electricWrench").getItem());

		if (proxy.isServer())
		{
			logger.info("Advanced Power Management 1.7.2.02 loaded.");
		}

		if (Info.isDebugging)
		{
			logger.info("Debugging enabled.");
		}

		logger.info("Done loading.");
	}

	@EventHandler
	public void modsLoaded(FMLPostInitializationEvent event)
	{
		logger.info("Adding crafting recipes.");

		// Charging Bench recipes
		GameRegistry.addRecipe(new ItemStack(blockAdvPwrMan, 1, Info.CB_META + 0), new Object[] {"UUU", "WCW", "WBW", 'U', IC2Items.getItem("insulatedCopperCableItem"), 'W', Blocks.planks, 'C', IC2Items.getItem("electronicCircuit"), 'B', IC2Items.getItem("batBox")});
		GameRegistry.addRecipe(new ItemStack(blockAdvPwrMan, 1, Info.CB_META + 1), new Object[] {"UUU", "WCW", "WBW", 'U', IC2Items.getItem("insulatedGoldCableItem"), 'W', Blocks.planks, 'C', IC2Items.getItem("electronicCircuit"), 'B', IC2Items.getItem("mfeUnit")});
		GameRegistry.addRecipe(new ItemStack(blockAdvPwrMan, 1, Info.CB_META + 2), new Object[] {"UUU", "WCW", "WBW", 'U', IC2Items.getItem("insulatedIronCableItem"), 'W', Blocks.planks, 'C', IC2Items.getItem("electronicCircuit"), 'B', IC2Items.getItem("mfsUnit")});

		// Battery Station recipes
		GameRegistry.addRecipe(new ItemStack(blockAdvPwrMan, 1, Info.BS_META + 0), new Object[] {"UUU", "WCW", "WBW", 'U', IC2Items.getItem("insulatedCopperCableItem"), 'W', Blocks.planks, 'C', IC2Items.getItem("electronicCircuit"), 'B', IC2Items.getItem("lvTransformer")});
		GameRegistry.addRecipe(new ItemStack(blockAdvPwrMan, 1, Info.BS_META + 1), new Object[] {"UUU", "WCW", "WBW", 'U', IC2Items.getItem("insulatedGoldCableItem"), 'W', Blocks.planks, 'C', IC2Items.getItem("electronicCircuit"), 'B', IC2Items.getItem("mvTransformer")});
		GameRegistry.addRecipe(new ItemStack(blockAdvPwrMan, 1, Info.BS_META + 2), new Object[] {"UUU", "WCW", "WBW", 'U', IC2Items.getItem("insulatedIronCableItem"), 'W', Blocks.planks, 'C', IC2Items.getItem("electronicCircuit"), 'B', IC2Items.getItem("hvTransformer")});

		// Adjustable Transformer recipe
		GameRegistry.addRecipe(new ItemStack(blockAdvPwrMan, 1, Info.AT_META), new Object[] {"L", "C", "H", 'L', IC2Items.getItem("lvTransformer"), 'C', IC2Items.getItem("advancedCircuit"), 'H', IC2Items.getItem("hvTransformer")});
		GameRegistry.addRecipe(new ItemStack(blockAdvPwrMan, 1, Info.AT_META), new Object[] {"H", "C", "L", 'H', IC2Items.getItem("hvTransformer"), 'C', IC2Items.getItem("advancedCircuit"), 'L', IC2Items.getItem("lvTransformer")});

		// Storage Monitor recipe
		GameRegistry.addRecipe(new ItemStack(blockAdvPwrMan, 1, Info.SM_META), new Object[] {"WUW", "GCG", "WRW", 'W', Blocks.planks, 'U', IC2Items.getItem("goldCableItem"), 'G', Blocks.glass, 'C', IC2Items.getItem("electronicCircuit"), 'R', Items.redstone});

		// Link Card Creator recipe
		GameRegistry.addRecipe(new ItemStack(itemStorageLinkCardCreator, 1, 0), new Object[] {"U  ", " C ", "  V", 'U', IC2Items.getItem("insulatedCopperCableItem"), 'C', IC2Items.getItem("electronicCircuit"), 'V', Items.paper});

		// Bench Toolkit recipe
		GameRegistry.addRecipe(new ItemStack(itemBenchTools, 1, 0), new Object[] {" I ", "S S", 'I', Items.iron_ingot, 'S', Items.stick});

		// LV, MV, HV Charging Bench Components recipes
		GameRegistry.addShapelessRecipe(new ItemStack(itemBenchTools, 1, 1), new ItemStack(itemBenchTools, 1, 0), new ItemStack(blockAdvPwrMan, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(itemBenchTools, 1, 2), new ItemStack(itemBenchTools, 1, 0), new ItemStack(blockAdvPwrMan, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(itemBenchTools, 1, 3), new ItemStack(itemBenchTools, 1, 0), new ItemStack(blockAdvPwrMan, 1, 2));

		// LV, MV, HV Charging Bench reassembly recipes
		GameRegistry.addShapelessRecipe(new ItemStack(blockAdvPwrMan, 1, 0), new ItemStack(itemBenchTools, 1, 0), new ItemStack(itemBenchTools, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(blockAdvPwrMan, 1, 1), new ItemStack(itemBenchTools, 1, 0), new ItemStack(itemBenchTools, 1, 2));
		GameRegistry.addShapelessRecipe(new ItemStack(blockAdvPwrMan, 1, 2), new ItemStack(itemBenchTools, 1, 0), new ItemStack(itemBenchTools, 1, 3));
	}
/*
	@EventHandler
	public void certificateWarning(FMLFingerprintViolationEvent event)
	{
		FMLLog.warning("[AdvancedPowerManagement] " + "[Certificate Error] Fingerprint does not match! This mod's jar file has been modified from the original version.");
		FMLLog.warning("[AdvancedPowerManagement] " + "[Certificate Error] Expected fingerprint: " + event.expectedFingerprint);
		FMLLog.warning("[AdvancedPowerManagement] " + "[Certificate Error] File: " + event.source.getAbsolutePath());
	}*/
}
