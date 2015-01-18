package chylex.hee.api;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

public class AbstractAPI{
	protected AbstractAPI(){}
	
	protected final void validate(){
		if (!Loader.instance().isInState(LoaderState.POSTINITIALIZATION))throw new IllegalStateException("Do not use HEE API outside post initialization!");
	}
}
