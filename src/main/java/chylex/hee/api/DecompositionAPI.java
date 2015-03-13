package chylex.hee.api;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import net.minecraft.item.Item;
import chylex.hee.mechanics.misc.StardustDecomposition;
import chylex.hee.system.util.ItemPattern;

/**
 * API for Stardust and Decomposition Table (uncrafting).
 */
@Deprecated
public final class DecompositionAPI extends AbstractAPI{
	public DecompositionAPI(){}
	
	/**
	 * Blacklists an item and all of its damage values in the Decomposition Table.
	 * @param item Item to be banned. To ban a block, use {@link net.minecraft.item.Item#getItemFromBlock(net.minecraft.block.Block) getItemFromBlock(Block)} to convert it.
	 */
	@Deprecated
	public static void blacklistItem(Item item){
		blacklistItem(item,-1);
	}
	
	/**
	 * Blacklists an item and selected damage values of it in the Decomposition Table.
	 * @param item Item to be banned. To ban a block, use {@link net.minecraft.item.Item#getItemFromBlock(net.minecraft.block.Block) getItemFromBlock(Block)} to convert it.
	 * @param blacklistedDamageValues Array of damage values to blacklist.
	 */
	@Deprecated
	public static void blacklistItem(Item item, int...blacklistedDamageValues){
		UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(item);
		StardustDecomposition.addToBlacklist(new ItemPattern().setItemName(id.modId,id.name).setDamageValues(blacklistedDamageValues));
	}
	
	/**
	 * Blacklists items by parsing a string, which follows the same syntax rules as decompositionBlackList in configuration - {@code http://hee.chylex.com/config}.
	 * @param data String to parse.
	 */
	@Deprecated
	public static void blacklistItemFromString(String data){
		StardustDecomposition.addFromString(data);
	}
}
