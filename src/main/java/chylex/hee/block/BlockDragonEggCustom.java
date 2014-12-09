package chylex.hee.block;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
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

public class BlockDragonEggCustom extends BlockDragonEgg{
	public BlockDragonEggCustom(){
		setBlockUnbreakable().setResistance(2000F).setStepSound(Block.soundTypeStone).setLightLevel(0.125F).setBlockName("dragonEgg").setBlockTextureName("dragon_egg");
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand){
		fallIfPossible(world,x,y,z);
	}
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z){
		if (x == 9 && z == 6 && y == 249 && world.provider.dimensionId == 1 && !world.isRemote){
			DragonSavefile save = WorldDataHandler.get(DragonSavefile.class);
			Set<UUID> playersInTemple = save.getPlayersInTemple();
			
			if (!playersInTemple.isEmpty()){
				save.setPreventTempleDestruction(true);
				
				for(EntityPlayer player:(List<EntityPlayer>)world.playerEntities){
					if (playersInTemple.contains(player.getGameProfile().getId()))player.addStat(AchievementManager.REBIRTH,1);
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
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta){
		teleportNearby(world,x,y,z);
	}
	
	@Override
	public Item getItemDropped(int meta, Random rand, int fortune){
		return null;
	}
	
	@Override
	public int quantityDropped(Random rand){
		return 0;
	}
	
	public static boolean teleportNearby(World world, int x, int y, int z){
		if (world.getBlock(x,y,z) == Blocks.dragon_egg && !world.isRemote){
			for(int attempt = 0,xx,yy,zz; attempt < 1000; ++attempt){
				xx = x+world.rand.nextInt(16)-world.rand.nextInt(16);
				yy = y+world.rand.nextInt(8)-world.rand.nextInt(8);
				zz = z+world.rand.nextInt(16)-world.rand.nextInt(16);

				if (world.getBlock(xx,yy,zz).getMaterial() == Material.air){
					world.setBlock(xx,yy,zz,Blocks.dragon_egg,world.getBlockMetadata(x,y,z),2);
					world.setBlockToAir(x,y,z);
					
					PacketPipeline.sendToAllAround(world.provider.dimensionId,x,y,z,64D,new C22EffectLine(FXType.Line.DRAGON_EGG_TELEPORT,x+0.5D,y+0.5D,z+0.5D,xx+0.5D,yy+0.5D,zz+0.5D));
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
				PacketPipeline.sendToAllAround(endWorld.provider.dimensionId,coords.posX+0.5D,coords.posY+0.5D,coords.posZ+0.5D,64D,new C20Effect(FXType.Basic.DRAGON_EGG_RESET,coords.posX+0.5D,coords.posY+0.5D,coords.posZ+0.5D));
				return true;
			}
		}
		
		return false;
	}
}