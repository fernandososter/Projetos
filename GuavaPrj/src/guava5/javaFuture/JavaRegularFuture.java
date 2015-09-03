package guava5.javaFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


class Teste extends Thread implements Runnable{ 
	@Override
	public void run() {

	}
}


public class JavaRegularFuture {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
	
		
		ExecutorService service = Executors.newCachedThreadPool(); 
		
		Future<Integer> future = service.submit(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				Thread.sleep(5000);
				System.out.println("vai retornar do callable");
				return 1;
			}
		}); 
		
		System.out.println("esta no main");
		System.out.println(future.get());
		
		
	}

}
