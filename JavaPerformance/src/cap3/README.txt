


No unix comandos: 
	sar
	vmstat
	iostat
	prstat
	...

No windows
	typeperf
	
		
CPU
	O tempo de cpu é dividiro em duas partes: 
	User Time: tempo de execucao de aplicacoes.
	System Time: tempo de execucao de kernel. 
	
	O tempo de app esta relacionado ao tempo de system. Quanto 
	mais a app solicitar recursos (network, disk,...), mais o system time
	ira aumentar. 
	
	executando vmstat 1 (pool a cada 1 seg): 
	
	r b swpd free	 buff 	 cache 	 si so bi bo in 	cs us sy id wa
	...
	1 0 0 	 1813552 1229084 1508284 0 	0 	3 22 2354 3896 42 3  55  0
	..
	
	Podemos concluir que: 
		o processo a cada um segundo tem 45% de uso de cpu( 42% de app exec (us) e 3% de kernel (sy)), ou seja, 450ms de execucao. 
		55% de idle (id). Esse tempo de ser: 
			- aguardando disco ou network. 
			- aguardando lock de objeto sincronizado
			- nao ter o que fazer. 
			
		* Os dois primeiro podemos tratar com tunning de disco, de banco ou de codigo (tratando as sincronizacoes de objs). 
		* o terceiro caso, o problema começa. Por default o processador deixa alguns ciclos livres para que outro programa possa consumir (caso 
			seja ativado pelo so). Se abrir outra aplicacao quando uma estiver executando, vamos ver esse idle diminuir (sendo consumido por outro app). 
			
			
	Se for uma aplicacao java batch, o objetivo é alcançar sempre 100% do uso. Caso seja uma app client-server (com http por exemplo), ira existir tempo
	de idle devido ao network.
	
	O vmstat reportou que durante 450 ms o processador ficou 100%, isso foi considerado como 45% de utilizacao. 
	
	Se o processador for multiple-cpu, o processador pode ficar idle pelo fato de nao ter algum para tratar o resultado da task que esta executando. Se existe 
	uma quantidade limitada de threads para tratar as tasks (ThreadPool), e uma thread esta executando e entra em block aguardando um retorno do banco, 
	ela nao pode assumir outra tarefa. A tarefa nao podera ser executada pelo fato de nao existir threads que possam assumir outra tarefa. Nesse caso, o processador
	entrará em idle. 
	Para solucionar o ThreadPool deverá ser aumentado (mas isso não é bala de prata), afinal, pode estar ocorrendo um dos dois motivos anteriores (aguardando lock,
	rede ou banco). 
	
	The CPU Run Queue

	Para verificas as threads que podem ser executadas (nao estao em block, sleeping,..). No unix, essa lista é chamada de run queue.
	O vmstat retorna essa informacao, é o primeiro numero de cada linha. 
	No windows, é chamado de processor queue: C:> typeperf -si 1 "\System\Processor Queue Length"

	Como ideal, devemos procurar sem no windows um numero proximo a zero e no unix um numero proximo ao de cores do processador. 

	Se esse numero estiver muito alto por um periodo de tempo, quer dizer que a carga esta alta, e deve ser dividida com outro servidor ou o codigo deve
	ser optimizado. 

Disk Usage

	Se as estatisticas de uso estivem alta, a aplicacao pode estar fazendo mais requisicoes do que o disco pode suportar. 
	Se as estatisticas estiverem muito baixas, pode ser que a app nao esta fazendo buffering corretamente. 
	
	*Ficar de olho em ambos os casos
	
	para verificar no linux: 
	
	% iostat -xm 5
		
		avg-cpu:  %user   %nice %system %iowait  %steal   %idle
           0.00    0.00    0.00    0.10    0.00   99.90

	Device:         rrqm/s   wrqm/s   r/s   w/s    rMB/s    wMB/s avgrq-sz avgqu-sz   await  svctm  %util
	sda               0.00     0.20  0.00  0.40     0.00     0.00    12.00     0.00    0.50   0.50   0.02
	sda1              0.00     0.00  0.00  0.00     0.00     0.00     0.00     0.00    0.00   0.00   0.00
	sda2              0.00     0.20  0.00  0.40     0.00     0.00    12.00     0.00    0.50   0.50   0.02
	dm-0              0.00     0.00  0.00  0.60     0.00     0.00     8.00     0.00    0.33   0.33   0.02
	dm-1              0.00     0.00  0.00  0.00     0.00     0.00     0.00     0.00    0.00   0.00   0.00
			
  
	onde: primeiro bloco: %user : porcentagem de aplicacoes do usuario
						  % systems: porcentagem de system (kernel) 
						  %iowait: nos diz quanto porcento do tempo o processo esta esperando pela leitura do disco. 
						  
		 segundo bloco: 
		 				 await: tempo gasto para ler informacao
		 				 %util: percentual de utilizacao. 
		 				 r ou w/s : read ou writes por segundo
		 				 rMB/s ou wMB/s: quantidade de dados. (ficar de olha na quantidade e quantidade de conteudo escrito ou lido por segundo). 
		 				 
		
	Outro motivo pelo qual devemos ficar de olho no disco é o swap. As app costumam alocar mais memoria que o necessario para rodar. As informações nao 
	acessadas constantemente são paginas em disco. 
	Esse recurso é bem usado em GUI e mal usando em aplicacoes client-server pelo so. 
	
	Podemos verificar o numero de swap realizado no vmstat 1, com as informacoes si (swap in) e so (swap out). 
	
	
