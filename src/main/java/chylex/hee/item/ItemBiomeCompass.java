package chylex.hee.item;
import java.util.List;
import java.util.Set;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import chylex.hee.render.texture.TextureBiomeCompass;
import chylex.hee.system.achievements.AchievementManager;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.DragonSavefile;
import chylex.hee.system.util.ItemUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.island.util.IslandSpawnChecker;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBiomeCompass extends Item{
	@SideOnly(Side.CLIENT)
	public static List<Set<ChunkCoordinates>> locations;
	@SideOnly(Side.CLIENT)
	public static byte currentBiome;
	@SideOnly(Side.CLIENT)
	private static int lastSavedX, lastSavedZ;
	
	@Override
	public void onUpdate(ItemStack is, World world, Entity entity, int slot, boolean isHeld){
		if (world.isRemote){
			if (is.hasTagCompound() && entity instanceof EntityPlayer){
				currentBiome = is.getTagCompound().getByte("curBiome");
				
				if (lastSavedX == Integer.MAX_VALUE && lastSavedZ == Integer.MAX_VALUE){
					long seed1 = is.getTagCompound().getLong("seed1");
					int seed2 = is.getTagCompound().getInteger("seed2");
					
					for(int x = entity.chunkCoordX-96, z; x <= entity.chunkCoordX+96; x++){
						for(z = entity.chunkCoordZ-96; z <= entity.chunkCoordZ+96; z++){
							byte biome = IslandSpawnChecker.getIslandBiomeAt(x,z,seed1,seed2);
							if (biome != -1)locations.get(biome).add(new ChunkCoordinates(x*16+(IslandSpawnChecker.featureSize>>1),0,z*16+(IslandSpawnChecker.featureSize>>1)));
						}
					}
					
					lastSavedX = entity.chunkCoordX;
					lastSavedZ = entity.chunkCoordZ;
				}
				else if (MathUtil.square(entity.chunkCoordX-lastSavedX)+MathUtil.square(entity.chunkCoordZ-lastSavedZ) > 250F){
					lastSavedX = lastSavedZ = Integer.MAX_VALUE;
				}
			}
		}
		else{
			if (entity instanceof EntityPlayerMP){
				EntityPlayerMP player = (EntityPlayerMP)entity;
				if (!player.func_147099_x().hasAchievementUnlocked(AchievementManager.BIOME_COMPASS))player.addStat(AchievementManager.BIOME_COMPASS,1); // OBFUSCATED getStatisticsFile
			}
			
			NBTTagCompound nbt = ItemUtil.getTagRoot(is,true);
			
			if (!nbt.hasKey("seed1")){
				nbt.setLong("seed1",world.getSeed());
				nbt.setShort("seed2",(short)(1+WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).getDragonDeathAmount()));
			}
			else if (isHeld && entity.dimension == 1 && entity.ticksExisted%100 == 0){
				int seed2 = 1+WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).getDragonDeathAmount();
				if (seed2 != nbt.getShort("seed2"))nbt.setShort("seed2",(short)seed2);
			}
		}
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		byte biome = ItemUtil.getTagRoot(is,true).getByte("curBiome");
		/* TODO if (++biome >= IslandBiomeBase.biomeList.size())*/biome = 0;
		
		is.getTagCompound().setByte("curBiome",biome);
		return is;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister){
		((TextureMap)iconRegister).setTextureEntry("hardcoreenderexpansion:biome_compass",(TextureAtlasSprite)(itemIcon = new TextureBiomeCompass("hardcoreenderexpansion:biome_compass")));
		
		/* TODO locations = new ArrayList<>(IslandBiomeBase.biomeList.size());
		for(int a = 0; a < IslandBiomeBase.biomeList.size(); a++)locations.add(a,new HashSet<ChunkCoordinates>());*/
		lastSavedX = lastSavedZ = Integer.MAX_VALUE;
	}
}
