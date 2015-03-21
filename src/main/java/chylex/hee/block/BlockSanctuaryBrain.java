package chylex.hee.block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import chylex.hee.tileentity.TileEntitySanctuaryBrain;

public class BlockSanctuaryBrain extends BlockContainer{
	public BlockSanctuaryBrain(){
		super(Material.rock);
		disableStats();
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntitySanctuaryBrain();
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player){
		return null;
	}
}
