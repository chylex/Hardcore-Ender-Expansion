package chylex.hee.game.save.types.global;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.EnumUtils;
import chylex.hee.game.save.SaveFile;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.logging.Log;
import chylex.hee.system.util.NBTUtil;
import chylex.hee.world.end.EndTerritory;
import com.google.common.primitives.Longs;

public class WorldFile extends SaveFile{
	private final TObjectIntHashMap<EndTerritory> territories = new TObjectIntHashMap<>(EndTerritory.values.length);
	private final TLongObjectHashMap<NBTTagCompound> territoryData = new TLongObjectHashMap<>();
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
	
	public NBTTagCompound getTerritoryData(long hash){
		NBTTagCompound tag = territoryData.get(hash);
		if (tag == null)territoryData.put(hash,tag = new NBTTagCompound());
		
		return NBTUtil.createCallbackTag(tag,this::setModified);
	}

	@Override
	protected void onSave(NBTTagCompound nbt){
		if (voidPortalPos != null)nbt.setLong("voidPortal",voidPortalPos.toLong());
		
		NBTTagCompound territoryTag = new NBTTagCompound();
		for(EndTerritory territory:territories.keySet())territoryTag.setInteger(territory.toString(),territories.get(territory));
		nbt.setTag("territories",territoryTag);
		
		NBTTagCompound territoryDataTag = new NBTTagCompound();
		
		for(TLongObjectIterator<NBTTagCompound> iter = territoryData.iterator(); iter.hasNext();){
			iter.advance();
			territoryDataTag.setTag(new String(Longs.toByteArray(iter.key()),StandardCharsets.ISO_8859_1),iter.value());
		}
		
		nbt.setTag("tdata",territoryDataTag);
	}

	@Override
	protected void onLoad(NBTTagCompound nbt){
		if (nbt.hasKey("voidPortal"))voidPortalPos = Pos.at(nbt.getLong("voidPortal"));
		
		NBTTagCompound territoryTag = nbt.getCompoundTag("territories");
		
		for(String key:(Set<String>)territoryTag.func_150296_c()){
			EndTerritory territory = EnumUtils.getEnum(EndTerritory.class,key);
			
			if (territory == null)Log.reportedError("Unknown territory $0 in WorldFile.",key);
			else territories.put(territory,territoryTag.getInteger(key));
		}
		
		NBTTagCompound territoryDataTag = nbt.getCompoundTag("tdata");
		
		for(String key:(Set<String>)territoryDataTag.func_150296_c()){
			territoryData.put(Longs.fromByteArray(key.getBytes(StandardCharsets.ISO_8859_1)),territoryDataTag.getCompoundTag(key));
		}
	}
}
