package chylex.hee.item;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.init.BlockList;
import chylex.hee.mechanics.energy.EnergyClusterData;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.ItemUtil;
import chylex.hee.tileentity.TileEntityEnergyCluster;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemEnergyReceptacle extends Item{
	private static final float updateEnergyLevel(float lvl, float limit, int cycles){
		float lossPerCycle = (float)Math.pow(limit,0.001D)-1F;
		return Math.max(lvl-lossPerCycle*cycles,0F);
	}
	
	@Override
	public void onUpdate(ItemStack is, World world, Entity entity, int slot, boolean isHeld){
		if (!world.isRemote && ItemUtil.getTagRoot(is,false).hasKey("cluster")){
			NBTTagCompound nbt = ItemUtil.getTagRoot(is,true);
			long prevTime = nbt.getLong("ltime"), currentTime = world.getTotalWorldTime();
			
			if (currentTime-prevTime >= 10){
				NBTTagCompound clusterTag = nbt.getCompoundTag("cluster");
				float lvl = clusterTag.getFloat("lvl");
				float limit = clusterTag.getFloat("max");
				
				clusterTag.setFloat("lvl",updateEnergyLevel(lvl,limit,1));
				nbt.setLong("ltime",currentTime);
			}
		}
	}
	
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		if (!world.isRemote){
			Pos pos = Pos.at(x,y,z);
			
			if (ItemUtil.getTagRoot(is,false).hasKey("cluster")){
				if (side > 0)pos = pos.offset(side);
				if (!player.canPlayerEdit(pos.getX(),pos.getY(),pos.getZ(),side,is) || !pos.isAir(world))return false;
				
				NBTTagCompound nbt = ItemUtil.getTagRoot(is,false);
				pos.setBlock(world,BlockList.energy_cluster);
				
				TileEntityEnergyCluster tile = pos.getTileEntity(world);
				tile.readTileFromNBT(nbt);
				
				EnergyClusterData data = tile.getData().get();
				if (data.getEnergyLevel()-nbt.getFloat("origlvl") >= Math.pow(data.getMaxLevel(),0.015D)+0.01D)data.weaken();
				
				tile.synchronize();
				
				nbt.removeTag("cluster");
				nbt.removeTag("ltime");
				nbt.removeTag("origlvl");
			}
			else if (pos.getBlock(world) == BlockList.energy_cluster){
				NBTTagCompound nbt = ItemUtil.getTagRoot(is,true);
				NBTTagCompound clusterTag = new NBTTagCompound();
				TileEntityEnergyCluster cluster = pos.getTileEntity(world);
				
				cluster.writeTileToNBT(clusterTag);
				cluster.shouldNotExplode = true;
				pos.breakBlock(world,false);
				
				nbt.setTag("cluster",clusterTag);
				nbt.setFloat("origlvl",clusterTag.getFloat("lvl"));
				nbt.setLong("ltime",world.getTotalWorldTime());
			}
			
			return true;
		}

		//if (player.isSneaking() && !world.isRemote){
			/* TODO if (ItemUtil.getTagRoot(is,false).hasKey("cluster")){
				BlockPosM tmpPos = BlockPosM.tmp(x,y,z);
				if (side > 0)tmpPos.move(side);
	
				if (!player.canPlayerEdit(tmpPos.x,tmpPos.y,tmpPos.z,side,is) || !tmpPos.isAir(world))return false;
				
				tmpPos.setBlock(world,BlockList.energy_cluster);
				TileEntityEnergyCluster tile = (TileEntityEnergyCluster)tmpPos.getTileEntity(world);
				
				if (tile != null){
					NBTTagCompound tag = ItemUtil.getTagSub(is,"cluster",true);
					tag.setLong("loc",tmpPos.toLong());
					tile.readTileFromNBT(tag);
					
					Pos prevLoc = Pos.at(ItemUtil.getTagRoot(is,false).getLong("prevLoc"));
					double dist = ItemUtil.getTagRoot(is,false).getShort("prevDim") == world.provider.dimensionId ? MathUtil.distance(prevLoc.getX()-tmpPos.x,prevLoc.getY()-tmpPos.y,prevLoc.getZ()-tmpPos.z) : Double.MAX_VALUE;
					
					if (dist > 8D){
						tile.data.setEnergyLevel(tile.data.getEnergyLevel()*(1F-0.5F*Math.min(1F,(float)dist/256F)));
						if (itemRand.nextInt(100) < tile.data.getHealthStatus().chanceToWeaken)tile.data.weakenCluster();
					}
					
					tile.synchronize();
				}
				
				ItemUtil.getTagRoot(is,false).removeTag("cluster");
				return true;
			}
			else if (BlockPosM.tmp(x,y,z).getBlock(world) == BlockList.energy_cluster){
				TileEntityEnergyCluster tile = (TileEntityEnergyCluster)BlockPosM.tmp(x,y,z).getTileEntity(world);
				
				if (tile != null){
					tile.shouldNotExplode = true;
					
					if (!world.isRemote){
						CausatumUtils.increase(player,CausatumMeters.END_ENERGY,tile.data.getEnergyLevel()*0.5F);
						
						NBTTagCompound tag = tile.writeTileToNBT(new NBTTagCompound());
						tag.setLong("loc",BlockPosM.tmp(0,-1,0).toLong());
						
						NBTTagCompound itemNbt = ItemUtil.getTagRoot(is,true);
						itemNbt.setTag("cluster",tag);
						itemNbt.setLong("prevLoc",BlockPosM.tmp(x,y,z).toLong());
						itemNbt.setShort("prevDim",(short)world.provider.dimensionId);
						
						BlockPosM.tmp(x,y,z).setAir(world);
					}
				}
				
				return true;
			}*/
		//}
		
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		NBTTagCompound nbt = ItemUtil.getTagRoot(is,false);
		
		if (nbt.hasKey("cluster")){
			float lvl = nbt.getCompoundTag("cluster").getFloat("lvl");
			float limit = nbt.getCompoundTag("cluster").getFloat("max");
			long diff = player.worldObj.getTotalWorldTime()-nbt.getLong("ltime");
			textLines.add(I18n.format("item.energyReceptacle.holding").replace("$",DragonUtil.formatTwoPlaces.format(updateEnergyLevel(lvl,limit,(int)(diff/10)))));
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack is, int pass){
		return ItemUtil.getTagRoot(is,false).hasKey("cluster");
	}
}
