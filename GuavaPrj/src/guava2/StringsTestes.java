package guava2;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;

public class StringsTestes {

	
	/*
	 * 
	 * Guava fornece tres classes para facilitar a manipulacao de strings: 
	 * 
	 *  *	CharMatcher (teste3)
	 *		Realiza a substituição de uma determinada sequencia de caracteres por outra passada como param. 
	 *		Ex, alterar os whitespaces, tabs,... por um simples espaco em branco para juntar um texto. 
	 *		CharMatcher.WHITESPACE.collapseFrom(tabsAndSpaces,' '); 
	 *  
	 *  *	Charsets (teste1)
	 *  	
	 *  	Exitem 6 charsets suportados pelo java. Para nos ajudar a obter um deles: 
	 *  	byte[] arrByte = "teste do charset".getBytes(Charsets.UTF_8);	
	 *  
	 *  *	Strings (teste2)
	 * 		
	 * 		Metodos padEnd e padStart ira colocar chars no final e no incio, até
	 * 		que a string fique com o tamanho minimo estipulado na passagem do metodo. 
	 * 		Caso a String ja tenha o tamanho, nao ira fazer nada. 
	 * 		-> padEnd/padStart(String,tamanhominimo,char)
	 * 		-> emptyToNull()  - transforma empty em null
	 * 		-> nullToEmpty() - transformar null em """" (empty)
	 * 		-> isNullOrEmpty() - retorna true se for null ou length ==0
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	
	public static void main(String...args) {
		
		teste1(); 
		teste2();
		teste3(); 
	}
	
	
	private static void teste1() {
		byte[] arr1 = "teste1".getBytes(Charsets.UTF_8) ;
		System.out.println(arr1);
		
		byte[] arr2 = "teste1".getBytes(Charsets.US_ASCII) ;
		System.out.println(arr2);
		
		byte[] arr3 = "teste1".getBytes(Charsets.ISO_8859_1) ;
		System.out.println(arr3);
		
		byte[] arr4 = "teste1".getBytes(Charsets.UTF_16) ;
		System.out.println(arr4);
		
	}
	
	
	private static void teste2() {
		String nome = "fernando"; 
		System.out.println(Strings.padEnd(nome, 10, 'x'));
		System.out.println(Strings.padStart(nome, 10, 'y'));
		
		System.out.println(Strings.nullToEmpty(null));
		System.out.println(Strings.emptyToNull(""));
		System.out.println(Strings.isNullOrEmpty(""));
		
	}
	
	private static void teste3() {
		
		
	}
	
}
