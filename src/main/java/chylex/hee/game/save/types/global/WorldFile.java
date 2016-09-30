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
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagLong;
import chylex.hee.game.save.SaveFile;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.nbt.NBT;
import chylex.hee.system.abstractions.nbt.NBTCompound;
import chylex.hee.system.abstractions.nbt.NBTList;
import chylex.hee.system.collections.CollectionUtil;
import chylex.hee.system.collections.EmptyEnumSet;
import chylex.hee.system.logging.Log;
import chylex.hee.world.end.EndTerritory;
import com.google.common.primitives.Longs;

public class WorldFile extends SaveFile{
	private final TObjectIntHashMap<EndTerritory> territories = new TObjectIntHashMap<>(1);
	private final TLongHashSet rareTerritories = new TLongHashSet(10);
	
	private final TLongIntHashMap territoryVariations = new TLongIntHashMap(1, Constants.DEFAULT_LOAD_FACTOR, 0L, 0);
	private final TLongLongHashMap territoryPos = new TLongLongHashMap(1, Constants.DEFAULT_LOAD_FACTOR, 0L, 0L);
	private final TLongObjectHashMap<NBTCompound> territoryData = new TLongObjectHashMap<>(1);
	
	private Pos voidPortalPos;
	
	public WorldFile(){
		super("world.nbt");
	}
	
	public int increment(EndTerritory territory){
		setModified();
		return territories.adjustOrPutValue(territory, 1, 1)-1;
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
		
		territoryVariations.put(hash, territory.properties.serialize(variations));
		setModified();
	}
	
	public EnumSet<? extends Enum<?>> getTerritoryVariations(long hash){
		int bits = territoryVariations.get(hash);
		return bits == 0 ? EmptyEnumSet.get() : EndTerritory.getFromHash(hash).map(territory -> territory.properties.deserialize(bits)).orElseGet(EmptyEnumSet::get);
	}
	
	public void setTerritoryPos(long hash, Pos spawnPoint){
		territoryPos.put(hash, spawnPoint.toLong());
		setModified();
	}
	
	public Pos getTerritoryPos(long hash){
		return Pos.at(territoryPos.get(hash));
	}
	
	public NBTCompound getTerritoryData(long hash){
		NBTCompound tag = territoryData.get(hash);
		if (tag == null)territoryData.put(hash, tag = NBT.callback(this::setModified));
		
		return tag;
	}
	
	public void setVoidPortalPos(Pos pos){
		this.voidPortalPos = pos.immutable();
	}
	
	public @Nullable Pos getVoidPortalPos(){
		return voidPortalPos;
	}

	@Override
	protected void onSave(NBTCompound nbt){
		if (voidPortalPos != null)nbt.setLong("voidPortal", voidPortalPos.toLong());
		
		NBTCompound territoryTag = new NBTCompound();
		NBTCompound territoryVariationsTag = new NBTCompound();
		NBTCompound territoryPosTag = new NBTCompound();
		NBTCompound territoryDataTag = new NBTCompound();
		NBTList rareTerritoriesTag = new NBTList();
		
		for(EndTerritory territory:territories.keySet())territoryTag.setInt(serializeByte((byte)territory.ordinal()), territories.get(territory));
		
		for(TLongIntIterator iter = territoryVariations.iterator(); iter.hasNext();){
			iter.advance();
			territoryVariationsTag.setInt(serializeLong(iter.key()), iter.value());
		}
		
		for(TLongLongIterator iter = territoryPos.iterator(); iter.hasNext();){
			iter.advance();
			territoryPosTag.setLong(serializeLong(iter.key()), iter.value());
		}
		
		for(TLongObjectIterator<NBTCompound> iter = territoryData.iterator(); iter.hasNext();){
			iter.advance();
			
			if (iter.value().isEmpty())iter.remove();
			else territoryDataTag.setCompound(serializeLong(iter.key()), iter.value());
		}
		
		for(TLongIterator iter = rareTerritories.iterator(); iter.hasNext();){
			rareTerritoriesTag.appendTag(new NBTTagLong(iter.next()));
		}
		
		nbt.setCompound("territories", territoryTag);
		nbt.setCompound("tvar", territoryVariationsTag);
		nbt.setCompound("tpos", territoryPosTag);
		nbt.setCompound("tdata", territoryDataTag);
		nbt.setList("trare", rareTerritoriesTag);
	}

	@Override
	protected void onLoad(NBTCompound nbt){
		if (nbt.hasKey("voidPortal"))voidPortalPos = Pos.at(nbt.getLong("voidPortal"));
		
		NBTCompound territoryTag = nbt.getCompound("territories");
		NBTCompound territoryVariationsTag = nbt.getCompound("tvar");
		NBTCompound territoryPosTag = nbt.getCompound("tpos");
		NBTCompound territoryDataTag = nbt.getCompound("tdata");
		NBTList rareTerritoriesTag = nbt.getList("trare");
		
		territories.ensureCapacity(territoryTag.size());
		territoryVariations.ensureCapacity(territoryVariationsTag.size());
		territoryPos.ensureCapacity(territoryPosTag.size());
		territoryData.ensureCapacity(territoryDataTag.size());
		
		territoryTag.forEachInt((key, value) -> {
			EndTerritory territory = CollectionUtil.get(EndTerritory.values, deserializeByte(key)).orElse(null);
			
			if (territory == null)Log.reportedError("Unknown territory $0 in WorldFile.", key);
			else territories.put(territory, value);
		});
		
		territoryVariationsTag.forEachInt((key, value) -> territoryVariations.put(deserializeLong(key), value));
		territoryPosTag.forEachLong((key, value) -> territoryPos.put(deserializeLong(key), value));
		territoryDataTag.forEachCompound((key, value) -> territoryData.put(deserializeLong(key), value));
		rareTerritoriesTag.readLongs().forEach(rareTerritories::add);
	}
	
	private static final String serializeByte(byte value){
		return new String(new byte[]{ value }, StandardCharsets.ISO_8859_1);
	}
	
	private static final byte deserializeByte(String str){
		return str.getBytes()[0];
	}
	
	private static final String serializeLong(long value){
		return new String(Longs.toByteArray(value), StandardCharsets.ISO_8859_1);
	}
	
	private static final long deserializeLong(String str){
		return Longs.fromByteArray(str.getBytes(StandardCharsets.ISO_8859_1));
	}
}
