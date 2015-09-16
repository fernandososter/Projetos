

-> cada programa deve ser testado da mesma forma que será usado. 


Existem 4 principios para obter perfomance dos testes.

1 - PRINCIPIO: TESTAR APLICACOES REAIS
2 - ENTENDER THROUGHPUT, BATCHING E RESPONSE TIME
3 - ENTENDER VARIABILITY
4 - TEST EARLY, TEST OFTEN 


Para o primeiro principio: 

Existem 3 tipos de testes e o tipo a ser escolhido depende do cenario de producao. 

1. Microbenchmarks

	Medicao de uma pequena quantidade de performance. 
	
		- O tempo de criar uma thread ou usar um ThreadPool. 
		- usar metodo synchronized ou nao. 
		- testar uma implementacao ou outra. 
		
	Microbenchmarks é dificil de implementar. Principalmente quando nao armazenamos
	o valor de retorno de uma chamada. O compilador pode nos pregar uma peça e 
	remover a chamada que nao armazena valores. Assim, a medição dos tempos nao
	será real. 
		

	Outro pitfall é que quando executamos loops ou recursivos, o compilador identifica
	e aplica regras para melhorar a perfomenace. Por tanto, a melhor forma de 
	extrair valores reais, é passar valores randomicos para as funcoes envolvidas. 
	
	A compilacao das classes é feita por profile feedback. Ou seja, o compilador sabera
	quantas vezes o metodo foi chamado, se tem list, loop,... e compila com as opcoes
	mais performatica. 
	
	Muitas vezes nao vale a pena medir as performances em nanosegundos do Microbenchmarks, 
	as vezes é melhor focar no macro onde poderemos ganhar mais tempo. 

2. Macrobenchmarks.

	Produzir um software modularizado nao é o mesmo produzir um software completo. 
	Por exemplo, quanto integramos as partes, a compilacao ocorre com parametros de 
	perfomance difernte. 
	
	Usar mock para database não é o mesmo que usar um database real. A compilacao vai 
	ocorrer com outros parametros e a perfomance do proprio java (desconsiderando banco
	de dados) será diferetne. 
	
	Quando pensamos em melhorar perfomance, todos os componentes do software tem que
	melhorar de forma compativel. Nao adianta melhorar um modulo de calcula de 100 RPS 
	(requests per second) para 200 RPs e o output do modulo para um banco de dados 
	continuar em 100. 
	
	it is a matter of priorities: without testing the entire application, it is impossible
	to tell where spending time on performance work will pay off.
	
	
3. Mesobenchmarks. 

	Quando nao é nem micro nem macro. Por exemplo, medir o tempo de resposta em que 
	um jsp é montado na tela. Isso envolve o request, o response e bastante bloco de
	codigo. No entanto, sem tempo de login (ldap) ou até database. 
	
	
	
	
	
Quick Summary
	1. Good microbenchmarks are hard to write and offer limited value.
		If you must use them, do so for a quick overview of performance,
		but don’t rely on them.
	2. Testing an entire application is the only way to know how code
		will actually run.
	3. Isolating performance at a modular or operational level—a mesobenchmark—
		offers a reasonable compromise but is no substitute
		for testing the full application.



Para o segundo principio: 
Varias formas de ver a perfomance da aplicacao. Quais das medidas sao mais importantes para a aplicacao: 

	- Elapsed Time (Batch) Measurement: Quanto tempo levou uma certa task? 
	- Throughput Measurements: Quando de trabalho uma task pode fazer em um certo periodo de tempo? 
	- Response Time Tests: Menos comum, quanto tempo leva um request para um server e a resposta de volta para o 
							client. 
							
	

As medidas dos testes são geralmente: 
	- Transaction Per Second (TPS)
	- Requests per Second (RPS)
	- Operations per Second (OPS)
	
	
A fase de warm-up de um application server pode durar em torno de 45 min. 
Warm-up seria o tempo de carregar informacoes sobre o codigo? 


Exitem duas formas de medir o response time:
 	- media (somar os tempos e dividir pelo numero de requests)
 	- percentil (verificar quanto % dos requests estao em um tmepo estipulado)
 		Por exemplo, 90% dos requests estao abaixo de 1,5 seg
 
** é bom olhar os dois (media e percentil)
 		
** no caso da media, tomar cuidado com os outliers, que podem jogar o tempo
muito para cima. 
Quando ocorrer outliers muito discrepantes, podemos considerar a hipotese de ser 
o GC passando. 

Para operacao em client-server, o livro considera o thinking time. (que seria o tempo do 
cliente ler a informacao).  			
 			
 			
O livro aconselha uma ferramente para realizar o diagnostico: Faban Harness
O Faban vem com uma app, o fhb: 
% fhb -W 1000 -r 300/300/60 -c 25 http://host:port/StockServlet?stock=SDO


Terceiro principio: Understand Variability

Esse principio envolve a variacao de tempo e condicoes na execucao dos testes. Por mais que o mesmo 
metodo seja chamado, com dados iguais, o tempo será diferente por diversos motivos (disco, network,mem....)

O ideal é variar os inputs com ramdon values para processamento e tirar uma media dos multiplos processamentos. 

Quando tem uma flutuacao muito grande no tempo, podemos considerar que é devido ao random fluctuation. Esse flutuacao deve 
sumir no somatorio e nas medias. 

Testar o codigo por changes é chamado de regression test, sendo o código original o baseline e as alteraçoes o spiceman.
considere exemplo abaixo, onde um codigo é testado 3 vezes: 

						Baseline 	Specimen
First iteration  		1.0 seconds 0.5 seconds
Second iteration		0.8 seconds 1.25 seconds

Third iteration  		1.2 seconds 0.5 seconds
Average 				1 seconds 	0.75 seconds



Quarto principio: Test Early, Test Often

	Adicionar teste de regressao de performance  ao ciclo de desenvolvimento. 
	Esse tipo de teste demora até adiquirirmos confianca de que alterações foram responsaveis 
	pela diferença de tempo e nao random fluctuation. 
	
	As vezes pegamos problemas de arquitetura com testes. É melhor que seja pego o quanto antes. 
	
	guidelines: 
		- Automatize tudo
			Scripts que executam tudo automaticamente. t-tests (t de student), realiza acesso a banco 
			e no mesmo so para ser comparado com o baseline. 
			
		- Measure Everything
			Todos os tempos e todos os logs disponiveis devem ser considerados (log de system e de GC). 
			Java Flight Recorder (JFR), periodic thread stacks, heap analsys (histograms ou full head dumps)
			Se incluir sistemas externos, incluir, como Oracle ( AWR - Automatic Workload Repository Reports). 
			
		- Run on the target system
			O codigo quando compilado é para um hardware. Realizar as medicoes em um e aplicar em outro vai 
			gerar discrepancia. (diferente em um notebook e em um 256-sparc com mais cores). O segundo vai 
			abrir muito mais threads e a compilacao pode ficar compromometida. 
			
			Caches—software and, more importantly, hardware — behave differently on different systems, and under 
			different loads. And so on…
			

 


