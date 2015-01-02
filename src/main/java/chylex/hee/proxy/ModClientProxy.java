package chylex.hee.proxy;
import java.util.Calendar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBat;
import net.minecraft.client.renderer.entity.RenderFallingBlock;
import net.minecraft.client.renderer.entity.RenderFireball;
import net.minecraft.client.renderer.entity.RenderLightningBolt;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.renderer.tileentity.RenderEnderCrystal;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.stats.IStatStringFormat;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.MinecraftForgeClient;
import chylex.hee.block.BlockList;
import chylex.hee.entity.block.EntityBlockEnderCrystal;
import chylex.hee.entity.block.EntityBlockEnhancedTNTPrimed;
import chylex.hee.entity.block.EntityBlockFallingDragonEgg;
import chylex.hee.entity.block.EntityBlockFallingObsidian;
import chylex.hee.entity.block.EntityBlockHomelandCache;
import chylex.hee.entity.block.EntityBlockTempleDragonEgg;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.EntityBossEnderDemon;
import chylex.hee.entity.boss.EntityMiniBossEnderEye;
import chylex.hee.entity.boss.EntityMiniBossFireFiend;
import chylex.hee.entity.fx.FXEvents;
import chylex.hee.entity.mob.EntityMobAngryEnderman;
import chylex.hee.entity.mob.EntityMobBabyEnderman;
import chylex.hee.entity.mob.EntityMobCorporealMirage;
import chylex.hee.entity.mob.EntityMobEnderGuardian;
import chylex.hee.entity.mob.EntityMobEndermage;
import chylex.hee.entity.mob.EntityMobEnderman;
import chylex.hee.entity.mob.EntityMobFireGolem;
import chylex.hee.entity.mob.EntityMobHauntedMiner;
import chylex.hee.entity.mob.EntityMobHomelandEnderman;
import chylex.hee.entity.mob.EntityMobInfestedBat;
import chylex.hee.entity.mob.EntityMobLouse;
import chylex.hee.entity.mob.EntityMobParalyzedEnderman;
import chylex.hee.entity.mob.EntityMobScorchingLens;
import chylex.hee.entity.mob.EntityMobVampiricBat;
import chylex.hee.entity.projectile.EntityProjectileCorporealMirageOrb;
import chylex.hee.entity.projectile.EntityProjectileCorruptedEnergy;
import chylex.hee.entity.projectile.EntityProjectileCurse;
import chylex.hee.entity.projectile.EntityProjectileDragonFireball;
import chylex.hee.entity.projectile.EntityProjectileDragonFreezeball;
import chylex.hee.entity.projectile.EntityProjectileExpBottleConsistent;
import chylex.hee.entity.projectile.EntityProjectileFiendFireball;
import chylex.hee.entity.projectile.EntityProjectileFlamingBall;
import chylex.hee.entity.projectile.EntityProjectileGolemFireball;
import chylex.hee.entity.projectile.EntityProjectileMinerShot;
import chylex.hee.entity.projectile.EntityProjectilePotion;
import chylex.hee.entity.projectile.EntityProjectileSpatialDash;
import chylex.hee.entity.technical.EntityTechnicalBiomeInteraction;
import chylex.hee.entity.technical.EntityTechnicalCurseBlock;
import chylex.hee.entity.technical.EntityTechnicalCurseEntity;
import chylex.hee.entity.technical.EntityTechnicalPuzzleChain;
import chylex.hee.entity.technical.EntityTechnicalPuzzleSolved;
import chylex.hee.entity.technical.EntityTechnicalVoidChest;
import chylex.hee.entity.weather.EntityWeatherLightningBoltDemon;
import chylex.hee.entity.weather.EntityWeatherLightningBoltSafe;
import chylex.hee.gui.GuiItemViewer;
import chylex.hee.gui.GuiTransportBeacon;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.charms.handler.CharmPouchHandlerClient;
import chylex.hee.mechanics.compendium.events.CompendiumEventsClient;
import chylex.hee.mechanics.compendium.player.PlayerCompendiumData;
import chylex.hee.mechanics.misc.Baconizer;
import chylex.hee.render.OverlayManager;
import chylex.hee.render.RenderNothing;
import chylex.hee.render.block.RenderBlockCrossedDecoration;
import chylex.hee.render.block.RenderBlockEndFlowerPot;
import chylex.hee.render.block.RenderBlockEnhancedTNTPrimed;
import chylex.hee.render.block.RenderBlockHomelandCache;
import chylex.hee.render.block.RenderBlockObsidianSpecial;
import chylex.hee.render.block.RenderBlockSpookyLeaves;
import chylex.hee.render.block.RenderBlockTransportBeacon;
import chylex.hee.render.block.RenderBlockVoidChest;
import chylex.hee.render.entity.RenderBossDragon;
import chylex.hee.render.entity.RenderBossEnderDemon;
import chylex.hee.render.entity.RenderMiniBossEnderEye;
import chylex.hee.render.entity.RenderMiniBossFireFiend;
import chylex.hee.render.entity.RenderMobAngryEnderman;
import chylex.hee.render.entity.RenderMobBabyEnderman;
import chylex.hee.render.entity.RenderMobCorporealMirage;
import chylex.hee.render.entity.RenderMobEnderman;
import chylex.hee.render.entity.RenderMobHomelandEnderman;
import chylex.hee.render.entity.RenderMobInfestedBat;
import chylex.hee.render.entity.RenderMobLouse;
import chylex.hee.render.entity.RenderMobParalyzedEnderman;
import chylex.hee.render.entity.RenderTexturedMob;
import chylex.hee.render.item.RenderItemVoidChest;
import chylex.hee.render.model.ModelEnderGuardian;
import chylex.hee.render.model.ModelEndermage;
import chylex.hee.render.model.ModelEndermanHeadBiped;
import chylex.hee.render.model.ModelFireGolem;
import chylex.hee.render.model.ModelHauntedMiner;
import chylex.hee.render.model.ModelScorchingLens;
import chylex.hee.render.projectile.RenderProjectileCurse;
import chylex.hee.render.projectile.RenderProjectileFiendFireball;
import chylex.hee.render.projectile.RenderProjectilePotion;
import chylex.hee.render.tileentity.RenderTileCustomSpawner;
import chylex.hee.render.tileentity.RenderTileEndPortalFixed;
import chylex.hee.render.tileentity.RenderTileEndermanHead;
import chylex.hee.render.tileentity.RenderTileEssenceAltar;
import chylex.hee.render.tileentity.RenderTileLaserBeam;
import chylex.hee.render.tileentity.RenderTileTransportBeacon;
import chylex.hee.render.tileentity.RenderTileVoidChest;
import chylex.hee.render.weather.RenderWeatherLightningBoltPurple;
import chylex.hee.sound.MusicManager;
import chylex.hee.system.ConfigHandler;
import chylex.hee.system.achievements.AchievementManager;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.tileentity.TileEntityCustomSpawner;
import chylex.hee.tileentity.TileEntityEndermanHead;
import chylex.hee.tileentity.TileEntityEssenceAltar;
import chylex.hee.tileentity.TileEntityLaserBeam;
import chylex.hee.tileentity.TileEntityTransportBeacon;
import chylex.hee.tileentity.TileEntityVoidChest;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ModClientProxy extends ModCommonProxy{
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
	public PlayerCompendiumData getClientCompendiumData(){
		return CompendiumEventsClient.getClientData();
	}
	
	@Override
	public void registerRenderers(){
		Stopwatch.time("ModClientProxy - renderers");
		
		renderIdObsidianSpecial = RenderingRegistry.getNextAvailableRenderId();
		renderIdFlowerPot = RenderingRegistry.getNextAvailableRenderId();
		renderIdSpookyLeaves = RenderingRegistry.getNextAvailableRenderId();
		renderIdCrossedDecoration = RenderingRegistry.getNextAvailableRenderId();
		renderIdVoidChest = RenderingRegistry.getNextAvailableRenderId();
		renderIdTransportBeacon = RenderingRegistry.getNextAvailableRenderId();
		
		RenderingRegistry.registerBlockHandler(new RenderBlockObsidianSpecial());
		RenderingRegistry.registerBlockHandler(new RenderBlockEndFlowerPot());
		RenderingRegistry.registerBlockHandler(new RenderBlockSpookyLeaves());
		RenderingRegistry.registerBlockHandler(new RenderBlockCrossedDecoration());
		RenderingRegistry.registerBlockHandler(new RenderBlockVoidChest());
		RenderingRegistry.registerBlockHandler(new RenderBlockTransportBeacon());
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEssenceAltar.class, new RenderTileEssenceAltar());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEndermanHead.class, new RenderTileEndermanHead());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCustomSpawner.class, new RenderTileCustomSpawner());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLaserBeam.class, new RenderTileLaserBeam());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVoidChest.class, new RenderTileVoidChest());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTransportBeacon.class, new RenderTileTransportBeacon());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEndPortal.class, new RenderTileEndPortalFixed());
		
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockList.void_chest), new RenderItemVoidChest());

		RenderingRegistry.registerEntityRenderingHandler(EntityBossDragon.class, new RenderBossDragon());
		RenderingRegistry.registerEntityRenderingHandler(EntityBossEnderDemon.class, new RenderBossEnderDemon());
		
		RenderingRegistry.registerEntityRenderingHandler(EntityMiniBossEnderEye.class, new RenderMiniBossEnderEye());
		RenderingRegistry.registerEntityRenderingHandler(EntityMiniBossFireFiend.class, new RenderMiniBossFireFiend());
		
		RenderingRegistry.registerEntityRenderingHandler(EntityMobEnderman.class, new RenderMobEnderman());
		RenderingRegistry.registerEntityRenderingHandler(EntityMobAngryEnderman.class, new RenderMobAngryEnderman());
		RenderingRegistry.registerEntityRenderingHandler(EntityMobBabyEnderman.class, new RenderMobBabyEnderman());
		RenderingRegistry.registerEntityRenderingHandler(EntityMobParalyzedEnderman.class, new RenderMobParalyzedEnderman());
		RenderingRegistry.registerEntityRenderingHandler(EntityMobHomelandEnderman.class, new RenderMobHomelandEnderman());
		RenderingRegistry.registerEntityRenderingHandler(EntityMobEnderGuardian.class, new RenderTexturedMob(new ModelEnderGuardian(), 0.3F, "ender_guardian.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityMobVampiricBat.class, new RenderTexturedMob(new ModelBat(), 0.25F, "bat_vampiric.png", 0.35F));
		RenderingRegistry.registerEntityRenderingHandler(EntityMobInfestedBat.class, new RenderMobInfestedBat());
		RenderingRegistry.registerEntityRenderingHandler(EntityMobLouse.class, new RenderMobLouse());
		RenderingRegistry.registerEntityRenderingHandler(EntityMobFireGolem.class, new RenderTexturedMob(new ModelFireGolem(), 0.3F, "fire_golem.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityMobScorchingLens.class, new RenderTexturedMob(new ModelScorchingLens(), 0.3F, "scorching_lens.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityMobHauntedMiner.class, new RenderTexturedMob(new ModelHauntedMiner(), 0.5F, "haunted_miner.png", 1.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityMobCorporealMirage.class, new RenderMobCorporealMirage());
		RenderingRegistry.registerEntityRenderingHandler(EntityMobEndermage.class, new RenderTexturedMob(new ModelEndermage(), 0.3F, "endermage.png"));
		
		RenderingRegistry.registerEntityRenderingHandler(EntityBlockEnderCrystal.class, new RenderEnderCrystal());
		RenderingRegistry.registerEntityRenderingHandler(EntityBlockFallingObsidian.class, new RenderFallingBlock());
		RenderingRegistry.registerEntityRenderingHandler(EntityBlockFallingDragonEgg.class, new RenderFallingBlock());
		RenderingRegistry.registerEntityRenderingHandler(EntityBlockTempleDragonEgg.class, new RenderFallingBlock());
		RenderingRegistry.registerEntityRenderingHandler(EntityBlockEnhancedTNTPrimed.class, new RenderBlockEnhancedTNTPrimed());
		RenderingRegistry.registerEntityRenderingHandler(EntityBlockHomelandCache.class, new RenderBlockHomelandCache());
		
		RenderingRegistry.registerEntityRenderingHandler(EntityProjectileFlamingBall.class, new RenderNothing());
		RenderingRegistry.registerEntityRenderingHandler(EntityProjectileMinerShot.class, new RenderNothing());
		RenderingRegistry.registerEntityRenderingHandler(EntityProjectileGolemFireball.class, new RenderFireball(0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityProjectileDragonFireball.class, new RenderFireball(1F));
		RenderingRegistry.registerEntityRenderingHandler(EntityProjectileDragonFreezeball.class, new RenderSnowball(Items.snowball));
		RenderingRegistry.registerEntityRenderingHandler(EntityProjectileCorporealMirageOrb.class, new RenderSnowball(ItemList.corporeal_mirage_orb));
		RenderingRegistry.registerEntityRenderingHandler(EntityProjectilePotion.class, new RenderProjectilePotion());
		RenderingRegistry.registerEntityRenderingHandler(EntityProjectileSpatialDash.class, new RenderNothing());
		RenderingRegistry.registerEntityRenderingHandler(EntityProjectileCorruptedEnergy.class, new RenderNothing());
		RenderingRegistry.registerEntityRenderingHandler(EntityProjectileFiendFireball.class, new RenderProjectileFiendFireball(0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityProjectileCurse.class, new RenderProjectileCurse());
		RenderingRegistry.registerEntityRenderingHandler(EntityProjectileExpBottleConsistent.class, new RenderSnowball(ItemList.exp_bottle));

		RenderingRegistry.registerEntityRenderingHandler(EntityWeatherLightningBoltSafe.class, new RenderLightningBolt());
		RenderingRegistry.registerEntityRenderingHandler(EntityWeatherLightningBoltDemon.class, new RenderWeatherLightningBoltPurple());
		
		RenderingRegistry.registerEntityRenderingHandler(EntityTechnicalBiomeInteraction.class, new RenderNothing());
		RenderingRegistry.registerEntityRenderingHandler(EntityTechnicalVoidChest.class, new RenderNothing());
		RenderingRegistry.registerEntityRenderingHandler(EntityTechnicalPuzzleChain.class, new RenderNothing());
		RenderingRegistry.registerEntityRenderingHandler(EntityTechnicalPuzzleSolved.class, new RenderNothing());
		RenderingRegistry.registerEntityRenderingHandler(EntityTechnicalCurseBlock.class, new RenderNothing());
		RenderingRegistry.registerEntityRenderingHandler(EntityTechnicalCurseEntity.class, new RenderNothing());

		Stopwatch.finish("ModClientProxy - renderers");
		
		Baconizer.load();
	}
	
	@Override
	public void registerSidedEvents(){
		Stopwatch.time("ModClientProxy - events");
		
		OverlayManager.register();
		FXEvents.register();
		CompendiumEventsClient.register();
		MusicManager.register();
		CharmPouchHandlerClient.register();
		
		AchievementManager.THE_MORE_YOU_KNOW.setStatStringFormatter(new IStatStringFormat(){
			@Override
			public String formatString(String str){
				if (hardcoreEnderbacon)str = StatCollector.translateToLocal("achievement.theMoreYouKnow.desc.bacon");
				
				try{
					return String.format(str,GameSettings.getKeyDisplayString(CompendiumEventsClient.getCompendiumKeyCode()));
				}catch(Exception e){
					return "Error: "+e.getLocalizedMessage();
				}
			}
		});
		
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
			case TRANSPORT_BEACON_GUI:
				GuiScreen gui = Minecraft.getMinecraft().currentScreen;
				
				if (gui instanceof GuiTransportBeacon && data.length == 5){
					GuiTransportBeacon beacon = (GuiTransportBeacon)gui;
					if (beacon.centerX == data[0] && beacon.centerY == data[1] && beacon.centerZ == data[2])beacon.updateStatusEvent(data[3],data[4] == 1);
				}
				
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
