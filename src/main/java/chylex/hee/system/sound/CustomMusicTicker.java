package chylex.hee.system.sound;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class CustomMusicTicker extends MusicTicker{
	private static final CustomMusicTicker instance = new CustomMusicTicker(Minecraft.getMinecraft());
	
	public static CustomMusicTicker getInstance(){
		return instance;
	}
	
	public static void stopMusicAndPlayJukebox(ISound music){
		if (instance.currentMusic != null){
			instance.mc.getSoundHandler().stopSound(instance.currentMusic);
			instance.timeUntilNextMusic = 100;
		}
		
		instance.currentMusic = music;
		instance.mc.getSoundHandler().playSound(music);
		instance.isJukebox = true;
	}
	
	public static final MusicType HEE_END, HEE_END_DRAGON_CALM, HEE_END_DRAGON_ANGRY;
	
	static{
		Class[][] enumClasses = new Class[][]{
			{ MusicType.class, ResourceLocation.class, int.class, int.class }
		};

		HEE_END = EnumHelper.addEnum(enumClasses, MusicType.class, "HEE_END", new ResourceLocation("hardcoreenderexpansion:music.game.end"), 6000, 10000);
		HEE_END_DRAGON_CALM = EnumHelper.addEnum(enumClasses, MusicType.class, "HEE_END_DRAGON_CALM", new ResourceLocation("hardcoreenderexpansion:music.game.end.dragoncalm"), 0, 0);
		HEE_END_DRAGON_ANGRY = EnumHelper.addEnum(enumClasses, MusicType.class, "HEE_END_DRAGON_ANGRY", new ResourceLocation("hardcoreenderexpansion:music.game.end.dragonangry"), 0, 0);
	}
	
	private final Minecraft mc;
	private final Random rand;
	private ISound currentMusic;
	private int timeUntilNextMusic = 100;
	private boolean isJukebox = false;
	
	private CustomMusicTicker(Minecraft mc){
		super(mc);
		this.mc = mc;
		this.rand = new Random();
	}
	
	@Override
	public void update(){
		MusicType type = getCurrentMusicType();

		if (currentMusic != null){
			if (!type.getMusicTickerLocation().equals(currentMusic.getPositionedSoundLocation()) && !isJukebox){
				mc.getSoundHandler().stopSound(currentMusic);
				timeUntilNextMusic = MathHelper.getRandomIntegerInRange(rand,0,type.func_148634_b()/2);
			}

			if (!mc.getSoundHandler().isSoundPlaying(currentMusic)){
				currentMusic = null;
				timeUntilNextMusic = Math.min(MathHelper.getRandomIntegerInRange(rand,type.func_148634_b(),type.func_148633_c()),timeUntilNextMusic);
			}
		}

		if (currentMusic == null && timeUntilNextMusic-- <= 0){
			currentMusic = PositionedSoundRecord.func_147673_a(type.getMusicTickerLocation());
			mc.getSoundHandler().playSound(currentMusic);
			timeUntilNextMusic = Integer.MAX_VALUE;
			isJukebox = false;
		}
	}
	
	private MusicType getCurrentMusicType(){
		MusicType suggestedType = mc.func_147109_W();
		
		if (suggestedType == MusicType.END || suggestedType == MusicType.END_BOSS){
			if (BossStatus.statusBarTime > 0){
				switch(BossType.validateAndGetBossType()){
					case DRAGON_CALM: return HEE_END_DRAGON_CALM;
					case DRAGON_ANGRY: return HEE_END_DRAGON_ANGRY;
					default:
				}
			}
			
			return HEE_END;
		}
		else return suggestedType;
	}
}
