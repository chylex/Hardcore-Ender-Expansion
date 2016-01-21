package chylex.hee.system.abstractions.nbt;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public final class NBT{
	private static final NBTCompound dummyTag = new NBTCompound(new NBTTagCompoundDummy());
	
	public static NBTCompound wrap(NBTTagCompound tag){
		return new NBTCompound(tag);
	}
	
	public static NBTCompound item(ItemStack is, boolean create){
		NBTTagCompound tag = is.getTagCompound();
		
		if (tag == null){
			if (create)is.setTagCompound(tag = new NBTTagCompound());
			else return dummyTag;
		}
		
		return new NBTCompound(tag);
	}
	
	public static NBTCompound callback(Runnable callback){
		return new NBTCompound(new NBTTagCompoundCallback(callback));
	}
	
	public static interface IStringIntConsumer{ void accept(String key, int value); }
	public static interface IStringLongConsumer{ void accept(String key, long value); }
	
	private static final class NBTTagCompoundDummy extends NBTTagCompound{
		NBTTagCompoundDummy(){}
		
		@Override public void setBoolean(String key, boolean value){}
		@Override public void setByte(String key, byte value){}
		@Override public void setByteArray(String key, byte[] value){}
		@Override public void setDouble(String key, double value){}
		@Override public void setFloat(String key, float value){}
		@Override public void setIntArray(String key, int[] value){}
		@Override public void setInteger(String key, int value){}
		@Override public void setLong(String key, long value){}
		@Override public void setShort(String key, short value){}
		@Override public void setString(String key, String value){}
		@Override public void setTag(String key, NBTBase value){}
		@Override public void removeTag(String key){}
	}
	
	private static final class NBTTagCompoundCallback extends NBTTagCompound{
		private final Runnable callback;
		
		NBTTagCompoundCallback(Runnable callback){
			this.callback = callback;
		}
		
		@Override public void setTag(String key, NBTBase value){ super.setTag(key,value); callback.run(); }
		@Override public void setByte(String key, byte value){ super.setByte(key,value); callback.run(); }
		@Override public void setShort(String key, short value){ super.setShort(key,value); callback.run(); }
		@Override public void setInteger(String key, int value){ super.setInteger(key,value); callback.run(); }
		@Override public void setLong(String key, long value){ super.setLong(key,value); callback.run(); }
		@Override public void setFloat(String key, float value){ super.setFloat(key,value); callback.run(); }
		@Override public void setDouble(String key, double value){ super.setDouble(key,value); callback.run(); }
		@Override public void setString(String key, String value){ super.setString(key,value); callback.run(); }
		@Override public void setByteArray(String key, byte[] value){ super.setByteArray(key,value); callback.run(); }
		@Override public void setIntArray(String key, int[] value){ super.setIntArray(key,value); callback.run(); }
	}
	
	private NBT(){}
}
