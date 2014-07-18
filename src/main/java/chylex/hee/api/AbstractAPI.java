package chylex.hee.api;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;

public abstract class AbstractAPI{
	protected final void validate(){
		if (!Loader.instance().isInState(LoaderState.POSTINITIALIZATION))throw new RuntimeException("Do not use HEE API outside post initialization!");
	}
}
