AOP


cross-cutting concerns - sao funcionalidades pararelas ao core busines, como logging, 
security, transaction e caching, que AFETAM TODO O SISTEMA.

os aspects nos fornecem modulos que podem ser usados nesses casos. As classes sao 
nomeadas como aspcts e nos beneficiam fornecendo codigo limpo sem necessidade de 
delegate ou herança (como seria feito nomalmente). 

Os codigos referentes ao concern ficam em somente um ponto, nos aspects. 

AOP tem os proprios jargoes: 

	ADVICE: é o que e onde o aspect faz (what e when). è o job. Sempre em relacao ao metodo.
		No spring tem os seguintes tipos de advice: 
			- Before: antes de um metodo ser invocado
			- After: depois que o metodo completa
			- After-returning: depois do advised method completar com sucesso
			- After-throwing: depois do advised methodo reportar exception 
			- Around: faz um wrap do adivesd method e fornece funcionalidade para antes e depois de invoked. 
			
	JOIN POINTS: é onde o metodo será chamado no fluxo. 
	
	POINTCUTS: define quais joinpoints serão aplicados o advices. é o where. 
	
	ASPECTS: é a juncao do advice com os join points. Sao todas as informacoes necessarias para ser aplicado. 
			em um ponto. 
			
	INTRODUCTIONS: ???
	
Fig na 99 explica melhor o funcionamento. 

	
		






