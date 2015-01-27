package chylex.hee.item;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.DragonSavefile;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.system.util.ItemUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.island.biome.IslandBiomeBase;
import chylex.hee.world.structure.island.util.IslandSpawnChecker;

public class ItemBiomeCompass extends Item{
	@SideOnly(Side.CLIENT)
	public static List<Set<BlockPosM>> locations;
	@SideOnly(Side.CLIENT)
	public static byte currentBiome;
	@SideOnly(Side.CLIENT)
	private static int lastSavedX, lastSavedZ;
	
	@Override
	public void onUpdate(ItemStack is, World world, Entity entity, int slot, boolean isHeld){
		if (world.isRemote){
			if (is.hasTagCompound() && entity instanceof EntityPlayer){
				NBTTagCompound nbt = ItemUtil.getNBT(is,false);
				currentBiome = nbt.getByte("curBiome");
				
				if (lastSavedX == Integer.MAX_VALUE && lastSavedZ == Integer.MAX_VALUE){
					for(int x = entity.chunkCoordX-96, z; x <= entity.chunkCoordX+96; x++){
						for(z = entity.chunkCoordZ-96; z <= entity.chunkCoordZ+96; z++){
							byte biome = IslandSpawnChecker.getIslandBiomeAt(x,z,nbt.getLong("seed1"),nbt.getInteger("seed2"));
							if (biome != -1)locations.get(biome).add(new BlockPosM(x*16+(IslandSpawnChecker.featureSize>>1),0,z*16+(IslandSpawnChecker.featureSize>>1)));
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
			NBTTagCompound nbt = ItemUtil.getNBT(is,true);
			
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
		NBTTagCompound tag = ItemUtil.getNBT(is,true);
		
		byte biome = tag.getByte("curBiome");
		if (++biome >= IslandBiomeBase.biomeList.size())biome = 0;
		tag.setByte("curBiome",biome);
		
		return is;
	}
	
	/*@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister){
		((TextureMap)iconRegister).setTextureEntry("hardcoreenderexpansion:biome_compass",(TextureAtlasSprite)(itemIcon = new TextureBiomeCompass("hardcoreenderexpansion:biome_compass")));

		locations = new ArrayList<>(IslandBiomeBase.biomeList.size());
		for(int a = 0; a < IslandBiomeBase.biomeList.size(); a++)locations.add(a,new HashSet<ChunkCoordinates>());
		lastSavedX = lastSavedZ = Integer.MAX_VALUE;
	}*/ // TODO
}
