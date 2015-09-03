Concurrency

Monitor

O monitor � um semaforo que faz o papel do synchronize mas de uma forma muito mais facil. 

O monitor.guard vai garantir que apenas uma thread por vez ir� executar o bloco resguardado. 
Quem faz o controle � o monitor.enterWhen(). 

Ex de c�digo, MonitorTeste 


Condicoes do Monitor que retornam um boolean deve ter um bloco if para fazer algo, seguido de um try/finally. 
Se nao tiver como retorno um boolean, entao � somente try/finally. 

Ex (a impressao que da � que o enterWhen ir� aguardar o momento de executar, enquanto o 
enterIf se no momento indicado nao puder executar, o ciclo ser� perdido). 

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


-> Monitor.enter: vai tentar entrar no monitor e ficar� bloqueado at� conseguir. 
-> Monitor.enterIf: tem como retorno um bool indicando se o monitor aceitou ou nao a entrada. 
-> Monitor.enterWhen: aguarda entrar no monitor. Quando entra, somente ir� sair quando a 
					condi��o for satisfeita (isSatisfied). 
-> Monitor.tryEnter: vai tentar acessar o monitor, no entanto se nao conseguir, nao tenta� 
					novametne. Vai retornar um boolean indicando que nao conseguir. 
-> Monitor.tryEnterIf: Vai tentar entrar imediatamente no monitor se o lock estiver disponivel
					e a condicao for satifeita. Senao vai retornar um boolean indicando que 
					nao conseguiu. 
					
					
					
Pg 63					
					




