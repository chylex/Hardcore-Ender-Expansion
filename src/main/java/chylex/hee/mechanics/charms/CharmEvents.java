package chylex.hee.mechanics.charms;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import cpw.mods.fml.common.FMLCommonHandler;

final class CharmEvents{
	private static final CharmEvents instance = new CharmEvents();
	
	static void enable(){
		MinecraftForge.EVENT_BUS.register(instance);
		FMLCommonHandler.instance().bus().register(instance);
	}
	
	static void disable(){
		MinecraftForge.EVENT_BUS.unregister(instance);
		FMLCommonHandler.instance().bus().unregister(instance);
	}
	
	static short[] getProp(EntityPlayer player, String prop){
		CharmPouchInfo info = CharmPouchHandler.getActivePouch(player);
		if (info == null)return ArrayUtils.EMPTY_SHORT_ARRAY;
		
		List<Short> values = new ArrayList<Short>(5);
		for(Pair<CharmType,CharmRecipe> entry:info.charms){
			short value = entry.getRight().getProp(prop);
			if (value != -1)values.add(value);
		}
		
		short[] array = new short[values.size()];
		for(int a = 0; a < array.length; a++)array[a] = values.get(0).shortValue();
		return array;
	}
	
	private CharmEvents(){}
}
