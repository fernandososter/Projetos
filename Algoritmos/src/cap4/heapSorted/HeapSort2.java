package cap4.heapSorted;

import java.util.Arrays;

public class HeapSort2 {

	
	public static void main(String[] args) {
		
		 int[] teste = new int[]{40,90,20,10,50,70,80}; 
		 heapSort(teste); 
		
	}
	
	public static void heapSort(int[] v) {
		buildMaxHeap(v);
		int n = v.length;
		for(int i =v.length-1; i>0; i--) {
			swap(v,i,0); 
			heapfy(v,0,--n); 
		}
		
			
	}
	
	public static void buildMaxHeap(int[] v) {
		
		for(int i= v.length/2-1; i>= 0; i--) {
			System.out.println("---->>>> " + i);
			heapfy(v,i,v.length); 
		}
	}
	
	public static void heapfy(int[] v, int pos, int size) {
		int maxi; 
		int left = 2*pos; 
		int right = 2*pos+1; 
		
		System.out.println("l:" + left + " r:" + 
		right + " size: " + size + " pos:"+pos);
		System.out.println(Arrays.toString(v));
		
		System.out.println("comparando left > pos");
		if( (left < size) && (v[left] > v[pos])) {
			System.out.println("mudando lft: "+ v[left] + " com " + v[pos]);
			maxi = left; 
		} else {
			maxi = pos; 
		}

		System.out.println("comparando right > maxi");
		if(right < size && v[right] > v[maxi]) {
			System.out.println("mudando right: "+ v[right] + " com " + v[pos]);
			maxi = right; 
		}
		
		if(maxi != pos) {
			System.out.println(v[maxi] + "<>" + v[pos]);
			swap(v,pos,maxi); 
			heapfy(v,maxi,size); 
		}
	}
	
	public static void swap(int[] v, int j, int aposJ) {
		int aux = v[j]; 
		v[j] = v[aposJ]; 
		v[aposJ] = aux; 
	}
	
	
}
