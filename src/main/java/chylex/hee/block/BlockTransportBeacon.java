package chylex.hee.block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
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
		if (world.isRemote)player.openGui(HardcoreEnderExpansion.instance,8,world,pos.getX(),pos.getY(),pos.getZ());
		else{
			PlayerTransportBeacons data = PlayerTransportBeacons.getInstance(player);
			TileEntityTransportBeacon tile = (TileEntityTransportBeacon)world.getTileEntity(pos);
			
			if (tile.hasNotBeenTampered())data.addBeacon(pos.getX(),pos.getZ());
			PacketPipeline.sendToPlayer(player,new C13TransportBeaconData(data.getOffsets(pos.getX(),pos.getZ()),tile.hasEnergy(),tile.hasNotBeenTampered()));
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
	public EnumWorldBlockLayer getBlockLayer(){
		return EnumWorldBlockLayer.TRANSLUCENT;
	}
}