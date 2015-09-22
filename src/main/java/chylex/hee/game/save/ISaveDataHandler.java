package chylex.hee.game.save;
import java.io.File;

public interface ISaveDataHandler<A>{
	void register();
	void clear(File root);
	<T extends SaveFile> T get(A source);
	void save();
}
