package guava5.javaFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ListenableFuture;

public class ListenableFutureTeste {

	
	public static void main(String[] args) {
		
		ListeningExecutorService service = MoreExecutors.listeningDecorator( 
				Executors.newFixedThreadPool(1) // numero de threads
		); 
		
		
		
		ListenableFuture<Integer> fut = service.submit(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				// TODO Auto-generated method stub
				return 1;
			}
		}); 
		
		
		
		fut.addListener( new Runnable() {
			@Override
			public void run() {
			 System.out.println("na thread 1");
			}
		}, service);
		
		
		
	}	
}
