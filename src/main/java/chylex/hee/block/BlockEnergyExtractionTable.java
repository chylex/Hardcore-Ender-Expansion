package chylex.hee.block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.tileentity.TileEntityEnergyExtractionTable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEnergyExtractionTable extends BlockAbstractInventory{
	@SideOnly(Side.CLIENT)
	private IIcon iconTop, iconSide, iconBottom;
	
	public BlockEnergyExtractionTable(){
		super(Material.rock);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityEnergyExtractionTable();
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		player.openGui(HardcoreEnderExpansion.instance,3,world,x,y,z);
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return side == 0 ? iconBottom : side == 1 ? iconTop : iconSide;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		iconTop = iconRegister.registerIcon("hardcoreenderexpansion:energy_extraction_table_top");
		iconSide = iconRegister.registerIcon("hardcoreenderexpansion:energy_extraction_table_side");
		iconBottom = iconRegister.registerIcon("hardcoreenderexpansion:energy_extraction_table_bottom");
	}
}
