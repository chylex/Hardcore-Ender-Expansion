package chylex.hee.init;
import chylex.hee.entity.block.EntityBlockEnderCrystal;
import chylex.hee.entity.block.EntityBlockEnhancedTNTPrimed;
import chylex.hee.entity.block.EntityBlockFallingDragonEgg;
import chylex.hee.entity.block.EntityBlockFallingObsidian;
import chylex.hee.entity.block.EntityBlockHomelandCache;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.EntityBossEnderDemon;
import chylex.hee.entity.boss.EntityMiniBossEnderEye;
import chylex.hee.entity.boss.EntityMiniBossFireFiend;
import chylex.hee.entity.item.EntityItemAltar;
import chylex.hee.entity.item.EntityItemEndPowder;
import chylex.hee.entity.item.EntityItemIgneousRock;
import chylex.hee.entity.item.EntityItemInstabilityOrb;
import chylex.hee.entity.mob.*;
import chylex.hee.entity.projectile.*;
import chylex.hee.entity.technical.EntityTechnicalBiomeInteraction;
import chylex.hee.entity.technical.EntityTechnicalCurseBlock;
import chylex.hee.entity.technical.EntityTechnicalCurseEntity;
import chylex.hee.entity.technical.EntityTechnicalPuzzleChain;
import chylex.hee.entity.technical.EntityTechnicalPuzzleSolved;
import chylex.hee.entity.weather.EntityWeatherLightningBoltDemon;
import chylex.hee.entity.weather.EntityWeatherLightningBoltSafe;
import chylex.hee.system.util.GameRegistryUtil;

