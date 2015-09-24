


Existem dois tipos de factories no spring

Bean Factories : org.springframework.beans.factory.BeanFactory

Applicatino Context: org.springframework.context.ApplicationContext
	dentro de application context: 
	
->	AnnotationConfigApplicationContext—Loads a Spring application context
from one or more Java-based configuration classes
->	AnnotationConfigWebApplicationContext—Loads a Spring web application
context from one or more Java-based configuration classes
->  ClassPathXmlApplicationContext—Loads a context definition from one or
more XML files located in the classpath, treating context-definition files as classpath
resources
->	FileSystemXmlApplicationContext—Loads a context definition from one or
more XML files in the filesystem
->	XmlWebApplicationContext—Loads context definitions from one or more
XML files contained in a web application


ApplicationContext context = new FileSystemXmlApplicationContext("c:/knight.xml");
ApplicationContext context = new ClassPathXmlApplicationContext("knight.xml");
ApplicationContext context = new AnnotationConfigApplicationContext(com.springinaction.knights.config.KnightConfig.class);


Fig na pagina 20 mostra o ciclo de vida do bean no spring. 

Modulos do Spring. Cada modulo pode ser implantado separadamente. 

	Core Spring Container : prove as funcionalidades de DI. JNDI access, EJB integragion, scheduling, tudo é implementado com base nele. 
	
	Spring AOP Module: AOP suporte. 
	
	Data Access e Integration : Modulo de acesso a dados usando hibernate, jpa, ... 
	
	Web e Remoting :  web com suporter para http, rmi, rest, ...
	
	Instrumentation: adicionar agentes na jvm????
	
	Testing: suporte para testes e mocks. 
	
	
	
	