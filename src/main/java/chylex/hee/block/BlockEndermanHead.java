package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.BlockSkull;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thaumcraft.api.crafting.IInfusionStabiliser;
import chylex.hee.tileentity.TileEntityEndermanHead;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Optional.Interface(iface = "thaumcraft.api.crafting.IInfusionStabiliser", modid = "Thaumcraft")
public class BlockEndermanHead extends BlockSkull implements IInfusionStabiliser{
	public BlockEndermanHead(){}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityEndermanHead();
	}
	
	@Override
	public Item getItemDropped(int meta, Random rand, int fortune){
		return Item.getItemFromBlock(this);
	}
	
	@Override
    @SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z){
		return Item.getItemFromBlock(this);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getItemIconName(){
		return getTextureName();
	}
	
	@Override
	public boolean canStabaliseInfusion(World world, int x, int y, int z){ // TC API
		return true;
	}
}
