package chylex.hee.block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.tileentity.TileEntityTransportBeacon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockTransportBeacon extends BlockContainer{
	@SideOnly(Side.CLIENT)
	public IIcon iconOutside;
	
	public BlockTransportBeacon(){
		super(Material.glass);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityTransportBeacon();
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		return false;
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass(){
		return 1;
	}

	@Override
	public int getRenderType(){
		return ModCommonProxy.renderIdTransportBeacon;
	}
	
	@Override
	public void registerBlockIcons(IIconRegister iconRegister){
		super.registerBlockIcons(iconRegister);
		iconOutside = iconRegister.registerIcon(textureName+"_outside");
	}
}