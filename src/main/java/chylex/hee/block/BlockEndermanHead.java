package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.BlockSkull;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import chylex.hee.init.ItemList;
import chylex.hee.tileentity.TileEntityEndermanHead;
import cpw.mods.fml.common.Optional;
import thaumcraft.api.crafting.IInfusionStabiliser;

@Optional.Interface(iface = "thaumcraft.api.crafting.IInfusionStabiliser", modid = "Thaumcraft")
public class BlockEndermanHead extends BlockSkull implements IInfusionStabiliser{
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

	@Override
	public boolean canStabaliseInfusion(World world, int x, int y, int z){ // TC API
		return true;
	}
}
