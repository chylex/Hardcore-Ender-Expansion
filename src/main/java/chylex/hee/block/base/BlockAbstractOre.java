package chylex.hee.block.base;
import net.minecraft.block.BlockOre;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public abstract class BlockAbstractOre extends BlockOre{
	protected abstract int getCausatumLevel();
	
	@Override
	public final void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta){
		super.harvestBlock(world, player, x, y, z, meta);
		// TODO CausatumUtils.increase(player, CausatumMeters.END_ORE_MINING, getCausatumLevel());
	}
}
