package chylex.hee.world.structure.island.biome.data;

public final class BiomeRandomDeviation{
	private final BiomeContentVariation[] compatibleVariations;
	
	public BiomeRandomDeviation(BiomeContentVariation...compatibleVariations){
		this.compatibleVariations = compatibleVariations;
	}
	
	public boolean isCompatible(BiomeContentVariation variation){
		for(BiomeContentVariation compatibleVariation:compatibleVariations){
			if (compatibleVariation == variation)return true;
		}
		
		return false;
	}
	
	@Override
	public boolean equals(Object o){
		return o == this;
	}
	
	@Override
	public int hashCode(){
		return System.identityHashCode(this);
	}
}
