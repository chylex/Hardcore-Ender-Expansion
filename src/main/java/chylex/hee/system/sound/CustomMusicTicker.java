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
import chylex.hee.system.util.DragonUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CustomMusicTicker extends MusicTicker{
	//private static final MusicTypeWrapper musicTypeCache = new MusicTypeWrapper();
	private static CustomMusicTicker instance;
	
	public static final void stopCurrentMusicAndSetTo(ISound forcedMusic, boolean isJukebox){
		if (instance != null){
			if (instance.currentMusic != null)instance.mc.getSoundHandler().stopSound(instance.currentMusic);
			instance.currentMusic = forcedMusic;
			instance.isJukebox = isJukebox;
		}
	}
	
	public static final MusicType END, END_DRAGON_CALM, END_DRAGON_ANGRY;
	
	static{
		Class[][] musicTypeClasses = new Class[][]{
			{ MusicType.class, ResourceLocation.class, int.class, int.class }
		};

		END = EnumHelper.addEnum(musicTypeClasses,MusicType.class,"HEE_END",new ResourceLocation(""),0,0);
		END_DRAGON_CALM = EnumHelper.addEnum(musicTypeClasses,MusicType.class,"HEE_END_DRAGON_CALM",new ResourceLocation(""),0,0);
		END_DRAGON_ANGRY = EnumHelper.addEnum(musicTypeClasses,MusicType.class,"HEE_END_DRAGON_ANGRY",new ResourceLocation(""),0,0);
	}
		
	private final Minecraft mc;
	private final Random rand;
	private ISound currentMusic;
	private int timeUntilNextMusic = 100;
	private boolean isJukebox = false;
	
	public CustomMusicTicker(Minecraft mc){
		super(mc);
		this.mc = mc;
		this.rand = new Random();
		instance = this;
	}
	
	@Override
	public void update(){
		//updateCurrentMusicType();
		MusicType musicType = getMusicType();

		if (currentMusic != null){
			//ResourceLocation resCache = musicTypeCache.getResource(), resCur = currentMusic.getPositionedSoundLocation();
			
			//if ((!resCache.getResourceDomain().equals(resCur.getResourceDomain()) || !resCache.getResourcePath().equals(resCur.getResourcePath())) && !isJukebox){
			if (!musicType.getMusicTickerLocation().equals(currentMusic.getPositionedSoundLocation())){
				mc.getSoundHandler().stopSound(currentMusic);
				isJukebox = false;DragonUtil.info("stopping old music");
				timeUntilNextMusic = MathHelper.getRandomIntegerInRange(rand,0,musicType.func_148634_b()/2);
			}

			if (!mc.getSoundHandler().isSoundPlaying(currentMusic)){DragonUtil.info("no longer playing "+currentMusic.getPositionedSoundLocation());
				currentMusic = null;
				isJukebox = false;
				timeUntilNextMusic = Math.min(MathHelper.getRandomIntegerInRange(rand,musicType.func_148634_b(),musicType.func_148633_c()),timeUntilNextMusic);
			}
		}

		if (currentMusic == null && timeUntilNextMusic-- <= 0){DragonUtil.info("playing new music");
			currentMusic = PositionedSoundRecord.func_147673_a(musicType.getMusicTickerLocation());DragonUtil.info("... "+currentMusic.getPositionedSoundLocation());
			mc.getSoundHandler().playSound(currentMusic);
			timeUntilNextMusic = Integer.MAX_VALUE;
		}
	}
	
	private MusicType getMusicType(){
		MusicType suggestedType = mc.func_147109_W();
		
		if (suggestedType == MusicType.END || suggestedType == MusicType.END_BOSS){
			if (BossStatus.statusBarTime > 0){
				switch(BossType.validateAndGetBossType()){
					case DRAGON_CALM: suggestedType = END_DRAGON_CALM; break;
					case DRAGON_ANGRY: suggestedType = END_DRAGON_ANGRY; break;
					default:
				}
			}
			
			if (suggestedType == null)suggestedType = END;
		}
		
		return suggestedType;
		/*musicTypeCache.reset();
		
		MusicType suggestedType = mc.func_147109_W();
		String resName = "";
		
		if (suggestedType == MusicType.END){
			musicTypeCache.setTime(8400,16800);
			resName = "random";
		}
		else if (BossStatus.statusBarTime > 0){
			switch(BossType.validateAndGetBossType()){
				case DRAGON_CALM: resName = "dragoncalm"; break;
				case DRAGON_ANGRY: resName = "dragonangry"; break;
				case ENDER_EYE: resName = "tower"; break;
				case FIRE_FIEND: resName = "firefiend"; break;
				case ENDER_DEMON: resName = "enderdemon"; break;
				default: musicTypeCache.setMusicType(suggestedType);
			}
		}
		else{
			musicTypeCache.setMusicType(suggestedType);
			return;
		}
		
		if (MusicManager.hasPoolAnyTracks(resName))musicTypeCache.setCustomResource(new ResourceLocation("hee~music:"+resName+".ogg"));
		else musicTypeCache.setMusicType(suggestedType);*/
	}
}
