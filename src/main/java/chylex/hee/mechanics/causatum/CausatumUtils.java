package chylex.hee.mechanics.causatum;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import chylex.hee.game.save.SaveData;
import chylex.hee.game.save.types.global.CausatumFile;

public class CausatumUtils{
	public static void increase(DamageSource source, CausatumMeters meter, float amount){
		if (!(source.getEntity() instanceof EntityPlayer))return;
		SaveData.<CausatumFile>global(CausatumFile.class).increaseLevel(((EntityPlayer)source.getEntity()).getUniqueID(),meter,amount);
	}
	
	public static void increase(EntityPlayer player, CausatumMeters meter, float amount){
		SaveData.<CausatumFile>global(CausatumFile.class).increaseLevel(player.getUniqueID(),meter,amount);
	}
	
	public static void increase(UUID id, CausatumMeters meter, float amount){
		SaveData.<CausatumFile>global(CausatumFile.class).increaseLevel(id,meter,amount);
	}
}
