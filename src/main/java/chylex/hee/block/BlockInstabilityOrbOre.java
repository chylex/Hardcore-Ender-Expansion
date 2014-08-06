package chylex.hee.block;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C08PlaySound;

public class BlockInstabilityOrbOre extends BlockAbstractOre{
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
			dropXpOnBlockBreak(world,x,y,z,MathHelper.getRandomIntegerInRange(world.rand,6,9));
		}
		else{
			PacketPipeline.sendToAllAround(world.provider.dimensionId,x+0.5D,y+0.5D,z+0.5D,32D,new C08PlaySound(C08PlaySound.GLASS_BREAK,x+0.5D,y+0.5D,z+0.5D,1F,world.rand.nextFloat()*0.1F+0.92F));
		}
		
		return ret;
	}

	@Override
	protected void onOreMined(EntityPlayer player, ArrayList<ItemStack> drops, int x, int y, int z, int meta, int fortune){
		if (KnowledgeRegistrations.INSTABILITY_ORB_ORE.tryUnlockFragment(player,0.12F).stopTrying)return;
		if (KnowledgeRegistrations.INSTABILITY_ORB.tryUnlockFragment(player,0.07F,new byte[]{ 0,2 }).stopTrying)return;
		if (player.worldObj.rand.nextInt(3) != 0)KnowledgeRegistrations.ENHANCED_BREWING_STAND.tryUnlockFragment(player,0.1F,new byte[]{ 3 });
		else KnowledgeRegistrations.ENERGY_EXTRACTION_TABLE.tryUnlockFragment(player,0.15F,new byte[]{ 6 });
	}
}
