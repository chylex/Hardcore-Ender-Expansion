package chylex.hee.world.structure.island.biome.data;
import chylex.hee.system.weight.IWeightProvider;

public final class BiomeContentVariation implements IWeightProvider{
	private final short weight;
	
	public BiomeContentVariation(int weight){
		this.weight = (short)weight;
	}
	
	@Override
	public short getWeight(){
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
