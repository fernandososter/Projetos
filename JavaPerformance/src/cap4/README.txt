JIT compiler


Os codigos compilados tem perfomance melhor pq o compilador tem acesso a todo o codigo
de antemao e pode realizar perfomance. 
Nos interpretados, a compilacao para linguagem de maquina ocorre linha-a-linhas, assim, 
acessos a memoria sao mais constantes, o que torma mais lenta a execucao. 


O java é um meio termo. Ou seja, ocorre uma compilacao para uma linguagem idealizada 
(bytecode) e, a vm, consegue fazer a compilacao do bytecode para linguagem da maquina
onde esta executando. Esse é processo é realizado na execucao, just-in-time (JIT???)


Sobre o HotSpot da oracle

HotSpot são pontos de codigo que serão executados somente uma vez. 

Na execucao desses pontos, a jvm nao compila o código, pois é mais rapido executar em 
bytecode. 

Mas se o codigo é um loop ou metodo chamado varias vezes, o compilador calcula o tradeoff
de compilar o codigo. Se valer a pena, ele faz a compilacao. 
Quanto mais informacoes sao usadas no loop ou no metodos, mais a vm tem informacoes sobre 
o codigo e pode passar melhores parametros de compilacao. 

Ex de codigo: 
    ....
	private int sum;
	public void calculateSum(int n) {
		for (int i = 0; i < n; i++) {
			sum += i;
		}
	}
    ....
 
 Se no caso acima o compilador tiver que ler sum da memoria, ler i, somar e jogar novamente, 
 perderá muito tempo. 
 Ele resolve isso deixando sum no registrador e ir acumulando. Em um certo momento, ele pega 
 o resultado e grava no main memory. 
 
 Isso requer um alto nivel de controle transacional da thread...
 
 Ex2: 
 	Se toda vez que é chamado um metodo equal de uma instancia, o fluxo é direcionado para 
 	String.equals() apos uma verificacao, em um certo momento a vm ira aprender que a chamada 
 	do equal se refere  a uma string e nao precisará mais verificar. O pulo dessa verificacao 
 	nos dará um ganho de tempo. 
 	
 ** Isso até acontecer uma chamada para outro equals que nao é string. O codigo produzio pela vm
 esta preparado para isso. 
 
 
 CLIENT SERVER OR BOTH
 
 	Existem dois tipo de compilers: -client (C1)
 									-server (C2)
 									-d64 (??)
 									
 Ex: % java -client -XX:+TieredCompilation other_args									
 									
 A primeira diferenca entre -client e -server é a agressividade da compilação. Em client, 
 a compilacao irá começar antes no cododigo do que no server. (A vm assume que o codigo client
 tera mais codigo para compilar que o server). 
 
 -XX:+TieredCompilation : usado para fazer a compilacao como client, e depois passar o codigo para 
 server, ja que o client é mais eficiente. (até versão 7 precisa setar o tiered, na hora a tiered 
 ja é default. 
 
-> StartUP
 
 Pag 78 mostra um quadro comparando perfomance com os jit compilers. 
 
 Application 	-client 	-server 	-XX:+TieredCompilation
	HelloWorld 	0.08 		0.08 		0.08
	NetBeans 	2.83 		3.92 		3.07
	BigApp 		51.5 		54.0 		52.0
 
 O helloworld é simples demais para notar a diferença. 
 
 As vezes nao é só a vm que demora na inicializacao de uma app. Se uma app tiver muitos jars 
 para carregar, terá que ler todos do disco, e isso vai levar tempo. 
 
->  Batch Processing. 
 
 	Number of stocks 	-client 	-server 	-XX:+TieredCompilation
	1 					0.142 seconds 0.176 seconds 0.165 seconds
	10 					0.211 seconds 0.348 seconds 0.226 seconds
	100 				0.454 seconds 0.674 seconds 0.472 seconds
	1,000 				2.556 seconds 2.158 seconds 1.910 seconds
	1,0000 				23.78 seconds 14.03 seconds 13.56 seconds
 
 Chama a atencao o tiered. Ap´pos um certo tempo o hotspot começa a melhorar 
 a perfomance. 
 

-> Long Running

Informacoes coletadas com o fhb
	Warm-up period -client 	-server 	-XX:+TieredCompilation
	0 seconds 		15.87 	23.72 		24.23
	60 seconds 		16.00 	23.73 		24.26
	300 seconds 	16.85 	24.42 		24.43

é possivel ver a quantidade de operations por segundo apos o warmedup. Sao os hotspots
fazendo efeito em long running.

Quick Summary
For long-running applications, always choose the server compiler,
preferably in conjunction with tiered compilation.


VERSOES DO JIT COMPILER: 

• A 32-bit client version (-client)
• A 32-bit server version (-server)
• A 64-bit server version (-d64)

Se tiver um so 32, deve usar uma versao 32 bits. Se o SO for 64, pode usar uma 64 ou 32 (nao 
precisa ser necessariamente uma 64). 

**********************
Se estiver usando um heapspace com menos de 3GB, é vantagem usar um 32 bits, pois a quantidade
de informacoes necessarias para os ponteiros será menor do que o 64, mesmo que o SO seja 64 bits.
Alem de usar menos memoria para o gerenciamento. 
**********************

O downside de usar 32 bits é que precisa rodar em jvm com menos de 4Gb (3 em alguns windows e 3.5 em 
alguns linux), isso contado heap, permgen, jvm e libraries nativas que serao carregadas. 
Tambem pode ser que programas que usam muitos long ou double tenham problemas em gravar 
informacoes em registradores 64. 

Programas que cabem em 32 bits vao rodar em 5 e 20% mais rapido que em 64 (os que se adequam as condicoes
de memoria acima, nao quer dizer que 32 seja mais rapido em todos os casos). 

Para atrapalhar, em algumas distros de linux e solaris, na instalacao da vm, o compilador 32 e 64 sao 
separados fisicamente, ficam um em uma pasta e outro em outra pasta. Nesses casos temos que mudar 
manualmente o classpath.

sumario de utuilizacao: 

Install bits 	-client 				-server 				-d64
Linux 32-bit 	32-bit client compiler 	32-bit server compiler 	Error
Linux 64-bit 	64-bit server compiler 	64-bit server compiler 	64-bit server compiler
Mac OS X 		64-bit server compiler 	64-bit server compiler 	64-bit server compiler
Solaris 32-bit 	32-bit client compiler 	32-bit server compiler 	Error
Solaris 64-bit 	32-bit client compiler 	32-bit server compiler 	64-bit server compiler
Windows 32-bit 	32-bit client compiler 	32-bit server compiler 	Error
Windows 64-bit 	64-bit server compiler 	64-bit server compiler 	64-bit server compiler


Tabela de default compiler por so: 

OS 											Default compiler
Windows, 32-bit, any number of CPUs 			-client
Windows, 64-bit, any number of CPUs 			-server
MacOS, any number of CPUs 						-server
Linux/Solaris, 32-bit, 1 CPU					-client
Linux/Solaris, 32-bit, 2 or more CPUs 			-server
Linux, 64-bit, any number of CPUs 				-server
Solaris, 32-bit/64-bit overlay, 1 CPU 			-client
Solaris, 32-bit/64-bit overlay, 2 or more CPUs 	-server (32-bit mode)

 
Para mostrar qual versao do default compiler esta sendo usada: 

% java -client -version
java version "1.7.0"
Java(TM) SE Runtime Environment (build 1.7.0-b147)
Java HotSpot(TM) 64-Bit Server VM (build 21.0-b17, mixed mode)

(64-bits Server Vm)



** O mais importante aqui: 
	- Nem sempre 64 é mais rapido (se menos de 3GB 32 é mais rapido)
	- Para arrancada, client é mais rapido. 
	- Para longrunning, server é mais rapido (warm up)
	- para batch, depende. 
	- cada vm para cada versao tem um compilador default especifico. 
	- vale a pena configurar o mais apropriado. 
	
	
INTERMEDIATE TUNING FOR THE COMPILERS








 	
    
    
