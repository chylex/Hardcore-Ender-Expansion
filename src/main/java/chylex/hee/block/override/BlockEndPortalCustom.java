package chylex.hee.block.override;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import chylex.hee.tileentity.TileEntityEndPortalCustom;
import chylex.hee.world.util.TeleportHandler;

public class BlockEndPortalCustom extends BlockEndPortal{
	public BlockEndPortalCustom(){
		super(Material.portal);
		setBlockUnbreakable().setResistance(6000000F);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityEndPortalCustom();
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity){
		// TODO if not meta 15, disable
		if (entity instanceof EntityPlayerMP){
			if (entity.timeUntilPortal == 0){
				if (world.provider.dimensionId == 0)TeleportHandler.toEnd((EntityPlayerMP)entity);
				else TeleportHandler.toOverworld((EntityPlayerMP)entity);
			}
			
			entity.timeUntilPortal = 10;
		}
	}
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z){}
}
