package chylex.hee.item;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import chylex.hee.entity.boss.EntityMiniBossEnderEye;
import chylex.hee.entity.boss.EntityMiniBossFireFiend;
import chylex.hee.entity.mob.EntityMobBabyEnderman;
import chylex.hee.entity.mob.EntityMobEnderGuardian;
import chylex.hee.entity.mob.EntityMobEndermage;
import chylex.hee.entity.mob.EntityMobFireGolem;
import chylex.hee.entity.mob.EntityMobHauntedMiner;
import chylex.hee.entity.mob.EntityMobInfestedBat;
import chylex.hee.entity.mob.EntityMobLouse;
import chylex.hee.entity.mob.EntityMobScorchingLens;
import chylex.hee.entity.mob.EntityMobVampiricBat;
import chylex.hee.system.abstractions.Pos;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSpawnEggs extends ItemMonsterPlacer{
	private static final EggData[] eggTypes = new EggData[]{
		/*  0 */ EggData.unknown, // removed mob
		/*  1 */ new EggData("babyEnderman", EntityMobBabyEnderman.class, new int[]{ 22,22,22 }, new int[]{ 91,67,124 }),
		/*  2 */ new EggData("enderGuardian", EntityMobEnderGuardian.class, new int[]{ 22,22,22 }, new int[]{ 68,45,87 }),
		/*  3 */ new EggData("vampireBat", EntityMobVampiricBat.class, new int[]{ 76,62,48 }, new int[]{ 70,35,71 }),
		/*  4 */ new EggData("infestedBat", EntityMobInfestedBat.class, new int[]{ 76,62,48 }, new int[]{ 129,48,39 }),
		/*  5 */ new EggData("fireGolem", EntityMobFireGolem.class, new int[]{ 68,16,0 }, new int[]{ 210,142,50 }),
		/*  6 */ new EggData("scorchingLens", EntityMobScorchingLens.class, new int[]{ 255,112,0 }, new int[]{ 253,9,8 }),
		/*  7 */ new EggData("enderEye", EntityMiniBossEnderEye.class, new int[]{ 22,22,22 }, new int[]{ 255,255,255 }),
		/*  8 */ new EggData("fireFiend", EntityMiniBossFireFiend.class, new int[]{ 68,16,0 }, new int[]{ 33,0,0 }),
		/*  9 */ EggData.unknown, // removed mob
		/* 10 */ EggData.unknown, // removed mob
		/* 11 */ new EggData("louse", EntityMobLouse.class, new int[]{ 45,45,45 }, new int[]{ 80,0,140 }),
		/* 12 */ new EggData("hauntedMiner", EntityMobHauntedMiner.class, new int[]{ 48,23,23 }, new int[]{ 170,72,37 }),
		/* 13 */ EggData.unknown, // removed mob
		/* 14 */ new EggData("endermage", EntityMobEndermage.class, new int[]{ 22,22,22 }, new int[]{ 217,210,84 })
	};
	
	private static EggData getEggData(ItemStack is){
		int damage = is.getItemDamage();
		return damage >= 0 && damage < eggTypes.length ? eggTypes[damage] : EggData.unknown;
	}
	
	public static int getDamageForMob(Class<? extends EntityLiving> mobClass){
		for(int damage = 0; damage < eggTypes.length; damage++){
			if (eggTypes[damage].entityClass == mobClass)return damage;
		}
		
		return -1;
	}
	
	public static Class<? extends EntityLiving> getMobFromDamage(int damage){
		return damage >= 0 && damage < eggTypes.length ? eggTypes[damage].entityClass : null;
	}
	
	public static String getMobName(final Class<?> mobClass){
		for(EggData eggData:eggTypes){
			if (eggData.entityClass == mobClass)return StatCollector.translateToLocal("entity."+eggData.entityName+".name");
		}
		
		return "Unknown Mob";
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack is){
		return StatCollector.translateToLocal(getUnlocalizedName()+".name").trim()+" "+getMobName(getMobFromDamage(is.getItemDamage()));
	}
	
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		if (world.isRemote)return true;
		
		Pos pos = Pos.at(x,y,z).offset(EnumFacing.values()[side]);
		float yOffset = (side == 1 && pos.getBlock(world).getRenderType() == 11) ? 0.5F : 0F;
		
		if (getEggData(is).spawnMob(world,pos.getX()+0.5D,pos.getY()+yOffset,pos.getZ()+0.5D,is) != null && !player.capabilities.isCreativeMode)--is.stackSize;

		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		if (world.isRemote)return is;

		MovingObjectPosition mop = getMovingObjectPositionFromPlayer(world,player,true);
		
		if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK){
			int x = mop.blockX, y = mop.blockY, z = mop.blockZ;
			
			if (world.canMineBlock(player,x,y,z) && player.canPlayerEdit(x,y,z,mop.sideHit,is) && Pos.at(x,y,z).getMaterial(world) == Material.water){
				if (getEggData(is).spawnMob(world,x,y,z,is) != null && !player.capabilities.isCreativeMode)--is.stackSize;
			}
		}

		return is;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack is, int pass){
		EggData egg = getEggData(is);
		return pass == 0 ? egg.primaryColor : egg.secondaryColor;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list){
		for(int a = 0; a < eggTypes.length; a++){
			if (eggTypes[a] != EggData.unknown)list.add(new ItemStack(item,1,a));
		}
	}

	static class EggData{
		static EggData unknown = new EggData(null,null,new int[]{ 255, 255, 255 },new int[]{ 255, 255, 255 });
		
		final String entityName;
		final Class<? extends EntityLiving> entityClass;
		final int primaryColor,secondaryColor;
		
		EggData(String entityName, Class<? extends EntityLiving> entityClass, int[] rgbPrimaryColor, int[] rgbSecondaryColor){
			this.entityName = entityName;
			this.entityClass = entityClass;
			this.primaryColor = (rgbPrimaryColor[0]<<16)|(rgbPrimaryColor[1]<<8)|rgbPrimaryColor[2];
			this.secondaryColor = (rgbSecondaryColor[0]<<16)|(rgbSecondaryColor[1]<<8)|rgbSecondaryColor[2];
		}
		
		public EntityLiving spawnMob(World world, double x, double y, double z, ItemStack is){
			EntityLiving e = null;
			
			try{
				e = entityClass.getConstructor(World.class).newInstance(world);
				if (e == null)return null;
			}catch(NoSuchMethodError | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex){
				ex.printStackTrace();
				return null;
			}
			
			e.setLocationAndAngles(x,y,z,itemRand.nextFloat()*360F-180F,0F);
			e.rotationYawHead = e.renderYawOffset = e.rotationYaw;
			e.onSpawnWithEgg((IEntityLivingData)null);
			world.spawnEntityInWorld(e);
			e.playLivingSound();
			
			if (is.hasDisplayName())e.setCustomNameTag(is.getDisplayName());
			return e;
		}
	}
}
