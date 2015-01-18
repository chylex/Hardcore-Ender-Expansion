package chylex.hee.block;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.block.EntityBlockFallingDragonEgg;
import chylex.hee.entity.block.EntityBlockTempleDragonEgg;
import chylex.hee.entity.fx.FXType;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C20Effect;
import chylex.hee.packets.client.C22EffectLine;
import chylex.hee.system.achievements.AchievementManager;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.DragonSavefile;
import chylex.hee.system.util.BlockPosM;

public class BlockDragonEggCustom extends BlockDragonEgg{
	public BlockDragonEggCustom(){
		setBlockUnbreakable().setResistance(2000F).setStepSound(Block.soundTypeStone).setLightLevel(0.125F).setBlockName("dragonEgg").setBlockTextureName("dragon_egg");
	}
	
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand){
		fallIfPossible(world,pos);
	}
	
	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state){
		if (pos.getX() == 9 && pos.getZ() == 6 && pos.getY() == 249 && world.provider.getDimensionId() == 1 && !world.isRemote){
			DragonSavefile save = WorldDataHandler.get(DragonSavefile.class);
			Set<UUID> playersInTemple = save.getPlayersInTemple();
			
			if (!playersInTemple.isEmpty()){
				save.setPreventTempleDestruction(true);
				
				for(EntityPlayer player:(List<EntityPlayer>)world.playerEntities){
					if (playersInTemple.contains(player.getGameProfile().getId()))player.addStat(AchievementManager.REBIRTH,1);
				}
				
				world.spawnEntityInWorld(new EntityBlockTempleDragonEgg(world,pos.getX()+0.5D,pos.getY()+0.5D,pos.getZ()+0.5D));
			}
		}
		else super.onBlockAdded(world,pos,state);
	}

	private void fallIfPossible(World world, BlockPos pos){
		if (BlockFalling.canFallInto(world,pos.down()) && pos.getY() >= 0){
			byte checkRange = 32;
			
			if (!BlockFalling.fallInstantly && world.checkChunksExist(x-checkRange,y-checkRange,z-checkRange,x+checkRange,y+checkRange,z+checkRange)){
				world.spawnEntityInWorld(new EntityBlockFallingDragonEgg(world,x+0.5F,y+0.5F,z+0.5F));
			}
			else{
				world.setBlockToAir(pos);
				while(BlockFalling.func_149831_e(world,x,y-1,z) && y > 0)--y;
				if (y > 0)world.setBlock(x,y,z,this,0,2);
			}
		}
	}
	
	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player){
		if (player != null && player.isSneaking() && player.getHeldItem() != null &&
			player.getHeldItem().getItemUseAction() == EnumAction.BLOCK){
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
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		teleportNearby(world,pos);
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune){
		return null;
	}
	
	@Override
	public int quantityDropped(Random rand){
		return 0;
	}
	
	public static boolean teleportNearby(World world, BlockPos pos){
		if (world.getBlockState(pos).getBlock() == Blocks.dragon_egg && !world.isRemote){
			BlockPosM tpLoc = new BlockPosM();
			
			for(int attempt = 0; attempt < 1000; attempt++){
				tpLoc.x = pos.getX()+world.rand.nextInt(16)-world.rand.nextInt(16);
				tpLoc.y = pos.getY()+world.rand.nextInt(8)-world.rand.nextInt(8);
				tpLoc.z = pos.getZ()+world.rand.nextInt(16)-world.rand.nextInt(16);

				if (tpLoc.getBlockMaterial(world) == Material.air){
					world.setBlockState(tpLoc,world.getBlockState(pos));
					world.setBlockToAir(pos);
					
					PacketPipeline.sendToAllAround(world,pos,64D,new C22EffectLine(FXType.Line.DRAGON_EGG_TELEPORT,x+0.5D,y+0.5D,z+0.5D,xx+0.5D,yy+0.5D,zz+0.5D));
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static boolean teleportEntityToPortal(Entity eggEntity){
		DragonSavefile file = WorldDataHandler.get(DragonSavefile.class);
		
		if (file.isDragonDead()){
			ChunkCoordinates coords = file.getPortalEggLocation();
			World endWorld = MinecraftServer.getServer().worldServerForDimension(1);
			
			if (endWorld == null)HardcoreEnderExpansion.notifications.report("Could not teleport Dragon Egg to the End, world is null.");
			else if (endWorld.getBlock(coords.posX,coords.posY,coords.posZ) != Blocks.dragon_egg){
				endWorld.setBlock(coords.posX,coords.posY,coords.posZ,Blocks.dragon_egg);
				PacketPipeline.sendToAllAround(eggEntity,64D,new C20Effect(FXType.Basic.DRAGON_EGG_RESET,eggEntity));
				PacketPipeline.sendToAllAround(endWorld.provider.getDimensionId(),coords.posX+0.5D,coords.posY+0.5D,coords.posZ+0.5D,64D,new C20Effect(FXType.Basic.DRAGON_EGG_RESET,coords.posX+0.5D,coords.posY+0.5D,coords.posZ+0.5D));
				return true;
			}
		}
		
		return false;
	}
}