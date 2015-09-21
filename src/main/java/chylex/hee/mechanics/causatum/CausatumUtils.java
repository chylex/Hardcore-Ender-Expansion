package chylex.hee.mechanics.causatum;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import chylex.hee.game.savedata.WorldDataHandler;
import chylex.hee.game.savedata.types.CausatumSavefile;

public class CausatumUtils{
	public static void increase(DamageSource source, CausatumMeters meter, float amount){
		if (!(source.getEntity() instanceof EntityPlayer))return;
		WorldDataHandler.<CausatumSavefile>get(CausatumSavefile.class).increaseLevel(((EntityPlayer)source.getEntity()).getUniqueID(),meter,amount);
	}
	
	public static void increase(EntityPlayer player, CausatumMeters meter, float amount){
		WorldDataHandler.<CausatumSavefile>get(CausatumSavefile.class).increaseLevel(player.getUniqueID(),meter,amount);
	}
	
	public static void increase(UUID id, CausatumMeters meter, float amount){
		WorldDataHandler.<CausatumSavefile>get(CausatumSavefile.class).increaseLevel(id,meter,amount);
	}
}
