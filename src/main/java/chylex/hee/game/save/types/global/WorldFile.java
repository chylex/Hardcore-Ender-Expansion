package chylex.hee.game.save.types.global;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.game.save.SaveFile;
import chylex.hee.world.end.EndTerritory;

public class WorldFile extends SaveFile{
	private final TObjectIntHashMap<EndTerritory> territories = new TObjectIntHashMap<>(EndTerritory.values.length);
	
	public WorldFile(){
		super("world.nbt");
	}
	
	public int increment(EndTerritory territory){
		setModified();
		return territories.adjustOrPutValue(territory,1,1)-1;
	}

	@Override
	protected void onSave(NBTTagCompound nbt){}

	@Override
	protected void onLoad(NBTTagCompound nbt){}
}
