package chylex.hee.api;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;

/**
 * Main API class that contains and provides specific API instances.
 */
public final class HeeAPI{
	private static final EssenceAPI essence = new EssenceAPI();
	private static final WorldAPI world = new WorldAPI();
	private static final DecompositionAPI decomposition = new DecompositionAPI();
	
	/**
	 * @return API instance for Essences and Essence Altars.
	 */
	public static EssenceAPI essence(){
		validate();
		return essence;
	}
	
	/**
	 * @return API instance for loot and new biomes in the End.
	 */
	public static WorldAPI world(){
		validate();
		return world;
	}
	
	/**
	 * @return  API instance for Stardust and Decomposition Table.
	 */
	public static DecompositionAPI decomposition(){
		validate();
		return decomposition;
	}
	
	private static void validate(){
		if (!Loader.instance().isInState(LoaderState.POSTINITIALIZATION))throw new RuntimeException("Do not use HEE API outside post initialization!");
	}
}
