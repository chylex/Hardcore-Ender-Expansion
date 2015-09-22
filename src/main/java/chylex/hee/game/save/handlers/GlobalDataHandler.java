package chylex.hee.game.save.handlers;
import java.io.File;
import java.util.IdentityHashMap;
import java.util.Map;
import chylex.hee.game.save.ISaveDataHandler;
import chylex.hee.game.save.SaveFile;

public final class GlobalDataHandler implements ISaveDataHandler{
	private final Map<Class<? extends SaveFile>,SaveFile> cache = new IdentityHashMap<>();
	private File root;
	
	@Override
	public void register(){}
	
	@Override
	public void clear(File root){
		cache.clear();
		this.root = root;
	}
	
	public <T extends SaveFile> T get(Class<T> cls){
		SaveFile savefile = cache.get(cls);
		
		if (savefile == null){
			try{
				cache.put(cls,savefile = cls.newInstance());
				savefile.loadFromNBT(root);
			}catch(Exception e){
				throw new RuntimeException("Could not construct a new instance of SaveFile - "+cls.getName(),e);
			}
		}
		
		return (T)savefile;
	}
	
	@Override
	public void save(){
		cache.values().stream().filter(savefile -> savefile.wasModified()).forEach(savefile -> savefile.saveToNBT(root));
	}
}
