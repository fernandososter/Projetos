

Para poder determinar metodos e classes que serão deployados em  ambientes diferentes, 
o spring fornece a opcao de criar profiles: 

A classe Developerment somente será usada se dev estiver ativo
@Configuration
@Profile("dev")
public class DevelopmentProfileConfig { ... } 

A classe Production se prod estiver ativo. 
@Configuration
@Profile("prod")
public class ProductionProfileConfig { ... }

Assim nao precisamos recompilar a app para ambientes diferentes. 

 ** até a v3.1 o @Profile erá somente a nivel de classe
  a partir da 3.2, o @Profile pode ser usado em metodos. 
  
 
 Dentro do mesmo config: (podemos escolher o metodo)
 
 	@Bean
  	@Profile("dev")
	public DataSource embeddedDataSource() { ... } 
	
	@Bean
	@Profile("prod")
	public DataSource jndiDataSource() {...}
	
** metodos nao anotados com o @Profile serão sempre criados, independente do profile ativo


PARA ATIVAR UM PROFILE

Dentro do xml, na declaracao do <beans>, colocar um "profile="dev"" ou qualquer outro profile. 
Esses arquivos de profile serão carregados no momento do deploy, por isso podemos muda-los. 

Podemos, para evitar criar muitos arquivos, criar dentro do mesmo <beans> outros <beans> 

<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:jdbc="http://www.springframework.org/schema/jdbc"
	xmlns:jee="http://www.springframework.org/schema/jee"
	xmlns:p="http://www.springframework.org/schema/p"
	xsi:schemaLocation="
	http://www.springframework.org/schema/jee
	http://www.springframework.org/schema/jee/spring-jee.xsd
	http://www.springframework.org/schema/jdbc
	http://www.springframework.org/schema/jdbc/spring-jdbc.xsd
	http://www.springframework.org/schema/beans
	http://www.springframework.org/schema/beans/spring-beans.xsd">

	<beans profile="dev">
	 ...
	</beans> 

	<beans profile="QA">
	 ...
	</beans> 

	<beans profile="prod">
	 ...
	</beans> 
</beans> 

Mas como o Spring vai saber qual profile usar? 
Existe o profile ativo:  spring.profiles.active
e o defualt:  spring.profiles.default

Se o ativo nao estiver setado, será usado o default. Se nao tiver nenhum, nao vai
criar os beans que estao dentro dos profiles, somente os que estao fora. 

Para setar o ativo e o default, existem varias formas: 

	As initialization parameters on DispatcherServlet
	 As context parameters of a web application
 	As JNDI entries
	 As environment variables
	As JVM system properties
	Using the @ActiveProfiles annotation on an integration test class
	
Exemplo de web.xml para setar o default profile (pag 71): 

setando por context: 

	<context-param>
		<param-name>spring.profiles.default</param-name>	
		<param-value>dev</param-value>
	</context-param>	

por servlet
	
	<servlet>
		<servlet-name>appServlet</servlet-name>
		<servlet-class>
			org.springframework.web.servlet.DispatcherServlet
		</servlet-class>
		<init-param>
			<param-name>spring.profiles.default</param-name>
			<param-value>dev</param-value>
		</init-param>
		<load-on-startup>1</load-on-startup>
	</servlet>


** No entanto, se tiver um active profile setado, o default nao tera efeito, 
ja que o active tem preferencia. 

** sao default profiles e active profiles, ou seja, pode ter mais de um default
e mais de um sctive. Se fizer sentido...

Para usar o @AtiveProfile com testes integrados, o ex: (assim podemos simular prod, qa,...)

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={PersistenceTestConfig.class})
@ActiveProfiles("dev")
public class PersistenceTest {
...
}


CONDITIONAL

Pode ser aplicado a declaracao do bean no config e somente sera executado
se a condicao passada for verdadeira (por ex, uma classe exista), senao o bean 
sera ignorado: 

	@Bean
	@Conditional(MagicExistsCondition.class)
	public MagicBean magicBean() {
		return new MagicBean();
	}






