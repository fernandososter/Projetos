

SPARQL � SPARQL Protocol and RDF Query Language

Mas ele nao se limita somente ao RDF, pode fazer query em xml, json, 
xls, relational database,... e � a combinacao dos diferentes tipos 
que torna o sparql poderoso. 
Os suportados depende do RDFEngine(ou SPARQL engina) implementa. 

RDF n�o � um formato, mas um modelo de facts

Os dados do RDF sao compostos por tres tipos diferentes de informacoes: 

subject		 predicate		object.

richard 	homeTel			(229)276-5135

Esse formato se chama turtle.
O turtle acaba com um . no final da sente�a. 

# filename: ex002.ttl
@prefix ab: <http://learningsparql.com/ns/addressbook#> .
ab:richard ab:homeTel "(229) 276-5135" .
ab:richard ab:email "richard49@hotmail.com" .
ab:cindy ab:homeTel "(245) 646-5488" .
ab:cindy ab:email "cindym@gmail.com" .
ab:craig ab:homeTel "(194) 966-1505" .
ab:craig ab:email "craigellis@yahoo.com" .
ab:craig ab:email "c.ellis@usairwaysgroup.com" .


URI: Uniform Resource Identifier
		- uri visa identificar algo
		
URL: Uniform Resource Locators : 
		-� um tipo de URI
		- url visa localizar um objeto
	
Para armazenar queries, a especificacao do SPARQL diz que devemos armazenar
em arquivos com extens�o .rq

Os arquivos turtle s�o armazenados em .ttl


# filename: ex003.rq
PREFIX ab: <http://learningsparql.com/ns/addressbook#>
SELECT ?craigEmail
WHERE
{ ab:craig ab:email ?craigEmail . }

** QUERY simples, com uma variavel. 
** a variavel pode ser usada em qualquer lugar (?craigEmail) da query. 

no where: 
	subject = ab:craig
	predicate = ab:email
	object = ?craigEmail
	
SPARQL nao � case-sensitive, ou seja, o upper case � conven��o, nao necessidade

O . ao final do rq e do ttl � um bom habito, mas nao � necess�rio. 

SPARQL processor e SPARQL engine querem dizer a mesma coisa, um mecanismo
que carrega os dados e pode processar SPARQL Queries. 

Para processar no disco local o ARQ da conta (faz parte do pacote apache-jena). 
Java-Based. 

Para executar um rq sobre um ttl: 

	%> arq --data ex002.ttl --query ex003.rq


A query abaixo � a mesma da acima mas usa o fullpath do URI ao inves do 
prefix : 
O resultado ser� o mesmo: 

	# filename: ex006.rq
	SELECT ?craigEmail
	WHERE
	{
		<http://learningsparql.com/ns/addressbook#craig>
		<http://learningsparql.com/ns/addressbook#email>
		?craigEmail .
	}


Se nao colocar o --data na execucao do arq, podemos estipular o dataset no 
FROM da query: o <> diz ao arq para procurar no disco local o arquivo .ttl

	# filename: ex007.rq
	PREFIX ab: <http://learningsparql.com/ns/addressbook#>
	SELECT ?craigEmail FROM <ex002.ttl>
	WHERE
	{ ab:craig ab:email ?craigEmail . }

** Se colocar um na query e um dataset na linha de execucao (--data), o 
--data vai sobrepor o da query. 

