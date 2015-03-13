package chylex.hee.api;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;

/**
 * DO NOT USE, WILL BE REMOVED IN FAVOR OF http://hee-api.chylex.com/
 */
@Deprecated
public final class HeeAPI{
	private static final EssenceAPI essenceAPI = new EssenceAPI();
	private static final WorldAPI worldAPI = new WorldAPI();
	private static final DecompositionAPI decompositionAPI = new DecompositionAPI();
	
	/**
	 * DO NOT USE, WILL BE REMOVED IN FAVOR OF http://hee-api.chylex.com/
	 */
	@Deprecated
	public static EssenceAPI essence(){
		validate();
		return essenceAPI;
	}
	
	/**
	 * DO NOT USE, WILL BE REMOVED IN FAVOR OF http://hee-api.chylex.com/
	 */
	@Deprecated
	public static WorldAPI world(){
		validate();
		return worldAPI;
	}
	
	/**
	 * DO NOT USE, WILL BE REMOVED IN FAVOR OF http://hee-api.chylex.com/
	 */
	@Deprecated
	public static DecompositionAPI decomposition(){
		validate();
		return decompositionAPI;
	}
	
	private static void validate(){
		if (!Loader.instance().isInState(LoaderState.POSTINITIALIZATION))throw new IllegalStateException("Do not use HEE API outside post initialization!");
	}
	
	private HeeAPI(){}
}