Network Usage

	Nao tem nos SOs um bom medidor de network. O melhor no linux é o netstat e no windows a parte grafica do resource mananager. 
	
	Existe uma app separada chamada nicstat: 
	% nicstat 5
	Time 		Int 	rKB/s 	wKB/s 	rPk/s 	wPk/s 	rAvs 	wAvs 	%Util 	Sat
	17:05:17 	e1000g1 225.7 	176.2 	905.0 	922.5 	255.4 	195.6 	0.33 	0.00
	
	O primeiro campo, e1000g1 é o nome da interface, 1GB interface. 
	A utilizacao é o %Util, sendo utilizado .33% (baixa)
	
	Utilizacao de rede nao pode atingir 100%. Uma utilizacao de 40% em uma rede local ja indica que pode estar saturado. 
	Essa parte nao tem muito a ver com java, mas bastante com rede e SO. 
	


JAVA MONITORING TOOLS

	Ferramentas que vem com o JDK: 
		
	jcmd: printa as informacoes basicas de class, thread e vm para um processo: 
			% jcmd proccess_id command opt_args


	jconsole: printa uma visao grafica das atividades, incluindo uso de thread, class e GC. 
	
	jhat: le e ajuda a analisar memory head dumps. Isso é para pos processing.
	
	jmap: mapa de informacoes de uso de memoria pela VM. 
	
	jinfo: informacoes e edicao dos parametros da vm. 
	
	jstack: faz um dump das informacoes do stacks de um processo java.
	
	jstat: Fornece informacoes sobre o GC e class-loading. 
	
	jvisualvm: gui para monitora a JVM (running application e postprocessing). 
	
	
	As informacoes podemos dividir em: 
	
	Basic VM information
			
			Uptime:
				% jcmd process_id VM.uptime
				
			System properties ( inclui os parametro com -D)
				% jcmd process_id VM.system_properties
				% jinfo -systeprops process_id
				
			JVM Version
				% jcmd proccess_id VM.version
				
			JVM command line  ( qual linha de comando iniciou a vm?)
				%jcmd proccess_id VM.command_line
			
			JVM tuning flags
				% jcmd process_id VM.flags [-all]
			
		
		** as flags desempenham um papel importante na perfomance da vm. Para mostrar as flags, pode ser pelo command line, pelo VM.flags (ultimos 2 
		comandos) ou na chamada do processo java, usar a flag: -XX:+PrintFlagsFinal: 
			% java  other_options -XX:+PrintFlagsFinal -version
			
			Esse comando vai mostrar uma lista com centenas de flags (java 7 u 40 tem mais de 600). 
			
		** outra forma de verificar as flags (e podendo edita-las em execucao) é com o jinfo: 
			% jinfo -flags process_id
			
			com o jinfo podemos verificar uma flag especifica: 
			%jinfo -flag PrintGCDetails process_id
			
			e ativar e desativas: 
			%jinfo -flag -PrintGCDetails process_id # turns off. 
			%jinfo -flag PrintGCDetails process_id #turns on
				
			+++ mudar a flag nao quer dizer que a vm vai mudar o comportamento. Tem flags que somente sao consideradas no momento 
			da subida. As flags que podem ser alteradas pelo jinfo e terão efeito são as marcadas como 'Managesble' no PrintFlagsFinal. 
			
			
			
 	Thread information
 			jconsole e jvisualvm mostram o numero de thread por app. 
 			
 			é bom verificar no stack se as threads estao exeuctando, blocked ou sleeping: 
 			% jstack process_id
 			
 			ou via jcmd:
 			% jcmd process_id Thread.print
 			
 			+ no cap 9 e 4
 		
 	Class information
 			
 			Informacoes podem ser obtidas pelo jconsole ou jstat (que tambem fornece sobre class compilation). 
 			+ Cap 12. 
 			
 	Live GC analysis
 			jconsole mostra live graphs sobre head usage do GC
 			jcmd habilita GC to be performed
 			jmap mostra ou cria um dump da memoria. 
 			jstat mostra o que o GC esta fazendo. 
 			
 			+ cap 5
 			
 			
	Heap dump postprocessing
		pode ser obtido pelo jvisualvm, jcmd ou jmap. 
		Podemos gerar um snapshot do heap para analisar em outras ferramentas como o Eclipse Memory Analyzer Tool
	
 	Profiling a JVM
	
		(pag 51)
	 				 




	
	
	
	
	
	
		
	
			
	