package chylex.hee.world.structure.island.biome.data;
import org.apache.commons.lang3.ArrayUtils;

public final class IslandBiomeData{
	public final BiomeContentVariation content;
	private final BiomeRandomDeviation[] deviations;
	
	public IslandBiomeData(BiomeContentVariation content, BiomeRandomDeviation[] deviations){
		this.content = content;
		this.deviations = deviations;
	}
	
	public boolean hasDeviation(BiomeRandomDeviation deviation){
		for(BiomeRandomDeviation obj:deviations){
			if (obj == deviation)return true;
		}
		
		return false;
	}
	
	public String getDeviationsAsString(){
		return ArrayUtils.toString(deviations);
	}
}
