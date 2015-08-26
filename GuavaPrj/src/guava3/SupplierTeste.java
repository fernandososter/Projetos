package guava3;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterables;

public class SupplierTeste {

	
	

	
	
	public static void main(String...args) {
	
		
		
		
		Supplier<List<Integer>> supplier = new Supplier<List<Integer>>() 
		{
			private List<Integer> numbers = null; 
			public List<Integer> get() {
				if(numbers == null) {
					numbers = Arrays.asList(1,2,3,4,5,6,7,8,9); 
				}
				return numbers; 
			}
		}; 
		Supplier<String>  mapSupp = new Supplier<String>() {
			String str = "1=um#2=dois#3=tres#4=quatro#5=cinco#6=seis#7=sete#8=oito#9=nove"; 
			@Override
			public String get() {
				return str; 
			}
		}; 
		
		
		
		Predicate<Integer> evenPredicate = new Predicate<Integer>() {
			@Override
			public boolean apply(Integer input) {
				return (input%2) == 0; 
			}
		}; 
		while(true) {
			
			List<Integer> lista = Suppliers.memoize(supplier).get();  
			System.out.println(Joiner.on("#").join(Iterables.filter(lista, evenPredicate)));
			System.out.println(Joiner.on("#").join(Iterables.filter(lista, Predicates.not(evenPredicate))));
			
			System.out.println(
					Functions.forMap(
							Splitter.on("#").withKeyValueSeparator("=").split(
										mapSupp.get())).apply("2"));
			
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	
}
