Concurrency

Monitor

O monitor é um semaforo que faz o papel do synchronize mas de uma forma muito mais facil. 

O monitor.guard vai garantir que apenas uma thread por vez irá executar o bloco resguardado. 
Quem faz o controle é o monitor.enterWhen(). 

Ex de código, MonitorTeste 


Condicoes do Monitor que retornam um boolean deve ter um bloco if para fazer algo, seguido de um try/finally. 
Se nao tiver como retorno um boolean, entao é somente try/finally. 

Ex (a impressao que da é que o enterWhen irá aguardar o momento de executar, enquanto o 
enterIf se no momento indicado nao puder executar, o ciclo será perdido). 

Com if: 
	
	if(monitor.enterIf(guardCondition)) {
		try{ 
			doWork(); 
		}finally{
			monitor.leave(); 
		}
	}


sem if: 
	
	monitor.enterWhen(guardCondition); 
	try{
		doWork(); 
	}finally {
		monitor.leave(); 
	}									


-> Monitor.enter: vai tentar entrar no monitor e ficará bloqueado até conseguir. 
-> Monitor.enterIf: tem como retorno um bool indicando se o monitor aceitou ou nao a entrada. 
-> Monitor.enterWhen: aguarda entrar no monitor. Quando entra, somente irá sair quando a 
					condição for satisfeita (isSatisfied). 
-> Monitor.tryEnter: vai tentar acessar o monitor, no entanto se nao conseguir, nao tentaá 
					novametne. Vai retornar um boolean indicando que nao conseguir. 
-> Monitor.tryEnterIf: Vai tentar entrar imediatamente no monitor se o lock estiver disponivel
					e a condicao for satifeita. Senao vai retornar um boolean indicando que 
					nao conseguiu. 
					
					
					
ListenableFuture

É uma implementacao que usa a classe Future do java 5 (exemplo de funcionamento na classe JavaRegularFuture) 
que espera um objeto futuro. 				

Com a ListenableFuture, o guava adiciona listeners que sao notificados quando o valor esta preenchido. 
Esses elementos serao notificados e executarao algo. 


FutureCallback

é o mesmo principio do ListenableFuture mas é usada a classe Futures (que tem metodos estaticos para trabalhar com 
a interface Future). 
Nao adicionamos o FutureCallback diretamente no ListenableFuture, mas com o Futures.addCallback()

A classe que será noticada implementa dois metodos, o success() e o failure(). 
No success é passado o resultado do processamento, no failure a mensagem de erro. 


SettableFuture
usado para atribuir um valor a um ListenableFuture. 
SettableFuture sf = SettableFuture.create()
sf.set("")
sf.setException(exception)

The SettableFuture class is very valuable for cases
when you have a method that returns a Future instance, but you already have the
value to be returned and you don't need to run an asynchronous task. We will see
in the next section just how we can use the SettableFuture class.

Pesquisar sobre o AsyncFunction


FutureFallback 

usado para criar valores default quando ocorrer uma excpetion. 
A classe obriga a implementacao de um metodo, o create(Throwable t)


RateLImiter.
					

???


