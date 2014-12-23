package chylex.hee.block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.tileentity.TileEntityTransportBeacon;

public class BlockTransportBeacon extends BlockContainer{
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
	public int getRenderType(){
		return ModCommonProxy.renderIdTransportBeacon;
	}
}
