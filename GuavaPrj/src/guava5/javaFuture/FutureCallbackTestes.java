package guava5.javaFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;


class FutureCallbackImpl implements FutureCallback<String> {

	String str = null; 
	@Override
	public void onSuccess(String result) {
		System.out.println("Sucesso: " + result);
		this.str = "sucesso";
	}

	@Override
	public void onFailure(Throwable t) {
		t.printStackTrace();
		this.str = "falhou";
	}
	
	public String getStr() {
		return this.str; 
	}
	
}


public class FutureCallbackTestes {

	
	public static void main(String...args) {
		ListeningExecutorService service = MoreExecutors.listeningDecorator( 
				Executors.newFixedThreadPool(1) // numero de threads
		); 
		
		ListenableFuture<String> futureTask = service.submit(new Callable<String>(){
			@Override
			public String call() throws Exception {
				// TODO Auto-generated method stub
				return "Task Completed";
			}
		}); 
		
		
		FutureCallbackImpl callbcak = new FutureCallbackImpl(); 
		Futures.addCallback(futureTask, callbcak);
		System.out.println(callbcak.getStr()); 
		
	}
	
}
