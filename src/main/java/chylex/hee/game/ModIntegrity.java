package chylex.hee.game;
import java.lang.reflect.Field;
import java.util.Hashtable;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.DimensionManager;
import chylex.hee.block.override.BlockDragonEggCustom;
import chylex.hee.block.override.BlockEndPortalCustom;
import chylex.hee.block.override.BlockEndPortalFrameCustom;
import chylex.hee.entity.block.EntityBlockEnderCrystal;
import chylex.hee.entity.mob.EntityMobEnderman;
import chylex.hee.entity.mob.EntityMobSilverfish;
import chylex.hee.world.biome.BiomeGenHardcoreEnd;
import chylex.hee.world.providers.WorldProviderHardcoreEnd;

public final class ModIntegrity{
	/**
	 * Verifies that all the important mechanics of the mod will work (such as dimension and mob overrides).
	 */
	public static void verify() throws IllegalStateException{
		if (Blocks.dragon_egg.getClass() != BlockDragonEggCustom.class){
			throw new IllegalStateException("Mod conflict detected - Dragon Egg class mismatch: "+Blocks.dragon_egg.getClass().getName());
		}

		if (Blocks.end_portal.getClass() != BlockEndPortalCustom.class){
			throw new IllegalStateException("Mod conflict detected - End Portal class mismatch: "+Blocks.end_portal.getClass().getName());
		}

		if (Blocks.end_portal_frame.getClass() != BlockEndPortalFrameCustom.class){
			throw new IllegalStateException("Mod conflict detected - End Portal Frame class mismatch: "+Blocks.end_portal_frame.getClass().getName());
		}
		
		if (BiomeGenBase.sky.getClass() != BiomeGenHardcoreEnd.class){
			throw new IllegalStateException("Mod conflict detected - End biome class mismatch: "+BiomeGenBase.sky.getClass().getName());
		}
		
		if (getWorldProviderType(1) != WorldProviderHardcoreEnd.class){
			throw new IllegalStateException("Mod conflict detected - End world provider class mismatch: "+getWorldProviderType(1).getName());
		}
		
		if (EntityList.stringToClassMapping.get("Enderman") != EntityMobEnderman.class){
			throw new IllegalStateException("Mod conflict detected - Enderman class mismatch: "+((Class)EntityList.stringToClassMapping.get("Enderman")).getName());
		}
		
		if (EntityList.stringToClassMapping.get("Silverfish") != EntityMobSilverfish.class){
			throw new IllegalStateException("Mod conflict detected - Silverfish class mismatch: "+((Class)EntityList.stringToClassMapping.get("Silverfish")).getName());
		}
		
		if (EntityList.stringToClassMapping.get("EnderCrystal") != EntityBlockEnderCrystal.class){
			throw new IllegalStateException("Mod conflict detected - Ender Crystal class mismatch: "+((Class)EntityList.stringToClassMapping.get("EnderCrystal")).getName());
		}
	}
	
	private static Class getWorldProviderType(int dim){
		try{
			Field f = DimensionManager.class.getDeclaredField("providers");
			f.setAccessible(true); // let it throw NPE if the field isn't found
			return ((Hashtable<Integer,Class>)f.get(null)).get(1);
		}catch(Throwable t){
			throw new RuntimeException("Could not retrieve world provider!",t);
		}
	}
	
	private ModIntegrity(){}
}
