package chylex.hee.block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.mechanics.misc.PlayerTransportBeacons;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C13TransportBeaconData;
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