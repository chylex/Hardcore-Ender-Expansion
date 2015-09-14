package chylex.hee.block.sound;
import net.minecraft.block.Block.SoundType;

public class SoundTypeSingle extends SoundType{
	public SoundTypeSingle(String soundName, float volume, float pitch){
		super(soundName,volume,pitch);
	}
	
	@Override
	public String getBreakSound(){
		return soundName;
	}

    @Override
	public String getStepResourcePath(){
        return soundName;
    }

    @Override
	public String func_150496_b(){ // OBFUSCATED place block sound
        return soundName;
    }
}
