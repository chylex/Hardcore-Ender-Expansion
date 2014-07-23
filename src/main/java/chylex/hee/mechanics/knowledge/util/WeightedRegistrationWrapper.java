package chylex.hee.mechanics.knowledge.util;
import chylex.hee.mechanics.knowledge.data.KnowledgeRegistration;
import chylex.hee.system.weight.IWeightProvider;

public class WeightedRegistrationWrapper implements IWeightProvider{
	private final KnowledgeRegistration registration;
	private final byte weight;
	
	WeightedRegistrationWrapper(KnowledgeRegistration registration, int weight){
		this.registration = registration;
		this.weight = (byte)weight;
	}
	
	public KnowledgeRegistration getRegistration(){
		return registration;
	}
	
	@Override
	public int getWeight(){
		return weight;
	}
}
