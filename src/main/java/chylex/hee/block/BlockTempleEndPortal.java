package chylex.hee.block;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import chylex.hee.mechanics.temple.TempleEvents;
import chylex.hee.system.util.DragonUtil;

public class BlockTempleEndPortal extends BlockEndPortal{
	protected BlockTempleEndPortal(){
		super(Material.portal);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity){
		if (world.getBlockMetadata(x,y,z) == 1)return;
		
		if (!world.isRemote && entity.ridingEntity == null && entity.riddenByEntity == null && entity instanceof EntityPlayerMP){
			EntityPlayerMP player = (EntityPlayerMP)entity;
			TempleEvents.attemptDestroyTemple(player);
			DragonUtil.teleportToOverworld(player);
		}
	}
}
