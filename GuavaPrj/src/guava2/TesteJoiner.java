package guava2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.google.common.base.Joiner;

public class TesteJoiner {

	/*
	 * O joiner serve para juntar informacoes de uma collection 
	 * em uma string, passando um separador como parametros no Joiner.on(). 
	 * 
	 * O guava usado principio de programacao funcional.
	 * 
	 * Joiner.on("\").join(lista)
	 * 
	 * Quando tiver nulls e quisermos pular, 
	 * Joiner.on().skipNulls().join(lista); 
	 * 
	 * Podemos tambem colocar outro valor quando for null, useForNull(STR). 
	 * Joiner.on().useForNull("").join(lista);
	 * 
	 * O joiner usa o metodo toString() do objeto, por isso, podemos passar 
	 * qualquer objeto como parametro. 
	 * 
	 * Quando pegamos a instancia do Joiner, podemos chamar uma serie de metodos. 
	 * Entre eles, um que faz um append do resultado em um StringBuilder ou 
	 * em uma Interface Appendable. - appendTo(). 
	 * 
	 * 
	 * */
	
	
	
	public static void main(String[] args) throws IOException {

		File f = new File("/tmp/VIVO_201408_C_SMS_20140923110182.csv"); 
		String arqContent = ler(f); 
		
		testJoiner1(); 
		
		testJoiner2();  
	}

	private static void testJoiner1() {
		
		Integer[] lst = new Integer[] {1,2,3,8,100}; 
		// Joiner normal. Se tiver um null ira dar NullPointerExceptin 
		System.out.println(Joiner.on('|').join(lst)); 
		
		lst = new Integer[] {1,2,3,8,null,100}; 
		// Joiner com skipNulls()
		System.out.println(Joiner.on('|').skipNulls().join(lst)); 
		// Joiner com useForNull()
		System.out.println(Joiner.on('|').useForNull("nulo aqui").join(lst)); 
		
		
		File[] fArr = new File[] {new File("/tmp/f.txt"),new File("/tmp/f1.txt"),new File("/tmp/f2.txt")}; 
		System.out.println(Joiner.on("#").join(fArr));
		
		StringBuilder sb = new StringBuilder(); 
		Joiner j = Joiner.on("#"); 
		j.skipNulls().appendTo(sb, lst); 
		System.out.println(sb.toString());
		
		
	}
	
	
	
	private static void testJoiner2() {
		
		
		
		
		
	}
	
	
	
	private static String ler(File f) throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader(f)); 
		StringBuilder sb = new StringBuilder();
		
		String r = null; 
		while ( ( r = br.readLine() ) != null) {
			sb.append(r); 
		}
		return sb.toString();
		
		
	}
	
	
	
}
