package chylex.hee.world.structure.island.biome.data;
import chylex.hee.system.weight.IWeightProvider;

public final class BiomeContentVariation implements IWeightProvider{
	private final byte weight;
	
	public BiomeContentVariation(int weight){
		this.weight = (byte)weight;
	}
	
	@Override
	public int getWeight(){
		return weight;
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
