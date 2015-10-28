package cap4.quickSort;

import java.util.Arrays;

public class QuickSort {

	
	/*
	 * Pior caso: O(n²)
	 * Melhor caso: n log n
	 * Medio: n log n
	 * 
	 * 
	 * 
	 */
	
	public static void main(String...args) {
		 int[] teste = new int[]{10,4,1,2,9,3,11,50,34,55,55,6,1}; 
		 System.out.println(Arrays.toString(ordenar(teste)));  
		
	}

	public static int[] ordenar(int[] vetor) {
		ordenar(vetor,0, vetor.length-1); 
		return vetor; 
	}
	
	private static void ordenar(int[] vetor, int inicio, int fim) {
		if(inicio < fim) {
			int posicaoPivo = separar(vetor,inicio,fim); 
			ordenar(vetor,inicio,posicaoPivo -1); 
			ordenar(vetor,posicaoPivo + 1, fim); 
		}
	}
	
	private static int separar(int[] vetor, int inicio, int fim) {
		
		int pivo = vetor[inicio]; 
		int i = inicio+1; 
		int f = fim; 
		
		while(i <= f) {
			
			if(vetor[i] <= pivo) {
				i++; 
			} else if (pivo < vetor[f]) {
				f--; 
			} else {
				int troca = vetor[i]; 
				vetor[i] = vetor[f];
				vetor[f] = troca; 
				i++;
				f--; 
			}
		}
		vetor[inicio] = vetor[f]; 
		vetor[f] = pivo; 
		return f; 
	}
}
