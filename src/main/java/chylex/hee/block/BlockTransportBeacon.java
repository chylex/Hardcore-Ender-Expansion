package chylex.hee.block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.mechanics.misc.PlayerTransportBeacons;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C13TransportBeaconData;
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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
		if (world.isRemote)player.openGui(HardcoreEnderExpansion.instance,8,world,x,y,z);
		else{
			PlayerTransportBeacons data = PlayerTransportBeacons.getInstance(player);
			TileEntityTransportBeacon tile = (TileEntityTransportBeacon)world.getTileEntity(x,y,z);
			
			if (tile.hasNotBeenTampered())data.addBeacon(x,z);
			PacketPipeline.sendToPlayer(player,new C13TransportBeaconData(data.getOffsets(x,z),tile.hasEnergy(),tile.hasNotBeenTampered()));
		}
		
		return true;
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	@Override
	public boolean isFullCube(){
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass(){
		return 1;
	}
}