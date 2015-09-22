package chylex.hee.system.util;

public class BooleanByte{
	public static BooleanByte of(boolean...array){
		BooleanByte bb = new BooleanByte();
		
		for(int index = 0; index < array.length; index++){
			if (array[index])bb.set(index);
		}
		
		return bb;
	}
	
	private byte data;
	
	public BooleanByte(){}
	
	public BooleanByte(byte data){
		this.data = data;
	}
	
	public void set(int index){
		if (index < 0 || index >= 8)throw new IllegalArgumentException("Index must be between 0 and 7!");
		data |= 1 << index;
	}
	
	public boolean get(int index){
		if (index < 0 || index >= 8)throw new IllegalArgumentException("Index must be between 0 and 7!");
		return (data & (1 << index)) != 0;
	}
	
	public byte toByte(){
		return data;
	}
	
	@Override
	public int hashCode(){
		return data;
	}
	
	@Override
	public boolean equals(Object obj){
		return obj instanceof BooleanByte && ((BooleanByte)obj).data == data;
	}
}
