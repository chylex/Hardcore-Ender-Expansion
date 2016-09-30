package chylex.hee.block.override;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import chylex.hee.entity.block.EntityBlockFallingDragonEgg;
import chylex.hee.entity.fx.FXHelper;
import chylex.hee.entity.fx.FXType;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C22EffectLine;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Pos.PosMutable;

public class BlockDragonEggCustom extends BlockDragonEgg{
	public BlockDragonEggCustom(){
		setBlockUnbreakable().setResistance(2000F).setStepSound(Block.soundTypeStone).setLightLevel(0.125F).setBlockName("dragonEgg").setBlockTextureName("dragon_egg");
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand){
		if (BlockFalling.func_149831_e(world, x, y-1, z) && y >= 0){ // OBFUSCATED can fall?
			if (!BlockFalling.fallInstantly && world.checkChunksExist(x-32, y-32, z-32, x+32, y+32, z+32)){
				world.spawnEntityInWorld(new EntityBlockFallingDragonEgg(world, x+0.5F, y+0.5F, z+0.5F));
			}
			else{
				PosMutable testPos = new PosMutable();
				testPos.set(x, y, z).setAir(world);
				while(BlockFalling.func_149831_e(world, testPos.x, testPos.y-1, testPos.z) && testPos.y > 0)--testPos.y;
				if (testPos.y > 0)testPos.setBlock(world, this, 0, 2);
			}
		}
	}
	
	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player){
		if (!world.isRemote){
			EntityItem item = new EntityItem(world, x+0.5D, y+0.25D, z+0.5D, new ItemStack(Blocks.dragon_egg));
			item.motionX = item.motionY = item.motionZ = 0D;
			item.delayBeforeCanPickup = 25;
			world.spawnEntityInWorld(item);
			world.setBlockToAir(x, y, z);
		}
		else{
			FXHelper.create("smoke").pos(x, y, z).fluctuatePos(0.5D).fluctuateMotion(0.08D).paramSingle(1.5F).spawn(world.rand, 25);
			FXHelper.create("portalbig").pos(x, y, z).fluctuatePos(0.5D).fluctuateMotion(0.08D).paramSingle(0.25F).spawn(world.rand, 15);
		}
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		teleportNearby(world, Pos.at(x, y, z));
		return true;
	}
	
	@Override
	public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int fortune){
		teleportNearby(world, Pos.at(x, y, z));
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta){
		teleportNearby(world, Pos.at(x, y, z));
	}
	
	@Override
	public Item getItemDropped(int meta, Random rand, int fortune){
		return null;
	}
	
	@Override
	public int quantityDropped(Random rand){
		return 0;
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player){
		return new ItemStack(Blocks.dragon_egg);
	}
	
	public static boolean teleportNearby(World world, Pos pos){
		if (!world.isRemote && pos.getBlock(world) == Blocks.dragon_egg){
			PosMutable newPos = new PosMutable();
			
			for(int attempt = 0; attempt < 1000; attempt++){
				newPos.set(pos).move(world.rand.nextInt(31)-15, world.rand.nextInt(15)-7, world.rand.nextInt(31)-15);
				
				if (newPos.isAir(world)){
					newPos.setBlock(world, Blocks.dragon_egg, pos.getMetadata(world), 2);
					pos.setAir(world);
					PacketPipeline.sendToAllAround(world.provider.dimensionId, pos, 64D, new C22EffectLine(FXType.Line.DRAGON_EGG_TELEPORT, pos, newPos));
					return true;
				}
			}
		}
		
		return false;
	}
}