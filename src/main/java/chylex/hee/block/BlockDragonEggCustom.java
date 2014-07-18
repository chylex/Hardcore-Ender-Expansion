package chylex.hee.block;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.entity.block.EntityBlockFallingDragonEgg;
import chylex.hee.entity.block.EntityBlockTempleDragonEgg;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.knowledge.util.ObservationUtil;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C20ParticleDragonEggTeleportation;
import chylex.hee.system.achievements.AchievementManager;
import chylex.hee.system.savedata.ServerSavefile;
import chylex.hee.world.biome.BiomeDecoratorHardcoreEnd;

public class BlockDragonEggCustom extends BlockDragonEgg{
	public BlockDragonEggCustom(){
		super();
		setHardness(1000F).setResistance(2000F).setStepSound(Block.soundTypeStone).setLightLevel(0.125F).setBlockName("dragonEgg").setBlockTextureName("dragon_egg");
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand){
        fallIfPossible(world,x,y,z);
    }
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z){
		if (x == 9 && z == 6 && y == 249 && world.provider.dimensionId == 1 && !world.isRemote){
			ServerSavefile save = BiomeDecoratorHardcoreEnd.getCache(world);
			List<String> playersInTemple = save.getPlayersInTemple();
			if (playersInTemple.size() > 0){
				save.setPreventTempleDestruction(true);
				for(Object o:world.playerEntities){
					EntityPlayer player = (EntityPlayer)o;
					if (playersInTemple.contains(player.getCommandSenderName()))player.addStat(AchievementManager.REBIRTH,1);
				}
				
				world.spawnEntityInWorld(new EntityBlockTempleDragonEgg(world,x+0.5D,y+0.5D,z+0.5D));
			}
		}
		else super.onBlockAdded(world,x,y,z);
	}

	private void fallIfPossible(World world, int x, int y, int z){
		if (BlockFalling.func_149831_e(world,x,y-1,z) && y >= 0){ // OBFUSCATED can fall?
			byte checkRange = 32;
			
			if (!BlockFalling.fallInstantly && world.checkChunksExist(x-checkRange,y-checkRange,z-checkRange,x+checkRange,y+checkRange,z+checkRange)){
				world.spawnEntityInWorld(new EntityBlockFallingDragonEgg(world,x+0.5F,y+0.5F,z+0.5F));
			}
			else{
				world.setBlockToAir(x,y,z);
				while(BlockFalling.func_149831_e(world,x,y-1,z) && y > 0)--y;
				if (y > 0)world.setBlock(x,y,z,this,0,2);
			}
		}
	}
	
	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player){
    	if (player != null && player.isSneaking() && player.getHeldItem() != null &&
    		player.getHeldItem().getItemUseAction() == EnumAction.block){
    		world.setBlockToAir(x,y,z);
    		dropBlockAsItem(world,x,y,z,new ItemStack(Blocks.dragon_egg));
    	}
		else teleportNearby(world,x,y,z);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		teleportNearby(world,x,y,z);
		return true;
	}
	
	@Override
	public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int fortune){
		teleportNearby(world,x,y,z);
	}
	
	// COPIED FROM BlockDragonEgg, tweaked (a lot)
	private void teleportNearby(World world, int x, int y, int z){
		if (world.getBlock(x,y,z) == this && !world.isRemote){
			for(int attempt = 0,xx,yy,zz; attempt < 1000; ++attempt){
				xx = x+world.rand.nextInt(16)-world.rand.nextInt(16);
				yy = y+world.rand.nextInt(8)-world.rand.nextInt(8);
				zz = z+world.rand.nextInt(16)-world.rand.nextInt(16);

				if (world.getBlock(xx,yy,zz).getMaterial() == Material.air){
					world.setBlock(xx,yy,zz,this,world.getBlockMetadata(x,y,z),2);
					world.setBlockToAir(x,y,z);
					
					PacketPipeline.sendToAllAround(world.provider.dimensionId,x,y,z,76D,new C20ParticleDragonEggTeleportation(x,y,z,xx,yy,zz));
					
					for(EntityPlayer observer:ObservationUtil.getAllObservers(world,x+0.5D,y+0.5D,z+0.5D,12D))KnowledgeRegistrations.ENDER_DRAGON.tryUnlockFragment(observer,0.25F,new short[]{ 16,17 });

					return;
				}
			}
		}
	}
}