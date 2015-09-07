package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.BlockSkull;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import chylex.hee.init.ItemList;
import chylex.hee.tileentity.TileEntityEndermanHead;

public class BlockEndermanHead extends BlockSkull{
	public BlockEndermanHead(){}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityEndermanHead();
	}
	
	@Override
	public Item getItemDropped(int meta, Random rand, int fortune){
		return ItemList.enderman_head;
	}
	
	@Override
	public Item getItem(World world, int x, int y, int z){
		return ItemList.enderman_head;
	}
}
