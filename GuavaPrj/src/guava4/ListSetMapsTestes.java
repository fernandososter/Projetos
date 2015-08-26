package guava4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;


class Book {
	String name; 
	String isbn; 
	
	Book(String name, String isbn) {
		this.name = name;
		this.isbn = isbn; 
	}
		
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("name", this.name).toString(); 
	}
}


class FornecedorLivros implements Supplier<List<Book>> {
	
	private static List<Book> lst = new ArrayList<Book>(); 
	static{
		lst.add(new Book("1","1")); 
		lst.add(new Book("2","2")); 
		lst.add(new Book("3","3")); 
		lst.add(new Book("4","4")); 
	}
	
	public List<Book> get() {
		return lst; 
	}
}


public class ListSetMapsTestes {

	
	public static void main(String...args) {
		listTeste(); 
		setTestes();
		mapTestes(); 
		arrayListMultimapTestes(); 
	}
	
	
	
	private static void listTeste() {
		
		List<Integer> l = Arrays.asList(1,2,3,4,5,6,7,8,9) ;
		/*particiona a lista em numero de elementos*/
		System.out.println(Lists.partition(l, 3)); 
		System.out.println(Lists.partition(l, 8)); 
		
	}
	
	
	private static void setTestes() {
		
		HashSet<Integer> s1 = Sets.newHashSet(1,2,3,5,6,7); 
		HashSet<Integer> s2 = Sets.newHashSet(1,2,3,4,5,6); 
		HashSet<Integer> s3 = Sets.newHashSet(11,22,33,44,55,66);
		
		
		SetView<Integer> v = Sets.difference(s1, s2); 
		System.out.println(v);
		
		SetView<Integer> vInverse = Sets.difference(s2, s1); 
		System.out.println(vInverse);
	
		
		Set<List<Integer>> pCartesiano = Sets.cartesianProduct(s1,s2,s3);
		System.out.println(pCartesiano);
		
		//
		System.out.println(Sets.symmetricDifference(s1, s2));
		System.out.println(Sets.union(s1, s2));
		System.out.println(Sets.intersection(s1, s2));
		
		
	}

	
	private static void mapTestes() {
		
		
		/*O uniqueIndex faz a transformacao de list/iterable para map, usando uma chave
		 * especificada em uma function.*/
		List<Book> lista =  Suppliers.memoize(new FornecedorLivros()).get(); 
		ImmutableMap mapRes = Maps.uniqueIndex(lista, new Function<Book,String>() {
			@Override
			public String apply(Book input) {
				return input.isbn;
			}
		}); 
		
		System.out.println(mapRes);
		
		// o retorno é atualizado se o objeto origianl for atualizado
	
		Map<Book,String> map2 = 
		Maps.asMap(Sets.newHashSet(lista), new Function<Book,String>() {
			@Override
			public String apply(Book input) {
				return input.isbn;
			}
		}); 
		
		System.out.println(map2);
		
		// retornar um ImmutableMap que nao é atualizado se o map original alterar. 
		//Maps.toMap(keys, valueFunction)
		
	}
	
	private static void arrayListMultimapTestes() {
		
		ArrayListMultimap<String, String> m = ArrayListMultimap.create(); 
		
		m.put("foo","1"); 
		m.put("foo","2"); 
		m.put("foo","3"); 
		m.put("abc","5"); 
		m.put("abc","6"); 
		m.put("abc","7"); 
		m.put("zoo",""); 
		
		System.out.println(m.get("foo")); 
	
		
		
		
	}
	
}
