package chylex.hee.system.savedata;
import net.minecraft.entity.player.EntityPlayer;

public class InfestationSavefile extends Savefile{
	private byte saveLimiter = 0;
	
	public InfestationSavefile(WorldData worldData){
		super(worldData,"infestation.nbt");
	}

	public void increaseInfestationPower(EntityPlayer player){
		nbt.setInteger(player.getCommandSenderName()+"_pow",getInfestationPower(player)+1);
		nbt.setByte(player.getCommandSenderName()+"_tim",(byte)8);
		
		if (++saveLimiter > 120){
			save();
			saveLimiter = 0;
		}
	}
	
	public int getInfestationPower(EntityPlayer player){
		return nbt.getInteger(player.getCommandSenderName()+"_pow");
	}
	
	public void resetInfestation(EntityPlayer player){
		nbt.removeTag(player.getCommandSenderName()+"_pow");
		nbt.removeTag(player.getCommandSenderName()+"_tim");
		save();
	}
	
	public byte decreaseInfestationStartTimer(EntityPlayer player){
		byte b = getInfestationStartTimer(player);
		if (b-- == 0)return 0;
		nbt.setByte(player.getCommandSenderName()+"_tim",b);
		
		if (++saveLimiter > 120){
			save();
			saveLimiter = 0;
		}
		
		return b;
	}
	
	public byte getInfestationStartTimer(EntityPlayer player){
		return nbt.getByte(player.getCommandSenderName()+"_tim");
	}
}
