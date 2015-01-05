package chylex.hee.item;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import chylex.hee.render.texture.TextureBiomeCompass;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.DragonSavefile;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.island.biome.IslandBiomeBase;
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
			if (is.stackTagCompound != null && entity instanceof EntityPlayer){
				currentBiome = is.stackTagCompound.getByte("curBiome");
				
				if (lastSavedX == Integer.MAX_VALUE && lastSavedZ == Integer.MAX_VALUE){
					for(int x = entity.chunkCoordX-96, z; x <= entity.chunkCoordX+96; x++){
						for(z = entity.chunkCoordZ-96; z <= entity.chunkCoordZ+96; z++){
							byte biome = IslandSpawnChecker.getIslandBiomeAt(x,z,is.stackTagCompound.getLong("seed1"),is.stackTagCompound.getInteger("seed2"));
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
			if (is.stackTagCompound == null)is.stackTagCompound = new NBTTagCompound();
			
			if (!is.stackTagCompound.hasKey("seed1")){
				is.stackTagCompound.setLong("seed1",world.getSeed());
				is.stackTagCompound.setShort("seed2",(short)(1+WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).getDragonDeathAmount()));
			}
			else if (isHeld && entity.dimension == 1 && entity.ticksExisted%100 == 0){
				int seed2 = 1+WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).getDragonDeathAmount();
				if (seed2 != is.stackTagCompound.getShort("seed2"))is.stackTagCompound.setShort("seed2",(short)seed2);
			}
		}
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		if (is.stackTagCompound == null)is.stackTagCompound = new NBTTagCompound();
		
		byte biome = is.stackTagCompound.getByte("curBiome");
		if (++biome >= IslandBiomeBase.biomeList.size())biome = 0;
		is.stackTagCompound.setByte("curBiome",biome);
		
		return is;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister){
		((TextureMap)iconRegister).setTextureEntry("hardcoreenderexpansion:biome_compass",(TextureAtlasSprite)(itemIcon = new TextureBiomeCompass("hardcoreenderexpansion:biome_compass")));

		locations = new ArrayList<>(IslandBiomeBase.biomeList.size());
		for(int a = 0; a < IslandBiomeBase.biomeList.size(); a++)locations.add(a,new HashSet<ChunkCoordinates>());
		lastSavedX = lastSavedZ = Integer.MAX_VALUE;
	}
}
