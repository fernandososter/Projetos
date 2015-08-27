package guava5;

import java.util.ArrayList;
import java.util.List;

import com.google.common.util.concurrent.Monitor;

 class Wrapper {

	static Monitor m = new Monitor(); 
	
	static List<String> lst = new ArrayList<String>(); 
	
	static Monitor.Guard guard = new Monitor.Guard(m) {
		@Override
		public boolean isSatisfied() {
			System.out.println("isSatisfied " + lst);
			System.out.println(Thread.currentThread().getName()); 
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return lst.size() <= 5; 
			
		}
	};
}



public class MonitorTeste  implements Runnable{
	
	
	
	public static void main(String...args) {
		
		
			MonitorTeste monitor = new MonitorTeste();
		
			Thread t = new Thread(monitor); 
			Thread t1 = new Thread(monitor); 
			Thread t2 = new Thread(monitor); 
			t.start();
			t1.start();
			t2.start();
	}
	
	
	

	@Override
	public void run() {
		try {
		while(true) {
		
			Wrapper.m.enterWhen(Wrapper.guard);
		
			try {
			
				Wrapper.lst.add("teste" + Thread.currentThread().getName()); 
				
			} finally {
				Wrapper.m.leave();
			}
			
			Thread.sleep(5000);
		}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
