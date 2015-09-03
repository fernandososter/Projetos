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
					
					
					
Pg 63					
					




