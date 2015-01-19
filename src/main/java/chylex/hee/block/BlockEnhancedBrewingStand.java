package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.item.ItemList;
import chylex.hee.tileentity.TileEntityEnhancedBrewingStand;

public class BlockEnhancedBrewingStand extends BlockBrewingStand{
	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityEnhancedBrewingStand();
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		player.openGui(HardcoreEnderExpansion.instance,0,world,x,y,z);
		return true;
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune){
		return ItemList.enhanced_brewing_stand;
	}
	
	@Override
	public Item getItem(World world, int x, int y, int z){
		return ItemList.enhanced_brewing_stand;
	}
}
