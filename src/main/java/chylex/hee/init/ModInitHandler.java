package chylex.hee.init;
import java.util.ArrayList;
import java.util.List;

public final class ModInitHandler{
	private static final List<Runnable> afterPreInit = new ArrayList<>();
	
	public static void afterPreInit(Runnable run){
		afterPreInit.add(run);
	}
	
	public static void finishPreInit(){
		for(Runnable preInit:afterPreInit)preInit.run();
		afterPreInit.clear();
	}
	
	private ModInitHandler(){}
}
