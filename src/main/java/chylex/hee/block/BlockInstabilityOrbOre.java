package chylex.hee.block;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.BlockOre;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import chylex.hee.init.ItemList;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C08PlaySound;

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
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune){
		ArrayList<ItemStack> ret = new ArrayList<>();

		if (world.rand.nextInt(100) > 60-fortune*4){
			ret.add(new ItemStack(ItemList.instability_orb));
			dropXpOnBlockBreak(world, x, y, z, MathHelper.getRandomIntegerInRange(world.rand, 6, 9));
		}
		else{
			PacketPipeline.sendToAllAround(world.provider.dimensionId, x+0.5D, y+0.5D, z+0.5D, 32D, new C08PlaySound(C08PlaySound.GLASS_BREAK, x+0.5D, y+0.5D, z+0.5D, 1F, world.rand.nextFloat()*0.1F+0.92F));
		}
		
		return ret;
	}
}
