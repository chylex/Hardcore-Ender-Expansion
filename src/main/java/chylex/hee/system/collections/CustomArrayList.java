package chylex.hee.system.collections;
import java.util.ArrayList;

public class CustomArrayList<T> extends ArrayList<T>{
	public boolean addAll(T[] objArray){
		for(T obj:objArray)super.add(obj);
		return true;
	}
}