public final class EntityList{
	public static void registerEntities(){
		GameRegistryUtil.replaceVanillaEntity(EntityMobEnderman.class,58);
		GameRegistryUtil.replaceVanillaEntity(EntityBlockEnderCrystal.class,200);
		
		GameRegistryUtil.registerEntity(EntityBossDragon.class, "Dragon", 8, 320);
		GameRegistryUtil.registerEntity(EntityBossEnderDemon.class, "EnderDemon", 21, 512);
		GameRegistryUtil.registerEntity(EntityMiniBossEnderEye.class, "EnderEye", 11, 256);
		GameRegistryUtil.registerEntity(EntityMiniBossFireFiend.class, "FireFiend", 23, 256);
		GameRegistryUtil.registerEntity(EntityMobEnderman.class, "Enderman", 51, 256);
		GameRegistryUtil.registerEntity(EntityMobAngryEnderman.class, "AngryEnderman", 1, 256);
		GameRegistryUtil.registerEntity(EntityMobBabyEnderman.class, "BabyEnderman", 16, 256);
		GameRegistryUtil.registerEntity(EntityMobHomelandEnderman.class, "HomelandEnderman", 39, 256);
		GameRegistryUtil.registerEntity(EntityMobEnderGuardian.class, "EnderGuardian", 22, 256);
		GameRegistryUtil.registerEntity(EntityMobVampiricBat.class, "VampireBat", 10, 256);
		GameRegistryUtil.registerEntity(EntityMobInfestedBat.class, "InfestedBat", 12, 128);
		GameRegistryUtil.registerEntity(EntityMobForestGhost.class, "ForestGhost", 13, 32);
		GameRegistryUtil.registerEntity(EntityMobLouse.class, "Louse", 34, 256);
		GameRegistryUtil.registerEntity(EntityMobFireGolem.class, "FireGolem", 14, 256);
		GameRegistryUtil.registerEntity(EntityMobScorchingLens.class, "ScorchedLens", 15, 256);
		GameRegistryUtil.registerEntity(EntityMobHauntedMiner.class, "HauntedMiner", 35, 256);
		GameRegistryUtil.registerEntity(EntityMobEndermage.class, "Endermage", 41, 256);
		// TODO SANCTUARY GameRegistryUtil.registerEntity(EntityMobSanctuaryOverseer.class, "SanctuaryOverseer", 53, 256);

		GameRegistryUtil.registerEntity(EntityBlockFallingDragonEgg.class, "FallingDragonEgg", 25, 128);
		GameRegistryUtil.registerEntity(EntityBlockFallingObsidian.class, "FallingObsidian", 26, 128);
		GameRegistryUtil.registerEntity(EntityBlockEnhancedTNTPrimed.class, "EnhancedTNT", 31, 128);
		GameRegistryUtil.registerEntity(EntityBlockHomelandCache.class, "BlockHomelandCache", 50, 256);

		GameRegistryUtil.registerEntity(EntityItemIgneousRock.class, "ItemIgneousRock", 9, 64);
		GameRegistryUtil.registerEntity(EntityItemInstabilityOrb.class, "ItemInstabilityOrb", 6, 64);
		GameRegistryUtil.registerEntity(EntityItemAltar.class, "ItemAltar", 19, 128, 1, false);
		GameRegistryUtil.registerEntity(EntityItemEndPowder.class, "ItemEndPowder", 27, 64);
		
		GameRegistryUtil.registerEntity(EntityProjectileDragonFireball.class, "ProjectileDragonFireball", 2, 128);
		GameRegistryUtil.registerEntity(EntityProjectileEnhancedEnderPearl.class, "ProjectileEnhancedEnderPearl", 5, 128);
		GameRegistryUtil.registerEntity(EntityProjectileFlamingBall.class, "ProjectileFlamingBall", 17, 128);
		GameRegistryUtil.registerEntity(EntityProjectileGolemFireball.class, "ProjectileGolemFireball", 18, 128);
		GameRegistryUtil.registerEntity(EntityProjectileMinerShot.class, "ProjectileMinerShot", 36, 128);
		GameRegistryUtil.registerEntity(EntityProjectilePotion.class, "ProjectilePotionOfInstability", 30, 128);
		GameRegistryUtil.registerEntity(EntityProjectileSpatialDash.class, "ProjectileSpatialDash", 32, 128);
		GameRegistryUtil.registerEntity(EntityProjectileCorruptedEnergy.class, "ProjectileCorruptedEnergy", 42, 128);
		GameRegistryUtil.registerEntity(EntityProjectileFiendFireball.class, "ProjectileFiendFireball", 45, 128);
		GameRegistryUtil.registerEntity(EntityProjectileCurse.class, "ProjectileCurse", 48, 128);
		GameRegistryUtil.registerEntity(EntityProjectileExpBottleConsistent.class, "ProjectileExpBottleConsistent", 49, 128);
		GameRegistryUtil.registerEntity(EntityProjectileSacredWand.class, "ProjectileSacredWand", 52, 128);

		GameRegistryUtil.registerEntity(EntityWeatherLightningBoltSafe.class, "LightningBoltSafe", 4, 512, 1, false);
		GameRegistryUtil.registerEntity(EntityWeatherLightningBoltDemon.class, "LightningBoltDemon", 21, 512, 1, false);
		
		GameRegistryUtil.registerEntity(EntityTechnicalBiomeInteraction.class, "TechnicalBiomeInteraction", 38, 0, Integer.MAX_VALUE, false);
		GameRegistryUtil.registerEntity(EntityTechnicalPuzzleChain.class, "TechnicalPuzzleChain", 43, 0, Integer.MAX_VALUE, false);
		GameRegistryUtil.registerEntity(EntityTechnicalPuzzleSolved.class, "TechnicalPuzzleSolved", 44, 0, Integer.MAX_VALUE, false);
		GameRegistryUtil.registerEntity(EntityTechnicalCurseBlock.class, "TechnicalCurseBlock", 46, 64, 1, false);
		GameRegistryUtil.registerEntity(EntityTechnicalCurseEntity.class, "TechnicalCurseEntity", 47, 64, 1, false);
		// last: 53
	}
	
	private EntityList(){}
}
