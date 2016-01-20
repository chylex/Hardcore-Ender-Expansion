package chylex.hee.game.save.types.global;
import gnu.trove.impl.Constants;
import gnu.trove.iterator.TLongIntIterator;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.iterator.TLongLongIterator;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.hash.TLongIntHashMap;
import gnu.trove.map.hash.TLongLongHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.hash.TLongHashSet;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import chylex.hee.game.save.SaveFile;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.collections.CollectionUtil;
import chylex.hee.system.collections.EmptyEnumSet;
import chylex.hee.system.logging.Log;
import chylex.hee.system.util.NBTUtil;
import chylex.hee.world.end.EndTerritory;
import com.google.common.primitives.Longs;

public class WorldFile extends SaveFile{
	private final TObjectIntHashMap<EndTerritory> territories = new TObjectIntHashMap<>(1);
	private final TLongHashSet rareTerritories = new TLongHashSet(10);
	
	private final TLongIntHashMap territoryVariations = new TLongIntHashMap(1,Constants.DEFAULT_LOAD_FACTOR,0L,0);
	private final TLongLongHashMap territoryPos = new TLongLongHashMap(1,Constants.DEFAULT_LOAD_FACTOR,0L,0L);
	private final TLongObjectHashMap<NBTTagCompound> territoryData = new TLongObjectHashMap<>(1);
	
	private Pos voidPortalPos;
	
	public WorldFile(){
		super("world.nbt");
	}
	
	public int increment(EndTerritory territory){
		setModified();
		return territories.adjustOrPutValue(territory,1,1)-1;
	}
	
	public void setTerritoryRare(long hash){
		rareTerritories.add(hash);
	}
	
	public boolean isTerritoryRare(long hash){
		return rareTerritories.contains(hash);
	}
	
	public void setTerritoryVariations(long hash, EnumSet<? extends Enum<?>> variations){
		if (variations.isEmpty())return;
		
		EndTerritory territory = EndTerritory.getFromHash(hash).orElse(null);
		if (territory == null)return;
		
		territoryVariations.put(hash,territory.properties.serialize(variations));
		setModified();
	}
	
	public EnumSet<? extends Enum<?>> getTerritoryVariations(long hash){
		int bits = territoryVariations.get(hash);
		return bits == 0 ? EmptyEnumSet.get() : EndTerritory.getFromHash(hash).map(territory -> territory.properties.deserialize(bits)).orElseGet(EmptyEnumSet::get);
	}
	
	public void setTerritoryPos(long hash, Pos spawnPoint){
		territoryPos.put(hash,spawnPoint.toLong());
		setModified();
	}
	
	public Pos getTerritoryPos(long hash){
		return Pos.at(territoryPos.get(hash));
	}
	
	public NBTTagCompound getTerritoryData(long hash){
		NBTTagCompound tag = territoryData.get(hash);
		if (tag == null)territoryData.put(hash,tag = NBTUtil.createCallbackTag(this::setModified));
		
		return tag;
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
		NBTTagCompound territoryVariationsTag = new NBTTagCompound();
		NBTTagCompound territoryPosTag = new NBTTagCompound();
		NBTTagCompound territoryDataTag = new NBTTagCompound();
		NBTTagList rareTerritoriesTag = new NBTTagList();
		
		for(EndTerritory territory:territories.keySet())territoryTag.setInteger(serializeByte((byte)territory.ordinal()),territories.get(territory));
		
		for(TLongIntIterator iter = territoryVariations.iterator(); iter.hasNext();){
			iter.advance();
			territoryVariationsTag.setInteger(serializeLong(iter.key()),iter.value());
		}
		
		for(TLongLongIterator iter = territoryPos.iterator(); iter.hasNext();){
			iter.advance();
			territoryPosTag.setLong(serializeLong(iter.key()),iter.value());
		}
		
		for(TLongObjectIterator<NBTTagCompound> iter = territoryData.iterator(); iter.hasNext();){
			iter.advance();
			
			if (iter.value().hasNoTags())iter.remove();
			else territoryDataTag.setTag(serializeLong(iter.key()),iter.value());
		}
		
		for(TLongIterator iter = rareTerritories.iterator(); iter.hasNext();){
			rareTerritoriesTag.appendTag(new NBTTagLong(iter.next()));
		}
		
		nbt.setTag("territories",territoryTag);
		nbt.setTag("tvar",territoryVariationsTag);
		nbt.setTag("tpos",territoryPosTag);
		nbt.setTag("tdata",territoryDataTag);
		nbt.setTag("trare",rareTerritoriesTag);
	}

	@Override
	protected void onLoad(NBTTagCompound nbt){
		if (nbt.hasKey("voidPortal"))voidPortalPos = Pos.at(nbt.getLong("voidPortal"));
		
		NBTTagCompound territoryTag = nbt.getCompoundTag("territories");
		NBTTagCompound territoryVariationsTag = nbt.getCompoundTag("tvar");
		NBTTagCompound territoryPosTag = nbt.getCompoundTag("tpos");
		NBTTagCompound territoryDataTag = nbt.getCompoundTag("tdata");
		
		territories.ensureCapacity(territoryTag.func_150296_c().size());
		territoryVariations.ensureCapacity(territoryVariationsTag.func_150296_c().size());
		territoryPos.ensureCapacity(territoryPosTag.func_150296_c().size());
		territoryData.ensureCapacity(territoryDataTag.func_150296_c().size());
		
		for(String key:(Set<String>)territoryTag.func_150296_c()){
			EndTerritory territory = CollectionUtil.get(EndTerritory.values,deserializeByte(key)).orElse(null);
			
			if (territory == null)Log.reportedError("Unknown territory $0 in WorldFile.",key);
			else territories.put(territory,territoryTag.getInteger(key));
		}
		
		for(String key:(Set<String>)territoryVariationsTag.func_150296_c()){
			territoryVariations.put(deserializeLong(key),territoryVariationsTag.getInteger(key));
		}
		
		for(String key:(Set<String>)territoryPosTag.func_150296_c()){
			territoryPos.put(deserializeLong(key),territoryPosTag.getLong(key));
		}
		
		for(String key:(Set<String>)territoryDataTag.func_150296_c()){
			territoryData.put(deserializeLong(key),territoryDataTag.getCompoundTag(key));
		}
		
		NBTUtil.readNumericList(nbt,"trare").forEach(tag -> rareTerritories.add(tag.func_150291_c()));
	}
	
	private static final String serializeByte(byte value){
		return new String(new byte[]{ value },StandardCharsets.ISO_8859_1);
	}
	
	private static final byte deserializeByte(String str){
		return str.getBytes()[0];
	}
	
	private static final String serializeLong(long value){
		return new String(Longs.toByteArray(value),StandardCharsets.ISO_8859_1);
	}
	
	private static final long deserializeLong(String str){
		return Longs.fromByteArray(str.getBytes(StandardCharsets.ISO_8859_1));
	}
}
