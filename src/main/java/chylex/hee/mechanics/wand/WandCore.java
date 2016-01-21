package chylex.hee.mechanics.wand;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import org.apache.commons.lang3.EnumUtils;
import chylex.hee.system.abstractions.nbt.NBT;

public enum WandCore{
	INFESTATION, BLAZE, MAGIC, DEXTERITY, FORCE, REPULSION;
	
	public static WandCore[] values = values();
	
	/**
	 * Returns an array of cores sorted based on slots (there can be null elements).
	 */
	public static List<WandCore> getCores(ItemStack is){
		return NBT.item(is,false).getList("wandcores").readStrings().map(name -> EnumUtils.getEnum(WandCore.class,name)).filter(Objects::nonNull).collect(Collectors.toList());
	}
	
	public static boolean hasCore(ItemStack is, WandCore core){
		final String name = core.name();
		return NBT.item(is,false).getList("wandcores").readStrings().anyMatch(name::equals);
	}
	
	public static void setCores(ItemStack is, WandCore[] cores){
		NBT.item(is,true).writeList("wandcores",Arrays.stream(cores).filter(Objects::nonNull).map(WandCore::name).map(NBTTagString::new));
	}
}
