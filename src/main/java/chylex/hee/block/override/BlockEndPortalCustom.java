package chylex.hee.block.override;
import java.util.Random;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.tileentity.TileEntityEndPortalCustom;
import chylex.hee.world.util.TeleportHandler;

public class BlockEndPortalCustom extends BlockEndPortal{
	public BlockEndPortalCustom(){
		super(Material.portal);
		setBlockUnbreakable().setResistance(6000000F);
		setTickRandomly(true);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityEndPortalCustom();
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand){
		Pos pos = Pos.at(x,y,z);
		if (pos.getMetadata(world) != 15)pos.setAir(world);
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity){
		if (entity instanceof EntityPlayerMP){
			Pos pos = Pos.at(x,y,z);
			
			if (pos.getMetadata(world) != 15){
				pos.setAir(world);
				return;
			}
			
			if (entity.timeUntilPortal == 0){
				if (world.provider.dimensionId == 0)TeleportHandler.toEnd((EntityPlayerMP)entity);
				else TeleportHandler.toOverworld((EntityPlayerMP)entity);
			}
			
			entity.timeUntilPortal = 10;
		}
	}
	
	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta){
		return 15;
	}
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z){
		world.scheduleBlockUpdate(x,y,z,this,1);
	}
}
