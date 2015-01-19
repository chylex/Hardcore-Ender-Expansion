package chylex.hee.block;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.BlockOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import chylex.hee.item.ItemList;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C08PlaySound;
import chylex.hee.system.logging.Log;

public class BlockInstabilityOrbOre extends BlockOre{
	@Override
	public int quantityDropped(Random rand){
		return 0;
	}
	
	@Override
	public int quantityDroppedWithBonus(int fortune, Random rand){
		return 0;
	}
	
	@Override
	public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
		ArrayList<ItemStack> ret = new ArrayList<>();
		
		if (!(world instanceof World)){
			Log.error("Invalid world instance, expecting World, got $1",world.getClass());
			return ret;
		}

		if (BlockList.blockRandom.nextInt(100) > 60-fortune*4){
			ret.add(new ItemStack(ItemList.instability_orb));
			dropXpOnBlockBreak((World)world,pos,MathHelper.getRandomIntegerInRange(BlockList.blockRandom,6,9));
		}
		else{
			PacketPipeline.sendToAllAround(((World)world).provider.getDimensionId(),pos.getX()+0.5D,pos.getY()+0.5D,pos.getZ()+0.5D,32D,new C08PlaySound(C08PlaySound.GLASS_BREAK,pos.getX()+0.5D,pos.getY()+0.5D,pos.getZ()+0.5D,1F,BlockList.blockRandom.nextFloat()*0.1F+0.92F));
		}
		
		return ret;
	}
}
