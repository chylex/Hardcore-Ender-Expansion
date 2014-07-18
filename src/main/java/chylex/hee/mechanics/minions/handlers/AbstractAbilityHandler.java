package chylex.hee.mechanics.minions.handlers;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.entity.mob.EntityMobMinion;
import chylex.hee.mechanics.minions.MinionData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class AbstractAbilityHandler{
	public static final Random rand = new Random();
	
	protected final EntityMobMinion minion;
	protected final MinionData minionData;
	
	public AbstractAbilityHandler(EntityMobMinion minion){
		this.minion = minion;
		this.minionData = minion.getMinionData();
	}
	
	public abstract void onUpdate();
	public abstract void onDeath();
	public abstract void writeDataToNBT(NBTTagCompound nbt);
	public abstract void readDataFromNBT(NBTTagCompound nbt);
	
	@SideOnly(Side.CLIENT)
	public void render(){}
}
