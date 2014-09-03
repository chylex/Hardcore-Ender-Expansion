package chylex.hee.block;
import java.util.ArrayList;
import net.minecraft.block.BlockOre;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public abstract class BlockAbstractOre extends BlockOre{ // TODO redundant?
	protected BlockAbstractOre(){}

	@Override
	public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta){
		player.addStat(StatList.mineBlockStatArray[getIdFromBlock(this)],1);
		player.addExhaustion(0.025F);

		if (canSilkHarvest(world,player,x,y,z,meta) && EnchantmentHelper.getSilkTouchModifier(player)){
			ArrayList<ItemStack> items = new ArrayList<>();
			
			ItemStack is = createStackedBlock(meta);
			if (is != null)items.add(is);
			
			ForgeEventFactory.fireBlockHarvesting(items,world,this,x,y,z,meta,0,1F,true,player);
			for(ItemStack item:items)dropBlockAsItem(world,x,y,z,item);
		}
		else{
			int fortune = EnchantmentHelper.getFortuneModifier(player);
			ArrayList<ItemStack> items = getDrops(world,x,y,z,meta,fortune);
			ForgeEventFactory.fireBlockHarvesting(items,world,this,x,y,z,meta,fortune,1F,false,player);

			onOreMined(player,items,x,y,z,meta,fortune);
			for(ItemStack item:items)dropBlockAsItem(world,x,y,z,item);
		}
	}
	
	/**
	 * Triggers when the ore is mined without Silk Touch.
	 */
	protected abstract void onOreMined(EntityPlayer player, ArrayList<ItemStack> drops, int x, int y, int z, int meta, int fortune);
}
