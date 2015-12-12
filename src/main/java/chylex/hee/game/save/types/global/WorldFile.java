package chylex.hee.game.save.types.global;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.EnumUtils;
import chylex.hee.game.save.SaveFile;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.logging.Log;
import chylex.hee.world.end.EndTerritory;

public class WorldFile extends SaveFile{
	private final TObjectIntHashMap<EndTerritory> territories = new TObjectIntHashMap<>(EndTerritory.values.length);
	private Pos voidPortalPos;
	
	public WorldFile(){
		super("world.nbt");
	}
	
	public int increment(EndTerritory territory){
		setModified();
		return territories.adjustOrPutValue(territory,1,1)-1;
	}
	
	public void setVoidPortalPos(Pos pos){
		this.voidPortalPos = pos.immutable();
	}
	
	public @Nullable Pos getVoidPortalPos(){
		return voidPortalPos;
	}

	@Override
	protected void onSave(NBTTagCompound nbt){
		if (voidPortalPos != null)nbt.setLong("voidPortal",voidPortalPos.toLong());
		
		NBTTagCompound territoryTag = new NBTTagCompound();
		for(EndTerritory territory:territories.keySet())nbt.setInteger(territory.toString(),territories.get(territory));
		nbt.setTag("territories",territoryTag);
	}

	@Override
	protected void onLoad(NBTTagCompound nbt){
		if (nbt.hasKey("voidPortal"))voidPortalPos = Pos.at(nbt.getLong("voidPortal"));
		
		NBTTagCompound territoryTag = nbt.getCompoundTag("territories");
		
		for(String key:(Set<String>)territoryTag.func_150296_c()){
			EndTerritory territory = EnumUtils.getEnum(EndTerritory.class,key);
			
			if (territory == null)Log.reportedError("Unknown territory $0 in WorldFile.",key);
			else territories.put(territory,nbt.getInteger(key));
		}
	}
}
