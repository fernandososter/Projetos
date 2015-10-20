package cap4.insertionSort;

import java.util.Arrays;

public class InsertionSort {

	/*
	 * Boa escolha para quando o n for pequeno ou estiver
	 * quase todo ordenado. 
	 * Essas caracteristicas podem variar de acordo com a linguagem .
	 * 
	 * 
	 * A melhor performance ocorre quando o arr esta ordenado e a pior 
	 * quando o arr esta ordenado na ordem inversa. 
	 * 
	 * Na melhor performance, quando todos os elementos estao ordenados, 
	 * a performance é O(n), pois vai ter que iterar a lista mesmo que esteja 
	 * ordenada. 
	 * 
	 * No pior caso e na media, a performance será O(n²), pois será feito outro 
	 * loop quando o arr nao estiver ordenado para procurar a posicao correta. 
	 * 
	 * No insertionsort faz diferença ser ponteiro ou baseado em valor. 
	 * O ponteiro é mais rapido pois ocorre a substituição no valor do array. 
	 * 
	 * O principio dele é pegar um elemento por vez e comparar a um elemento do array. 
	 * Enquanto o elemento do array for maior, ele vai deslocando esse elemento para a
	 * direita. Quando encontra um menor, insere na posicao + 1. 
	 * 
	 * 
	 * itera array original
	 * 		
	 * 		pega o elemento que esta sendo comparado A[i]
	 * 			
	 * 		enquanto A[i] for menor que A[i-1]
	 * 				copia A[i-1] para A[i] //deslocando para direita
	 * 				i -- 
	 * 		
	 * 		copia A[i] para A[i+1]
	 * 
	 * 
	 * */
	
	
	public static void main(String...args) {
		 int[] teste = new int[]{10,4,1,2,9,3,11,50,34,55,55,6,100}; 
		
		
		 System.out.println(Arrays.toString(sort(teste)));  
		
	}
	
	
	public static int[] sort(int[] arr) {
		for(int i = 0; i<= arr.length-1;i++) {
			insert(arr,i,arr[i]); 
		}
		return arr; 
	}

	public static void insert(int[] arr, int pos, int value) {
		int i = pos - 1; 
		while(i >= 0 && arr[i] > value) { // enquanto o ponteiro (arr[i]) for maior que o valor. 
			arr[i+1] = arr[i]; // enquanto o valor for maior que o ponteiro, vai puxando para a direita da lista. 
			i = i-1;  // muda a posicao do ponteiro
		}
		arr[i+1] = value; // quando o ponteiro nao for mais maior que o valor, grava na posicao posteiror ao ponteiro (ja que o i foi diminuido). 
	}
}
