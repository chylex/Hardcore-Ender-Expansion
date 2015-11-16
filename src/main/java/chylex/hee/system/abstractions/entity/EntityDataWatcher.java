package chylex.hee.system.abstractions.entity;
import gnu.trove.map.hash.TObjectByteHashMap;
import javax.annotation.Nullable;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public final class EntityDataWatcher{
	private final DataWatcher watcher;
	private final TObjectByteHashMap<Enum<?>> idMap;
	
	public EntityDataWatcher(Entity trackedEntity){
		this.watcher = trackedEntity.getDataWatcher();
		this.idMap = new TObjectByteHashMap<>(8);
	}
	
	private void addObject(Enum<?> linkedEnum, int internalId, Object obj){
		watcher.addObject(internalId,obj);
		idMap.put(linkedEnum,(byte)internalId);
	}
	
	private byte getId(Enum<?> linkedEnum){
		byte value = idMap.get(linkedEnum);
		if (value == idMap.getNoEntryValue())throw new IllegalArgumentException("Enum was not registered in EntityDataWatcher!");
		return value;
	}
	
	// BOOLEAN
	
	public void addBoolean(Enum<?> linkedEnum, int internalId){
		addObject(linkedEnum,internalId,Byte.valueOf((byte)0));
	}
	
	public void addBoolean(Enum<?> linkedEnum, int internalId, boolean initialValue){
		addObject(linkedEnum,internalId,Byte.valueOf((byte)(initialValue ? 1 : 0)));
	}
	
	public boolean getBoolean(Enum<?> linkedEnum){
		return watcher.getWatchableObjectByte(getId(linkedEnum)) == 1;
	}
	
	public void setBoolean(Enum<?> linkedEnum, boolean newValue){
		watcher.updateObject(getId(linkedEnum),Byte.valueOf((byte)(newValue ? 1 : 0)));
	}
	
	// BYTE
	
	public void addByte(Enum<?> linkedEnum, int internalId){
		addObject(linkedEnum,internalId,Byte.valueOf((byte)0));
	}
	
	public void addByte(Enum<?> linkedEnum, int internalId, int initialValue){
		addObject(linkedEnum,internalId,Byte.valueOf((byte)initialValue));
	}
	
	public byte getByte(Enum<?> linkedEnum){
		return watcher.getWatchableObjectByte(getId(linkedEnum));
	}
	
	public void setByte(Enum<?> linkedEnum, int newValue){
		watcher.updateObject(getId(linkedEnum),Byte.valueOf((byte)newValue));
	}
	
	// SHORT
	
	public void addShort(Enum<?> linkedEnum, int internalId){
		addObject(linkedEnum,internalId,Short.valueOf((short)0));
	}
	
	public void addShort(Enum<?> linkedEnum, int internalId, int initialValue){
		addObject(linkedEnum,internalId,Short.valueOf((short)initialValue));
	}
	
	public short getShort(Enum<?> linkedEnum){
		return watcher.getWatchableObjectShort(getId(linkedEnum));
	}
	
	public void setShort(Enum<?> linkedEnum, int newValue){
		watcher.updateObject(getId(linkedEnum),Short.valueOf((short)newValue));
	}
	
	// INTEGER
	
	public void addInt(Enum<?> linkedEnum, int internalId){
		addObject(linkedEnum,internalId,Integer.valueOf(0));
	}
	
	public void addInt(Enum<?> linkedEnum, int internalId, int initialValue){
		addObject(linkedEnum,internalId,Integer.valueOf(initialValue));
	}
	
	public int getInt(Enum<?> linkedEnum){
		return watcher.getWatchableObjectInt(getId(linkedEnum));
	}
	
	public void setInt(Enum<?> linkedEnum, int newValue){
		watcher.updateObject(getId(linkedEnum),Integer.valueOf(newValue));
	}
	
	// FLOAT
	
	public void addFloat(Enum<?> linkedEnum, int internalId){
		addObject(linkedEnum,internalId,Float.valueOf(0F));
	}
	
	public void addFloat(Enum<?> linkedEnum, int internalId, float initialValue){
		addObject(linkedEnum,internalId,Float.valueOf(initialValue));
	}
	
	public float getFloat(Enum<?> linkedEnum){
		return watcher.getWatchableObjectFloat(getId(linkedEnum));
	}
	
	public void setFloat(Enum<?> linkedEnum, float newValue){
		watcher.updateObject(getId(linkedEnum),Float.valueOf(newValue));
	}
	
	// STRING
	
	public void addString(Enum<?> linkedEnum, int internalId){
		addObject(linkedEnum,internalId,"");
	}
	
	public void addString(Enum<?> linkedEnum, int internalId, String initialValue){
		addObject(linkedEnum,internalId,initialValue);
	}
	
	public String getString(Enum<?> linkedEnum){
		return watcher.getWatchableObjectString(getId(linkedEnum));
	}
	
	public void setString(Enum<?> linkedEnum, String newValue){
		watcher.updateObject(getId(linkedEnum),newValue);
	}
	
	// ITEMSTACK
	
	public void addItemStack(Enum<?> linkedEnum, int internalId){
		watcher.addObjectByDataType(internalId,5);
		idMap.put(linkedEnum,(byte)internalId);
	}
	
	public void addItemStack(Enum<?> linkedEnum, int internalId, ItemStack initialValue){
		addObject(linkedEnum,internalId,initialValue);
	}
	
	public @Nullable ItemStack getItemStack(Enum<?> linkedEnum){
		return watcher.getWatchableObjectItemStack(getId(linkedEnum));
	}
	
	public void setItemStack(Enum<?> linkedEnum, @Nullable ItemStack newValue){
		watcher.updateObject(getId(linkedEnum),newValue);
	}
}
