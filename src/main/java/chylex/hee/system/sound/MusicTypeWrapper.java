package chylex.hee.system.sound;
import net.minecraft.client.audio.MusicTicker.MusicType;
import net.minecraft.util.ResourceLocation;

class MusicTypeWrapper{
	private MusicType parentMusicType;
	private ResourceLocation customResource;
	private int minTime,maxTime;
	
	public void setMusicType(MusicType type){
		this.parentMusicType = type;
		this.customResource = null;
	}
	
	public void setCustomResource(ResourceLocation location){
		this.customResource = location;
		this.parentMusicType = null;
	}
	
	public void setTime(int minTime, int maxTime){
		this.minTime = minTime;
		this.maxTime = maxTime;
	}
	
	public ResourceLocation getResource(){
		return parentMusicType == null ? customResource : parentMusicType.getMusicTickerLocation();
	}
	
	public int getMinTime(){
		return parentMusicType != null ? parentMusicType.func_148634_b() : minTime;
	}
	
	public int getMaxTime(){
		return parentMusicType != null ? parentMusicType.func_148633_c() : maxTime;
	}
	
	public void reset(){
		parentMusicType = null;
		customResource = null;
		minTime = maxTime = 0;
	}
}
