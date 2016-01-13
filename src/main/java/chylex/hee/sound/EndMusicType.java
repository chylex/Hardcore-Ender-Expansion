package chylex.hee.sound;
import java.util.Random;
import net.minecraft.client.audio.MusicTicker.MusicType;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum EndMusicType{
	EXPLORATION("music.game.end", 3600, 8400),
	DRAGON_CALM("music.game.end.dragoncalm"),
	DRAGON_ANGRY("music.game.end.dragonangry");
	
	// TODO public final boolean isBossMusic;
	private final MusicType type;
	
	EndMusicType(String resourceName){
		this(resourceName,0,0);
	}
	
	EndMusicType(String resourceName, int minDelay, int maxDelay){
		this.type = createMusicType(this,resourceName,minDelay,maxDelay);
	}
	
	public int getTimer(Random rand){
		int min = type.func_148634_b(), max = type.func_148633_c();
		return min >= max ? max : rand.nextInt(max-min+1)+min;
	}
	
	public int getPriority(){
		return -ordinal();
	}
	
	public PositionedSoundRecord getPositionedSoundRecord(){
		return PositionedSoundRecord.func_147673_a(type.getMusicTickerLocation());
	}
	
	private static final Class[][] musicTypeClasses = new Class[][]{{ MusicType.class, ResourceLocation.class, int.class, int.class }};
	
	private static final MusicType createMusicType(EndMusicType parent, String resourceName, int minDelay, int maxDelay){
		return EnumHelper.addEnum(musicTypeClasses,MusicType.class,"HEE_"+parent.name(),new ResourceLocation("hardcoreenderexpansion",resourceName),minDelay,maxDelay);
	}
	
	private static EndMusicType cachedType = null;
	private static long lastUpdateMillis;
	
	public static void update(EndMusicType type){
		cachedType = type;
		lastUpdateMillis = System.currentTimeMillis();
	}
	
	public static EndMusicType validateAndGetMusicType(){
		if (cachedType == null || (cachedType != EXPLORATION && System.currentTimeMillis()-lastUpdateMillis > 3000))cachedType = EXPLORATION;
		return cachedType;
	}
}
