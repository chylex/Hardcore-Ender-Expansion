package chylex.hee.proxy;
import java.util.Calendar;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBat;
import net.minecraft.client.model.ModelSilverfish;
import net.minecraft.client.renderer.entity.RenderFallingBlock;
import net.minecraft.client.renderer.entity.RenderFireball;
import net.minecraft.client.renderer.entity.RenderLightningBolt;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.renderer.tileentity.RenderEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.Display;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.block.EntityBlockEnderCrystal;
import chylex.hee.entity.block.EntityBlockEnhancedTNTPrimed;
import chylex.hee.entity.block.EntityBlockFallingDragonEgg;
import chylex.hee.entity.block.EntityBlockFallingObsidian;
import chylex.hee.entity.block.EntityBlockTokenHolder;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.EntityBossEnderDemon;
import chylex.hee.entity.boss.EntityMiniBossEnderEye;
import chylex.hee.entity.boss.EntityMiniBossFireFiend;
import chylex.hee.entity.fx.FXEvents;
import chylex.hee.entity.mob.*;
import chylex.hee.entity.projectile.*;
import chylex.hee.entity.technical.EntityTechnicalBase;
import chylex.hee.entity.weather.EntityWeatherLightningBoltDemon;
import chylex.hee.entity.weather.EntityWeatherLightningBoltSafe;
import chylex.hee.game.ConfigHandler;
import chylex.hee.game.commands.HeeClientCommand;
import chylex.hee.gui.GuiItemViewer;
import chylex.hee.init.BlockList;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.compendium.events.CompendiumEventsClient;
import chylex.hee.mechanics.misc.Baconizer;
import chylex.hee.render.OverlayManager;
import chylex.hee.render.RenderNothing;
import chylex.hee.render.block.RenderBlockCrossedDecoration;
import chylex.hee.render.block.RenderBlockEndFlowerPot;
import chylex.hee.render.block.RenderBlockEnhancedTNTPrimed;
import chylex.hee.render.block.RenderBlockObsidianSpecial;
import chylex.hee.render.block.RenderBlockSpookyLeaves;
import chylex.hee.render.block.RenderBlockTokenHolder;
import chylex.hee.render.entity.*;
import chylex.hee.render.item.RenderItemEndermanHead;
import chylex.hee.render.item.RenderItemLootChest;
import chylex.hee.render.model.ModelEnderGuardian;
import chylex.hee.render.model.ModelEndermage;
import chylex.hee.render.model.ModelEndermanHeadBiped;
import chylex.hee.render.model.ModelFireGolem;
import chylex.hee.render.model.ModelHauntedMiner;
import chylex.hee.render.model.ModelScorchingLens;
import chylex.hee.render.projectile.RenderProjectileCurse;
import chylex.hee.render.projectile.RenderProjectileEyeOfEnder;
import chylex.hee.render.projectile.RenderProjectileFiendFireball;
import chylex.hee.render.projectile.RenderProjectilePotion;
import chylex.hee.render.tileentity.RenderTileCustomSpawner;
import chylex.hee.render.tileentity.RenderTileEndPortal;
import chylex.hee.render.tileentity.RenderTileEndermanHead;
import chylex.hee.render.tileentity.RenderTileEssenceAltar;
import chylex.hee.render.tileentity.RenderTileLaserBeam;
import chylex.hee.render.tileentity.RenderTileLootChest;
import chylex.hee.render.weather.RenderWeatherLightningBoltPurple;
import chylex.hee.sound.MusicManager;
import chylex.hee.system.logging.Log;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.tileentity.TileEntityCustomSpawner;
import chylex.hee.tileentity.TileEntityEndermanHead;
import chylex.hee.tileentity.TileEntityEssenceAltar;
import chylex.hee.tileentity.TileEntityLaserBeam;
import chylex.hee.tileentity.TileEntityLootChest;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ModClientProxy extends ModCommonProxy{
	public static final Random seedableRand = new Random();
	public static final ModelEndermanHeadBiped endermanHeadModelBiped = new ModelEndermanHeadBiped();
	
	@Override
	public void loadConfiguration(){
		super.loadConfiguration();
		ConfigHandler.loadClient();
	}
	
	@Override
	public EntityPlayer getClientSidePlayer(){
		return Minecraft.getMinecraft().thePlayer;
	}
	
	@Override
	public void registerRenderers(){
		Stopwatch.time("ModClientProxy - renderers");
		
		renderIdObsidianSpecial = RenderingRegistry.getNextAvailableRenderId();
		renderIdFlowerPot = RenderingRegistry.getNextAvailableRenderId();
		renderIdSpookyLeaves = RenderingRegistry.getNextAvailableRenderId();
		renderIdCrossedDecoration = RenderingRegistry.getNextAvailableRenderId();
		renderIdLootChest = RenderingRegistry.getNextAvailableRenderId();
		
		RenderingRegistry.registerBlockHandler(new RenderBlockObsidianSpecial());
		RenderingRegistry.registerBlockHandler(new RenderBlockEndFlowerPot());
		RenderingRegistry.registerBlockHandler(new RenderBlockSpookyLeaves());
		RenderingRegistry.registerBlockHandler(new RenderBlockCrossedDecoration());
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEssenceAltar.class, new RenderTileEssenceAltar());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEndermanHead.class, new RenderTileEndermanHead());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCustomSpawner.class, new RenderTileCustomSpawner());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLaserBeam.class, new RenderTileLaserBeam());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEndPortal.class, new RenderTileEndPortal());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLootChest.class, new RenderTileLootChest());
		
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockList.loot_chest), new RenderItemLootChest());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockList.enderman_head), new RenderItemEndermanHead());

		RenderingRegistry.registerEntityRenderingHandler(EntityBossDragon.class, new RenderBossDragon());
		RenderingRegistry.registerEntityRenderingHandler(EntityBossEnderDemon.class, new RenderBossEnderDemon());
		
		RenderingRegistry.registerEntityRenderingHandler(EntityMiniBossEnderEye.class, new RenderMiniBossEnderEye());
		RenderingRegistry.registerEntityRenderingHandler(EntityMiniBossFireFiend.class, new RenderMiniBossFireFiend());
		
		RenderingRegistry.registerEntityRenderingHandler(EntityMobEnderman.class, new RenderMobEnderman());
		RenderingRegistry.registerEntityRenderingHandler(EntityMobAngryEnderman.class, new RenderMobAngryEnderman());
		RenderingRegistry.registerEntityRenderingHandler(EntityMobBabyEnderman.class, new RenderMobBabyEnderman());
		RenderingRegistry.registerEntityRenderingHandler(EntityMobHomelandEnderman.class, new RenderMobHomelandEnderman());
		RenderingRegistry.registerEntityRenderingHandler(EntityMobEnderGuardian.class, new RenderTexturedMob(new ModelEnderGuardian(), 0.3F, "ender_guardian.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityMobVampiricBat.class, new RenderTexturedMob(new ModelBat(), 0.25F, "bat_vampiric.png", 0.35F));
		RenderingRegistry.registerEntityRenderingHandler(EntityMobInfestedBat.class, new RenderMobInfestedBat());
		RenderingRegistry.registerEntityRenderingHandler(EntityMobLouse.class, new RenderMobLouse());
		RenderingRegistry.registerEntityRenderingHandler(EntityMobFireGolem.class, new RenderTexturedMob(new ModelFireGolem(), 0.3F, "fire_golem.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityMobScorchingLens.class, new RenderTexturedMob(new ModelScorchingLens(), 0.3F, "scorching_lens.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityMobHauntedMiner.class, new RenderTexturedMob(new ModelHauntedMiner(), 0.5F, "haunted_miner.png", 1.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityMobEndermage.class, new RenderTexturedMob(new ModelEndermage(), 0.3F, "endermage.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityMobSilverfish.class, new RenderTexturedMob(new ModelSilverfish(), 0.3F, "minecraft", "silverfish.png"));
		
		RenderingRegistry.registerEntityRenderingHandler(EntityBlockEnderCrystal.class, new RenderEnderCrystal());
		RenderingRegistry.registerEntityRenderingHandler(EntityBlockFallingObsidian.class, new RenderFallingBlock());
		RenderingRegistry.registerEntityRenderingHandler(EntityBlockFallingDragonEgg.class, new RenderFallingBlock());
		RenderingRegistry.registerEntityRenderingHandler(EntityBlockEnhancedTNTPrimed.class, new RenderBlockEnhancedTNTPrimed());
		RenderingRegistry.registerEntityRenderingHandler(EntityBlockTokenHolder.class, new RenderBlockTokenHolder());
		
		RenderingRegistry.registerEntityRenderingHandler(EntityProjectileFlamingBall.class, new RenderNothing());
		RenderingRegistry.registerEntityRenderingHandler(EntityProjectileMinerShot.class, new RenderNothing());
		RenderingRegistry.registerEntityRenderingHandler(EntityProjectileGolemFireball.class, new RenderFireball(0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityProjectileDragonFireball.class, new RenderFireball(1F));
		RenderingRegistry.registerEntityRenderingHandler(EntityProjectilePotion.class, new RenderProjectilePotion());
		RenderingRegistry.registerEntityRenderingHandler(EntityProjectileSpatialDash.class, new RenderNothing());
		RenderingRegistry.registerEntityRenderingHandler(EntityProjectileCorruptedEnergy.class, new RenderNothing());
		RenderingRegistry.registerEntityRenderingHandler(EntityProjectileFiendFireball.class, new RenderProjectileFiendFireball(0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityProjectileCurse.class, new RenderProjectileCurse());
		RenderingRegistry.registerEntityRenderingHandler(EntityProjectileExpBottleConsistent.class, new RenderSnowball(ItemList.exp_bottle));
		RenderingRegistry.registerEntityRenderingHandler(EntityProjectileEyeOfEnder.class, new RenderProjectileEyeOfEnder());

		RenderingRegistry.registerEntityRenderingHandler(EntityWeatherLightningBoltSafe.class, new RenderLightningBolt());
		RenderingRegistry.registerEntityRenderingHandler(EntityWeatherLightningBoltDemon.class, new RenderWeatherLightningBoltPurple());
		
		RenderingRegistry.registerEntityRenderingHandler(EntityTechnicalBase.class, new RenderNothing());

		Stopwatch.finish("ModClientProxy - renderers");
		
		Baconizer.load();
	}
	
	@Override
	public void registerSidedEvents(){
		Stopwatch.time("ModClientProxy - events");
		
		OverlayManager.register();
		CompendiumEventsClient.register();
		// TODO CharmPouchHandlerClient.register();
		MusicManager.register();
		FXEvents.register();
		HeeClientCommand.register();
		
		Stopwatch.finish("ModClientProxy - events");
	}
	
	@Override
	public void openGui(String type){
		if (type.equals("itemviewer"))Minecraft.getMinecraft().displayGuiScreen(new GuiItemViewer());
		else if (type.equals("speedup"))Minecraft.getMinecraft().thePlayer.capabilities.setFlySpeed(0.3F);
	}
	
	@Override
	public void sendMessage(MessageType msgType, int[] data){
		switch(msgType){
			case DEBUG_TITLE_SET:
				Display.setTitle(Display.getTitle()+" - HardcoreEnderExpansion - "+(Log.isDeobfEnvironment ? "dev" : "debug")+' '+HardcoreEnderExpansion.modVersion);
				break;
		}
	}
	
	/**
	 * Mode 0: enable on April Fools
	 * Mode 1: always enabled
	 * Mode 2: never enabled
	 */
	public static void loadEnderbacon(int mode){
		if (mode == 1)hardcoreEnderbacon = true;
		else if (mode == 0){
			Calendar cal = Calendar.getInstance();
			hardcoreEnderbacon = cal.get(Calendar.MONTH) == 3 && cal.get(Calendar.DAY_OF_MONTH) == 1;
		}
	}
}
