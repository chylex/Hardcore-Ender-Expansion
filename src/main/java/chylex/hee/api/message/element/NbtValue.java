package chylex.hee.api.message.element;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;
import chylex.hee.api.message.element.base.Precondition;

public class NbtValue extends Precondition<NBTTagCompound>{
	public static final NbtValue any(){
		return new NbtValue();
	}
	
	private NbtValue(){}
	
	@Override
	public boolean checkType(NBTBase tag){
		return tag != null && tag.getId() == NBT.TAG_COMPOUND;
	}

	@Override
	public boolean checkValue(NBTBase tag){
		return true;
	}

	@Override
	public NBTTagCompound getValue(NBTBase tag){
		return (NBTTagCompound)tag;
	}
}
