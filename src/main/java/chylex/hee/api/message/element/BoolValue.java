package chylex.hee.api.message.element;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTBase.NBTPrimitive;
import net.minecraftforge.common.util.Constants.NBT;
import chylex.hee.api.message.element.base.Precondition;

public class BoolValue extends Precondition<Boolean>{
	public static final BoolValue any(){
		return new BoolValue();
	}
	
	private BoolValue(){}
	
	@Override
	public boolean checkType(NBTBase tag){
		return tag != null && tag.getId() == NBT.TAG_BYTE;
	}

	@Override
	public boolean checkValue(NBTBase tag){
		return true;
	}

	@Override
	public Boolean getValue(NBTBase tag){
		return Boolean.valueOf(((NBTPrimitive)tag).func_150290_f() == 1);
	}
}
