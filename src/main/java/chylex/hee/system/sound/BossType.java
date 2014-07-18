package chylex.hee.system.sound;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum BossType{
	DRAGON_CALM, DRAGON_ANGRY, ENDER_EYE, FIRE_FIEND, ENDER_DEMON, NONE;
	
	private static BossType currentBoss = NONE;
	private static long lastUpdateMillis;
	
	public static void update(BossType type){
		currentBoss = type;
		lastUpdateMillis = System.currentTimeMillis();
	}
	
	public static BossType validateAndGetBossType(){
		if (System.currentTimeMillis() - lastUpdateMillis > 3000) currentBoss = NONE;
		return currentBoss;
	}
}
