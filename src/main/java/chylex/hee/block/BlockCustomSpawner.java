package chylex.hee.block;
import net.minecraft.block.Block;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.tileentity.TileEntityCustomSpawner;

public class BlockCustomSpawner extends BlockMobSpawner{
	public BlockCustomSpawner(){
		super();
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
	public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta){
		super.harvestBlock(world,player,x,y,z,meta);
		
		if (meta == 0){
			KnowledgeRegistrations.DUNGEON_TOWER.tryUnlockFragment(player,0.1F,new short[]{ 0,1,2,3 });
		}
	}
}
