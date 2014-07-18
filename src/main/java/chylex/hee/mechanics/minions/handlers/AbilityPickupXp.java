package chylex.hee.mechanics.minions.handlers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.entity.mob.EntityMobMinion;
import chylex.hee.mechanics.minions.properties.MinionAttributes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AbilityPickupXp extends AbstractAbilityEntityTargeter{
	public AbilityPickupXp(EntityMobMinion minion){
		super(minion,EntityXPOrb.class,2);
	}

	@Override
	protected void onEntityCollision(Entity e){
		EntityXPOrb orb = (EntityXPOrb)e;
		if (orb.field_70532_c != 0)return;
		
		minion.playSound("random.orb",0.1F,0.5F*((rand.nextFloat()-rand.nextFloat())*0.7F+1.8F));
		minion.addExperience(orb.getXpValue());
		orb.setDead();
	}

	@Override
	protected boolean canTargetEntity(Entity e){
		return minion.getExperience() < 200+250*minionData.getAttributeLevel(MinionAttributes.CAPACITY);
	}
	
	@Override
	public void onDeath(){}

	@Override
	public void writeDataToNBT(NBTTagCompound nbt){
		nbt.setInteger("xpVal",minion.getExperience());
	}

	@Override
	public void readDataFromNBT(NBTTagCompound nbt){
		minion.setExperience(nbt.getInteger("xpVal"));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void render(){
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		font.drawString("XP: "+minion.getExperience(),120,1,(255<<24)|(255<<16)|(255<<8)|255);
	}
}
