package cap4.selectionSorted;

import java.util.Arrays;

public class SelectionSorted {

	
	/*
	 *O selectionsort procura em todo o arr o menor (ou maior) elemento e 
	 *vai substintuindo pelo que esta em foco. 
	 *
	 * Para toda sele��o, ser� comparado com todo o array. Caso seja necess�rios trocar, 
	 * ser� feito apos percorrer o array inteiro
	 * 
	 * Pior Caso: O(n�)
	 * Melhor Caso: O(n�)
	 * Caso Medio: O(n�)
	 *
	 *� um greedy. 
	 *
	 *O primeiro for � para garantir que o numero de itera��es seja igual ao do tamanho do array, 
	 *mas isso nao quer dizer que o elemento eleito como fixo seja na posicao .
	 *
	 *� o algoritmo mais lendo do livro, pois nao aprende nada com as iteracoes, ser� sempre n�
	 *
	 *
	 */

	public static void main(String...args) {
		// int[] teste = new int[]{10,4,1,2,9,3,11,50,34,55,55,6,1}; 
		 int[] teste = new int[]{3,4,1,2}; 
		
		 System.out.println(Arrays.toString(sort1(teste)));  
		
	}
	
	
	public static int[] sort1(int[] arr) {
		
		for(int fixo = 0; fixo < arr.length-1;fixo++) {
			
			int menor = fixo; // seleciona um elemento da lista. 
			
			for(int i = menor +1; i < arr.length; i ++) { // itera o restante da lista (a partir do menor) 
				if(arr[i] < arr[menor]) {
					menor = i; 
				}
			}
			
			if(menor != fixo) {
				int t = arr[fixo]; 
				arr[fixo] = arr[menor]; 
				arr[menor] = t; 
			}
		}
		
		
		return arr; 
		
	}
	
}
