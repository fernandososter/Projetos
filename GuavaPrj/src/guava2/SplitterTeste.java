package guava2;

import java.util.Map;

import com.google.common.base.Splitter;

public class SplitterTeste {

	/*
	 * teste1
	 * No Splitter.on() pode ser passado um separador ou expressao regular. 
	 * Splitter.on("|").split(lst) vai separar a lista com | 
	 * Splitter.on("\\d+").split(lsit) vai separar a lista em letras. 
	 * 
	 * teste2
	 * Assim como no joiner, no splitter tem a funcao que trabalha com map. 
	 * Com o splitter: 
	 * 	Splitter.MapSplitter .. = Splitter.on("#").withKeyValueSeparator("=")
	 *  e aplicar o split() vai retornar um map. 
	 */
	
	public static void main(String...args) {
		teste1(); 
		teste2(); 
	}

	
	private static void teste1() {
	
		Iterable<String> list = Splitter.on("|").split("1|2||4|5|%"); 
		Iterable<String> list2 = Splitter.on("\\d+").split("1|2||4|5|%"); 
		System.out.println(list);
		System.out.println(list2);
		
		Splitter st3 = Splitter.on("|").trimResults(); 
		Iterable<String> list3 = st3.split("1|2||4|5|%|"); 
		System.out.println(list3);
		
	}
	
	private static void teste2() {
		
		String str = "1=um#2=dois#3=tres";
		Splitter.MapSplitter mapSplitter = Splitter.on("#").withKeyValueSeparator("="); 
		Map<String,String> map = mapSplitter.split(str); 
		System.out.println(map);
	}
	
	
	
	
}