A variavel pode ser colocada em outras posicoes da query: 
(para saber quem tem o telefone ?person, colocamos a variavel do subject. 

	# filename: ex008.rq
	PREFIX ab: <http://learningsparql.com/ns/addressbook#>
	SELECT ?person
	WHERE
	{ ?person ab:homeTel "(229) 276-5135" . }


Ou entao saber tudo sobre a cindy: 
(vai retornar os predicados e os objetos)

	# filename: ex010.rq
	PREFIX ab: <http://learningsparql.com/ns/addressbook#>
	SELECT ?propertyName ?propertyValue
	WHERE
	{ ab:cindy ?propertyName ?propertyValue . }

Aqui o ARQ vai retornar duas colunas de informacoes, com tudo que encontrar
sobre o subject ab:cindy

Em dataset reais, no entanto, as informa��es nao ir�o vir de uma forma readble quando no exemplo. 
Provavelmente virao quebradas como se fosse um database relacional onde o id n�o vai ser um nome legivel: 
			
			# filename: ex012.ttl
			@prefix ab: <http://learningsparql.com/ns/addressbook#> .
			@prefix d: <http://learningsparql.com/ns/data#> .
			d:i0432 ab:firstName "Richard" .
			d:i0432 ab:lastName "Mutt" .
			d:i0432 ab:homeTel "(229) 276-5135" .
			d:i0432 ab:email "richard49@hotmail.com" .
			d:i9771 ab:firstName "Cindy" .
			d:i9771 ab:lastName "Marshall" .
			d:i9771 ab:homeTel "(245) 646-5488" .
			d:i9771 ab:email "cindym@gmail.com" .
			d:i8301 ab:firstName "Craig" .
			d:i8301 ab:lastName "Ellis" .
			d:i8301 ab:email "craigellis@yahoo.com" .
			d:i8301 ab:email "c.ellis@usairwaysgroup.com" .

AS inform��oes tambem nao vir�o dispostas todas na mesma uri, ter�o uris diferentes para fazer um reaproveitamento 
do vocabulario. 

Para fazer join das informacoes agora: 

	# filename: ex013.rq
	PREFIX ab: <http://learningsparql.com/ns/addressbook#>
	SELECT ?craigEmail
	WHERE
	{
		?person ab:firstName "Craig" .
		?person ab:email ?craigEmail .
	}

Na query acima, o arq vai procurar por alguem com nome Craig. Quando encontrar, vai atribuir o 
subject na variavel ?person. 
Entao ir� para a proxima condi��o, que � pegar o object do id para o predicate ab:email

No entato a condi��o somente ser� valida se ambos forem preechidas, ou seja, somente ter� retorno 
do select se existir alguem com nome Craig e com email cadastrado. 
Se mais de um registro atingirem as condicoes, mais de um ser� retornado. 

Usar uma variavel para fazer mais de uma condi��o, inclusive de diferentes fontes, � uma das 
melhores caracteristicas do SPARQL. 

Se na query acima formos buscar pelo dono do telenone (pelo subject), provavelmente vamos receber como 
retorno um �http://learningsparql.com/ns/data#i0432�. Isso quer dizer que o subject � o id0432 
da URI http://learningsparql.com/ns/data#. Podemos usar essa informa��o para buscar em outro
database o nome. 



Procurando por Strings
	
	Se nao sabemos quais informa��es estao disponiveis do triple (tripla), podemos usar 
	tres variaveis e todos os dados ser�o retornados (subject, precicate, object). 
	No entanto, pode ser muita informacao e precisamos colocar um filtro. 
	
	Para filtrar: FILTER
		E para fazer o filter, usamos o regex junto
		
		# filename: ex021.rq
		PREFIX ab: <http://learningsparql.com/ns/addressbook#>
		SELECT *
		WHERE
		{
			?s ?p ?o .
			FILTER (regex(?o, "yahoo","i"))
		}

** a query vai trazer todos os e-mails que tem yahoo
** a query usa o *, que vai retornar todas as variaveis declaradas na query. 


QUERING PUBLIC DATASOURCE

	SPARQL endpoint � o webservice onde executamos as queries. 
	
	Geralmente � oferecido uma webpage para executar as queries e mostrar o retorno. Do wikipedia (DBPedia), 
	� o SNORQL (http://dbpedia.org/snorql/).
	
	A conversao do wikipedia para o dbpedia � geralmente feita da seguinte forma: 
		WIKI : http://en.wikipedia.org/wiki/Some_Topic 
		DB 	 : http://dbpedia.org/resource/Some_Topic    -> direcina para http://dbpedia.org/page/some_topic
	
	** Acessando a ulr e trazendo conteudo sabemos que a url esta correta. 
	
	Abrindo a tela do SNORQL, o http://dbpedia.org/resource/ ja estara declarado como PREFIX :
	Isso quer dizer que para pesquisar um dado (Some_Topic), basta usar :Some_Topic   

** http://dbpedia.org/resource/Timbaland vai virar :Timbaland
	
	
	Para executar a query no snorql, o dbpedia.org/ontology/ nao � declarado automaticamente, entao precisa ser declarado: 
	
		PREFIX d: <http://dbpedia.org/ontology/>
		SELECT ?artist ?album
		WHERE
		{
			?album d:producer :Timbaland .
			?album d:musicalArtist ?artist .
		}
	
		
	
	
		































