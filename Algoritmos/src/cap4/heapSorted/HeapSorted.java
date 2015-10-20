package cap4.heapSorted;

import java.util.Arrays;

public class HeapSorted {

	
	/*
	 * 
	 * O heapsort usa a tatica das elimatorias de futebol para comparacao. 
	 * Por exemplo, 64 times, terão 6 vitorias até o final: 
	 * 	
	 * 	log(64) = 6 
	 * 
	 * Best: log(n log n) wiki ( - O(n lg n) -  )
	 * worst: log(n log n) wiki ( - 2n log n + O(n) - )
	 * avarage: log(n log n) 
	 * 
	 * ** N log n quer dizer que o processamento será quebrado em dois dentro do mesmo 
	 * metodo, mas que o arr sera varrido inteiro. 
	 * 
	 * Algoritmo nao estavel (em caso de igualdade, nao mantem a formacao original). 
	 */

	public static void main(String...args) {
		 int[] teste = new int[]{40,90,20,10,50,70,80}; 
		 System.out.println(Arrays.toString(heapSort(teste)));  
		
	}

	
	public static int[] heapSort(int[] v) {
		buildMaxHeap(v);
		int n = v.length; 
		for(int i = v.length-1; i>0;i--){
			swap(v,i,0); 
			maxHeapify(v, 0, --n);
		}
		return v; 
	}
	
	private static void buildMaxHeap(int[] v) {
		 System.out.println(Arrays.toString(v));  
		for(int i = v.length/2-1; i >= 0; i--) {
			maxHeapify(v,i,v.length); 
			System.out.println(Arrays.toString(v));  
		}
	}
	
	private static void maxHeapify(int[] v, int pos, int size) {
		int maxi; 
		int left = 2 * pos; 
		int right = 2 * pos +1; 
		
		if( (left < size) && (v[left] > v[pos])) {
			maxi = left; 
		} else {
			maxi = pos; 
		}
		
		if(right < size && v[right] > v[maxi]) {
			maxi = right; 
		}
	
		if(maxi != pos) {
			System.out.println(maxi + "<->" + pos);
			swap(v,pos,maxi); 
			maxHeapify(v,maxi,size); 
		}
		
	}
	
	
	public static void swap(int[] v, int j, int aposJ) {
		int aux = v[j]; 
		v[j] = v[aposJ]; 
		v[aposJ] = aux;
	}
}
