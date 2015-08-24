package guava2;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;



public class PreConditionsTestes {

	
	/*
	 *Preconditions são usadas para fazer verificacao de parametros 
	 *sem precisar escrever um monte de ifs. (Para lancar exceptions)
	 *
	 *Para funcionar, deve ser realizado um import static da classe Preconditions (para carregar os metodos). 
	 *
	 *
	 *Se o objeto abaixo for null, vai gerar a mensgam...
	 *checkNotNull(someObj,"someObj must not be null");
	 * 
	 * checkNotNull(T object, Object message) - se ojb null, Nullpointerexception, 
	 * 		senao retorna o T. 
	 * 
	 * checkElementIndex(int index, int size, Object msg) 
	 * 		checa se o index é maior que o size. se for 
	 * 		IndexOutOfBoundsException, senao retorna o indice. 
	 * 
	 * checkArgument(boolean expression, Object msg) - 
	 * 		o expression é uma operacao logica, se false lança uma 
	 * 		IllegalArgumentException
	 * 
	 * checkState(boolean state, Object msg) para avaliar o estado de 
	 * 		um obj. O state é um metodo que faz uma validacao e retorna
	 * 		true ou false. Se false, IllegalArgumentException.
	 * 
	 */
	
	
	public static void main(String...args) {
		teste1(); 
	}
	
	
	private static void teste1() {
		
		checkNotNull(new Date()); 
		checkElementIndex(2, 20); 
		
		try{
			checkNotNull(null,"objeto é nulo"); 
		} catch(NullPointerException npe) {
			npe.printStackTrace();
		}
		
		try{
			checkElementIndex(30,2,"nao pode."); 
		} catch(IndexOutOfBoundsException iofbo) {
			iofbo.printStackTrace();
		}
		
		try{
			checkArgument(1 ==0, "nao é"); 
		} catch(IllegalArgumentException iae) {
			iae.printStackTrace();
		}
	}
	
	
}
