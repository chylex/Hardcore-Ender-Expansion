package chylex.hee.item;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
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
import chylex.hee.system.logging.Log;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.DragonSavefile;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.island.biome.IslandBiomeBase;
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
							byte biome = checkBiome(x,z,is);
							if (biome != -1)locations.get(biome).add(new ChunkCoordinates(x*16+(featureSize>>1),0,z*16+(featureSize>>1)));
						}
					}
					
					lastSavedX = entity.chunkCoordX;
					lastSavedZ = entity.chunkCoordZ;
				}
				else if (MathUtil.square(entity.chunkCoordX-lastSavedX) + MathUtil.square(entity.chunkCoordZ-lastSavedZ) > 250F){
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
		
		Log.info("!!!!!!! IBC REGISTER");
	}
	
	// BIOME DETECTION

	private static final Random coordCheckRand = new Random(0L);
	private static final int minSpacing = 13, maxSpacing = 28, minDistanceFromCenter = 1600, featureSize = 208;
	
	@SideOnly(Side.CLIENT)
	private static final byte checkBiome(int chunkX, int chunkZ, ItemStack is){
		int origChunkX = chunkX, origChunkZ = chunkZ;

		if (chunkX < 0)chunkX -= maxSpacing-1;
		if (chunkZ < 0)chunkZ -= maxSpacing-1;

		int x2 = chunkX/maxSpacing, z2 = chunkZ/maxSpacing;
		
		coordCheckRand.setSeed(x2*341873128712L+z2*132897987541L+is.stackTagCompound.getLong("seed1")+358041L);
		coordCheckRand.nextInt(is.stackTagCompound.getShort("seed2"));
		
		x2 *= maxSpacing;
		z2 *= maxSpacing;
		
		x2 += coordCheckRand.nextInt(maxSpacing-minSpacing);
		z2 += coordCheckRand.nextInt(maxSpacing-minSpacing);

		if (origChunkX == x2 && origChunkZ == z2 && Math.sqrt(MathUtil.square(x2*16+(featureSize>>1))+MathUtil.square(z2*16+(featureSize>>1))) >= minDistanceFromCenter && coordCheckRand.nextInt(7) <= 4){
			int x = origChunkX*16, z = origChunkZ*16;
			coordCheckRand.setSeed(((x/9)*238504L+(z/9)*10058432215L)^is.stackTagCompound.getLong("seed1"));
			coordCheckRand.nextInt(25);
			return IslandBiomeBase.pickRandomBiome(coordCheckRand).biomeID;
		}
		else return -1;
	}
}
