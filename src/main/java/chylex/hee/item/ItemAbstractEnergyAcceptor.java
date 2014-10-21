package chylex.hee.item;
import chylex.hee.block.BlockList;
import chylex.hee.tileentity.TileEntityEnergyCluster;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class ItemAbstractEnergyAcceptor extends Item{
	protected abstract boolean canAcceptEnergy(ItemStack is);
	protected abstract void onEnergyAccepted(ItemStack is);
	
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		if (world.getBlock(x,y,z) == BlockList.energy_cluster && canAcceptEnergy(is)){
			TileEntityEnergyCluster tile = (TileEntityEnergyCluster)world.getTileEntity(x,y,z);

			if (tile != null && tile.data.drainEnergyUnit()){
				tile.onAbsorbed(player,is);
				if (!world.isRemote)onEnergyAccepted(is);
			}
			
			return true;
		}
		else return false;
	}
}
