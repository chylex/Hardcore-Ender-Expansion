package chylex.hee.system.savedata;
import java.util.IdentityHashMap;
import java.util.Map;

public final class WorldDataHandler{
	private static final Map<Class<? extends WorldSavefile>,WorldSavefile> cache = new IdentityHashMap<>();
	
	public static <T> T get(Class<? extends WorldSavefile> cls){
		WorldSavefile savefile = cache.get(cls);
		
		if (savefile == null){
			try{
				cache.put(cls,savefile = cls.newInstance());
			}catch(InstantiationException | IllegalAccessException e){
				throw new RuntimeException("Could not construct a new instance of WorldSavefile - "+cls.getName(),e);
			}
		}
		
		return (T)savefile;
	}
}
