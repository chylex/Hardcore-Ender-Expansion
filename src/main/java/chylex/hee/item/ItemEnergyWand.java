package chylex.hee.item;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.block.BlockList;
import chylex.hee.system.achievements.AchievementManager;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.ItemUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityEnergyCluster;

public class ItemEnergyWand extends Item{
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ){
		if (player.isSneaking() && !world.isRemote){
			NBTTagCompound nbt = ItemUtil.getNBT(is,false);
			
			if (nbt.hasKey("cluster")){
				pos = pos.offset(side);
				if (!player.canPlayerEdit(pos,side,is) || !world.isAirBlock(pos))return false;
				
				world.setBlockState(pos,BlockList.energy_cluster.getDefaultState());
				TileEntityEnergyCluster tile = (TileEntityEnergyCluster)world.getTileEntity(pos);
				
				if (tile != null){
					NBTTagCompound tag = nbt.getCompoundTag("cluster");
					tag.setIntArray("loc",new int[]{ pos.getX(), pos.getY(), pos.getZ() });
					tile.readTileFromNBT(tag);
					
					int[] prevLoc = nbt.getIntArray("prevLoc");
					double dist = nbt.getShort("prevDim") == world.provider.getDimensionId() ? MathUtil.distance(prevLoc[0]-pos.getX(),prevLoc[1]-pos.getY(),prevLoc[2]-pos.getZ()) : Double.MAX_VALUE;
					
					if (dist > 8D){
						tile.data.setEnergyLevel(tile.data.getEnergyLevel()*(1F-0.5F*Math.min(1F,(float)dist/256F)));
						if (itemRand.nextInt(100) < tile.data.getHealthStatus().chanceToWeaken)tile.data.weakenCluster();
					}
					
					tile.synchronize();
				}
				
				nbt.removeTag("cluster");
				return true;
			}
			else if (world.getBlockState(pos).getBlock() == BlockList.energy_cluster){
				TileEntityEnergyCluster tile = (TileEntityEnergyCluster)world.getTileEntity(pos);
				
				if (tile != null){
					tile.shouldNotExplode = true;
					
					if (!world.isRemote){
						NBTTagCompound tag = tile.writeTileToNBT(new NBTTagCompound());
						tag.setIntArray("loc",ArrayUtils.EMPTY_INT_ARRAY);
						
						nbt.setTag("cluster",tag);
						nbt.setIntArray("prevLoc",new int[]{ pos.getX(), pos.getY(), pos.getZ() });
						nbt.setShort("prevDim",(short)world.provider.getDimensionId());
						
						world.setBlockToAir(pos);
					}
				}
				
				return true;
			}
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
		if (!ItemUtil.getNBT(is,false).hasKey("cluster"))return;
		textLines.add("Holding cluster with "+DragonUtil.formatTwoPlaces.format(ItemUtil.getNBT(is,false).getCompoundTag("cluster").getShort("lvl"))+" Energy");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack is){
		return ItemUtil.getNBT(is,false).hasKey("cluster");
	}
}
