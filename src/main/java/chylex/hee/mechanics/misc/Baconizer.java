package chylex.hee.mechanics.misc;
import net.minecraft.init.Blocks;
import chylex.hee.proxy.ModCommonProxy;

public final class Baconizer{
	public static void load(){
		if (!ModCommonProxy.hardcoreEnderbacon)return;
		
		Blocks.end_stone.setBlockName("baconStone");
	}
	
	public static String unlocalizedName(String name){
		return ModCommonProxy.hardcoreEnderbacon ? name+".bacon" : name;
	}
	
	public static String textureName(String name){
		return ModCommonProxy.hardcoreEnderbacon ? name+"_bacon" : name;
	}
	
	private Baconizer(){}
}
