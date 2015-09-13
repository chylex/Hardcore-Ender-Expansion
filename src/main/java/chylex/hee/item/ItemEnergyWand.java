package chylex.hee.item;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.system.achievements.AchievementManager;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.ItemUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemEnergyWand extends Item{
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		if (player.isSneaking() && !world.isRemote){
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
		}
		
		return false;
	}
	
	@Override
	public void onCreated(ItemStack is, World world, EntityPlayer player){
		player.addStat(AchievementManager.ENERGY_WAND,1);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		if (ItemUtil.getTagRoot(is,false).hasKey("cluster"))textLines.add(I18n.format("item.energyWand.info.holding").replace("$",DragonUtil.formatTwoPlaces.format(ItemUtil.getTagSub(is,"cluster",false).getFloat("lvl"))));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack is, int pass){
		return ItemUtil.getTagRoot(is,false).hasKey("cluster");
	}
}
