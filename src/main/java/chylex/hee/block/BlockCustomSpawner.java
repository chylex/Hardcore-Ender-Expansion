package chylex.hee.block;
import net.minecraft.block.Block;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import chylex.hee.tileentity.TileEntityCustomSpawner;

public class BlockCustomSpawner extends BlockMobSpawner{
	public static final int metaTowerEnderman = 0,
							metaSilverfishDungeon = 1,
							metaRavagedLouse = 2,
							metaRavagedSilverfish = 3,
							metaBlobEnderman = 4;
	
	public BlockCustomSpawner(){
		disableStats();
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata){
		return new TileEntityCustomSpawner().setLogicId(metadata);
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldBlock, int oldMeta){
		if (!world.isRemote){
			TileEntityCustomSpawner spawner = (TileEntityCustomSpawner)world.getTileEntity(x,y,z);
			if (spawner != null)spawner.getSpawnerLogic().onBlockBreak();
		}
		
		super.breakBlock(world,x,y,z,oldBlock,oldMeta);
	}
	
	@Override
	public final void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta){
		super.harvestBlock(world,player,x,y,z,meta);
		int causatum = -1;
		
		switch(meta){
			case BlockCustomSpawner.metaTowerEnderman: causatum = 10; break;
			case BlockCustomSpawner.metaRavagedLouse: causatum = 20; break;
			case BlockCustomSpawner.metaRavagedSilverfish: causatum = 12; break;
			case BlockCustomSpawner.metaBlobEnderman: causatum = 25; break;
		}
		
		// TODO if (causatum != -1)CausatumUtils.increase(player,CausatumMeters.END_SPAWNER_MINING,causatum);
	}
}
