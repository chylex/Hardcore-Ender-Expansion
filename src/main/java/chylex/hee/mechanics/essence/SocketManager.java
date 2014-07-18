package chylex.hee.mechanics.essence;
import java.util.IdentityHashMap;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import chylex.hee.tileentity.TileEntityEssenceAltar;

public final class SocketManager{
	public static final byte EFFECT_SPEED_BOOST = 1, EFFECT_RANGE_INCREASE = 2, EFFECT_LOWER_COST = 4;
	
	private static final IdentityHashMap<Item,Byte> boostBlocks = new IdentityHashMap<>();
	private static final IdentityHashMap<Item,Byte> effectBlocks = new IdentityHashMap<>();
	
	static{
		boostBlocks.put(Item.getItemFromBlock(Blocks.iron_block),(byte)1);
		boostBlocks.put(Item.getItemFromBlock(Blocks.gold_block),(byte)3);
		boostBlocks.put(Item.getItemFromBlock(Blocks.diamond_block),(byte)7);
		boostBlocks.put(Item.getItemFromBlock(Blocks.emerald_block),(byte)10);
		
		effectBlocks.put(Item.getItemFromBlock(Blocks.redstone_block),EFFECT_SPEED_BOOST);
		effectBlocks.put(Item.getItemFromBlock(Blocks.lapis_block),EFFECT_RANGE_INCREASE);
		effectBlocks.put(Item.getItemFromBlock(Blocks.quartz_block),EFFECT_LOWER_COST);
	}
	
	public static final boolean isValidSocketBlock(Block block){
		return boostBlocks.containsKey(Item.getItemFromBlock(block)) || effectBlocks.containsKey(Item.getItemFromBlock(block));
	}
	
	public static final byte getSocketEffects(TileEntityEssenceAltar altar){
		byte b = 0;
		
		for(ItemStack is:altar.getSocketContents()){
			if (is == null)continue;
			else if (effectBlocks.containsKey(is.getItem()))b |= effectBlocks.get(is.getItem());
		}
		
		return b;
	}
	
	public static final byte getSocketBoost(TileEntityEssenceAltar altar){
		byte b = 0;
		
		for(ItemStack is:altar.getSocketContents()){
			if (is == null)continue;
			else if (boostBlocks.containsKey(is.getItem()))b += boostBlocks.get(is.getItem());
		}
		
		return b;
	}
}
