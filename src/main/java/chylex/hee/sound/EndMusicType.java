package chylex.hee.sound;
import net.minecraft.client.audio.MusicTicker.MusicType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum EndMusicType{
	DRAGON_CALM(true), DRAGON_ANGRY(true), EXPLORATION(false);
	
	public final boolean isBossMusic;
	
	EndMusicType(boolean isBossMusic){
		this.isBossMusic = isBossMusic;
	}
	
	public MusicType toMusicType(){
		switch(this){
			case EXPLORATION: return CustomMusicTicker.HEE_END;
			case DRAGON_CALM: return CustomMusicTicker.HEE_END_DRAGON_CALM;
			case DRAGON_ANGRY: return CustomMusicTicker.HEE_END_DRAGON_ANGRY;
			default: return null;
		}
	}
	
	private static EndMusicType cachedType = null;
	private static long lastUpdateMillis;
	
	public static void update(EndMusicType type){
		cachedType = type;
		lastUpdateMillis = System.currentTimeMillis();
	}
	
	public static EndMusicType validateAndGetMusicType(){
		if (cachedType == null || (cachedType.isBossMusic && System.currentTimeMillis()-lastUpdateMillis > 3000))cachedType = EndMusicType.EXPLORATION;
		return cachedType;
	}
}
