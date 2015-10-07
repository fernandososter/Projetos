

Security

- Introducing Spring Security
- Securing web applications using servlet filters
- Authentication against databases and LDAP


Spring security faz tanto autenticacao tanto autorizacao, na web e no spring normal. 

Usao AOP para fazer a segurança. 

é a evolução do Acegi (que exigia muitos xmls)

Na evolução, o Spring Security 3.2 usa Web Resquests, AOP, Servlet Filter....


Modulos do Spring Security
	
	Adicionando os modulos, que são divididos em 11:
		
		ACL: fornece suporte ao access control lists. 
		Aspects: fornece modulo alternativo ao Spring AOP nativo para AspectsJ	
		CAS Client: Single Sign-on supporte usando Central Authentication Srevice
		Configuration: Suporte para configurar o Spring Secutity com XML ou Java
		Core: Core do spring security
		Cryptography: encryption e password encoding
		LDAP: Ldap Authentication
		OpenID: suporte para OpenID
		Remoting: integracao com Spring Remoting
		Tag Library: Security para JSP Tag Libs
		Web: suporte para filter-based web security
		
		
	Como basico adicionar o core e o configuration. Para usar o web, precisa adicionar os relativos 
	a plataforma web tambem. 
	
	Existem duas formas de fazer a configuração iniciar o Spring Security. Ambas vao criar um 
	DelegatingFilterProxy: (o nome não é opcional, deve ser springSecurityFilterChain)
	
		Via WEB.xml: 
		
			<filter>
				<filter-name>springSecurityFilterChain</filter-name>
				<filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
			</filter>
			
		Via java component
			Extendendo WebApplicationInitializer (para security AbstractSecurityWebApplicationInitializer)
			
			public class SecurityWebInitializer extends AbstractSecurityWebApplicationInitializer 
			{
			...
			}
		
		
Simple Security Configuration

	@Configuration
	@EnableWebSecurity     <-- habilitando o websecurity
	public class SecurityConfig extends WebSecurityConfigurerAdapter {
	}
		
	
	A classe anotada com @EnableWebSecurity precisa ter um container de segurança junto. No caso acima, 
	o extends WebSecurityConfigurerAdapter é a criacao do Configurer container. 
	
	Para usar com mvn, tem uma annotation que cabe melhor, o @EnableWebMvcSecurity
	
	@Configuration
	@EnableWebMvcSecurity
	public class SecurityConfig extends WebSecurityConfigurerAdapter {
	}	
		
	
	O EnableWebMvcSecurity nos permite, entrou outras coisas, receber o user principal (username)
	via um parametro anotado com @AuthenticationPrincipal. 
	
	a classe WebSecurityConfigurerAdapter tem 3 metodos configure() que podemos sobreescrever: 
		
		configure(WebSecurity) - sobrescreve o filtro do Spring Security
		configure(HttpSecurity) - sobrescreve como os requests sao tratados pelos interceptors
		configure(AuthenticationManagerBuilder) - sobrescreve o user-detailed services. 
		
	
	IN-MEMORY authentication 
	
		para possibilitar o in-memory autentication ,temos que usar o configure() que recebe como 
		parametro o AuthenticationManagerBuilder :
		
			@Override
			protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
				auth.inMemoryAuthentication()
					.withUser("user").password("password").roles("USER").and()
					.withUser("admin").password("password").roles("USER", "ADMIN");
			}
					
		a chamada o inMemoryAuthentication() ja habilita o in-memory. No entanto temos que fornecer os usuarios, 
		passwds e roles. Todos esses metodos são da classe UserDetailsManagerConfigurer.UserDetailsBuilder. 
		
		** tem o and() para adicionar mais de um. 
		
		Alem desses metodos, a classe UserDetailsManagerConfigurer.UserDetailsBuilder ainte tem outros: 
			accountExpired(bool)
			accountLocked(bool)
			and()
			authorities(...)
			credentialsExpired()
			disabled()
			password()
			roles() 
			
			
			
	JDBC authentication
		
		@Autowired
		DataSource dataSource;

		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth
			.jdbcAuthentication()
			.dataSource(dataSource);
		}
			
		O SprintSecurity vai tentar por default as seguintes queries, assumindo que a app se enquadrou 
		no que o SS espera: 
			
			// query para obter usuarios e informacoes dos usuarios (autenticacao)
			public static final String DEF_USERS_BY_USERNAME_QUERY =
			"select username,password,enabled " +
			"from users " +
			"where username = ?";
			
			//query para obter autorizacoes do usuario (autorizacao)
			public static final String DEF_AUTHORITIES_BY_USERNAME_QUERY =
			"select username,authority " +
			"from authorities " +
			"where username = ?";
			
			public static final String DEF_GROUP_AUTHORITIES_BY_USERNAME_QUERY =
			"select g.id, g.group_name, ga.authority " +
			"from groups g, group_members gm, group_authorities ga " +
			"where gm.username = ? " +
			"and g.id = ga.group_id " +
			"and g.id = gm.group_id";
			
		
		Mas se a aplicacao precisa de queries diferentes das originais, podemos configura-las da
		seguinte forma: 
		
			protected void configure(AuthenticationManagerBuilder auth) throws Exception {
				auth
				.jdbcAuthentication()
					.dataSource(dataSource)
					.usersByUsernameQuery("select username, password, true " +	
						"from Spitter where username=?")
					.authoritiesByUsernameQuery("select username, 'ROLE_USER' from Spitter where username=?");
			}
			
	
		** para fazer uma criptografia da senha, chamar o .passwordEncoder(): 
			.passwordEncoder(new StandardPasswordEncoder("53cr3t"));
		O passwordEncoder aceita qualquer implementacao spring da interface PasswordEncoder.
			- BCryptPasswordEncoder
			- NoOpPasswordEncoder
			- StandardPasswordEncoder
			
		Podemos fornecer nossa propria implementacao do PasswordEncoder. A interface: 
			public interface PasswordEncoder {
				String encode(CharSequence rawPassword);
				boolean matches(CharSequence rawPassword, String encodedPassword);
			}	
		** a validacao será feita atraves do matches
		
		
	LDAP authentication
		
		O suporte para o ldap é fornecido pelo .ldapAuthentication:
		
			@Override
			protected void configure(AuthenticationManagerBuilder auth) throws Exception {
				auth
					.ldapAuthentication()
					.userSearchFilter("(uid={0})")
					.groupSearchFilter("member={0}");
			}
						
			Nessa configuracao, o userSearch e o groupSearch irão procurar o usuario e o grupo 
			na raiz do ldap. Para mudar o comportamente, adicionar o searchBase().
			
				.userSearchBase("ou=people")
				.userSearchFilter("(uid={0})")
				.groupSearchBase("ou=groups")
				.groupSearchFilter("member={0}");
			
			O SS vai tomar como base de procura o SearchBase. 
			
			A localizacao do servidor ldap se nao especificamos o local, é assumida no localhost 3389. 
			Para mudar, usar o contextSource(). 
			
					@Override
					protected void configure(AuthenticationManagerBuilder auth) throws Exception {
						auth
							.ldapAuthentication()
							.userSearchBase("ou=people")
							.userSearchFilter("(uid={0})")
							.groupSearchBase("ou=groups")
							.groupSearchFilter("member={0}")
								.contextSource()
								.url("ldap://habuma.com:389/dc=habuma,dc=com");
					}
								
								
			Podemos tambem usar embedded files do ldap para quando nao temos um servidor. Para isso usamos arquivos
			do tipo LDIF: 
				LDIF (LDAP Data Interchange Format)
			
			e é chamado atraves do 
				
				.contextSource()
					.root("dc=habuma,dc=com");
		
			O LDIF é um arquivo do tipo chave-valor, em plain text file, com os registros separados por blank line. 
				
				com somente o .root(), o ss vai procurar na raiz por todos os arquivos LDIF. Podemos restringir a busca
				usando:
				
					.contextSource()
						.root("dc=habuma,dc=com")
						.ldif("classpath:users.ldif");
				
*** Exemplo de arquivo ldif na pag 258


	
	NoSql authentication
		
		Se precisar autenticar um usuario em um banco nosql, temos que desenvolver nossa proprio codigo para isso em 
		uma classe que implemente a interface UserDatilsService
		
			public interface UserDetailsService {
				UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
			}
		
		
		Apos implementar a classe e o metodo loadUserByUsername(), precisamos adicionar 
		ao configure(): 
		
		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth
				.userDetailsService(new SpitterUserService(spitterRepository));
		}
		
		Podemos usar essa tecnica com bancos como mongo ou neo4j
		

	
	WEB
	
		Para proteger paginas, precisamos sobrescrever o metodo configure(HttpSecurity)
		
			@Override
			protected void configure(HttpSecurity http) throws Exception {
				http
					.authorizeRequests()
					.antMatchers("/spitters/me").authenticated()
					.antMatchers(HttpMethod.POST, "/spittles").authenticated()
					.anyRequest().permitAll();
			}
		
		 a primeira chamada a antMatchers diz que a pagina /splitters/me deve ser autenticada. 
		 a segunda, que as chamadas post ao /splittles deve ser
		 a terceira, qualquer outra chamada será permitida. 
		 
		 Podemos usar mais de uma chamada na mesma linha e usar wildcards tambem: 
		 
		 .antMatchers("/spitters/**", "/spittles/mine").authenticated();
		 
		 e usar expressoes regulares
		 .regexMatchers("/spitters/.*").authenticated();
		 
		 Se uma pagina exigir autenticated() e o usuario nao estiver logado, o spring security vai destinar 
		 o fluxo para a tela de login. 
		 
		 Existem mais metodos alem do athenticated() e do permitAll(): 
		 	access()
		 	anonymous()
		 	authenticated()
		 	denyAll()
		 	fullyAuthenticated()
		 	hasAnyAuthority()
		 	hasAnyRole()
		 	hasAuthority()
		 	hasIpAddress()
		 	hasRole()
		 	not()
		 	permitAll()
		 	rememberMe()
		 	
		Podemos aninhar todas as chamadas com os metodos tradicionais. No entanto, interpretacoes 
		sao feitas confome as declaracoes. Tomar cuidado para colocar da mais ampla para a menos. 
		
	Spring Security com Spring Expression (SpEL)
	
		.antMatchers("/spitter/me").access("hasRole('ROLE_SPITTER')")
		
		alem do hasRole() existem outros metodos que podem ser aplicados: 
		
			-authentication 
			denyAll
			hasAnyRola(list)
			hasRole(role)
			hasIpAdrress(ip)
			isAnonymous()
			isAuthenticated()
			isFullyAuthenticated()
			isRememberMe()
			permitAll
			principal
			
		Aninhando o hasRole() com o hasIpAddress() - as possibilidades sao infinitas de segurança
		.antMatchers("/spitter/me").access("hasRole('ROLE_SPITTER') and hasIpAddress('192.168.1.2')")		
	
	HTTPS
		Se precisamos que uma pagina seja transferida via HTTPS, podemos requierir um canal de comunicao 
		seguro:
		
			@Override
			protected void configure(HttpSecurity http) throws Exception {
				http
				.authorizeRequests()
					.antMatchers("/spitter/me").hasRole("SPITTER")
					.antMatchers(HttpMethod.POST, "/spittles").hasRole("SPITTER")
					.anyRequest().permitAll();
				.and()
				.requiresChannel()
					.antMatchers("/spitter/form").requiresSecure();
			}		 
		
		O requiredSecure() vai fazer com que todos os submits de /splitter/form sejam via https. Se tiver 
		alguma url que temos que tirar, podemos chamar o .requiresInsecure() e o trafego volta ao http.
		

	Autenticando usuarios
	
		As implementacoes de seguraça do spring fornecem a tela de login do usuario automaticamente
		até o momento que sobrescrevemos o configure(HttpSecurity). 
		
		No entanto, para chamar novamente a tela, usamos o metodo formLogin(): 
		
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
			.formLogin()
			.and()
			.authorizeRequests()
			.antMatchers("/spitter/me").hasRole("SPITTER")
			.antMatchers(HttpMethod.POST, "/spittles").hasRole("SPITTER")
			.anyRequest().permitAll();
			.and()
			.requiresChannel()
			.antMatchers("/spitter/form").requiresSecure();
		}
		
		
		Apartir dessa alteraçao do configure, se o usuario precisar autenticar, uma tela de login (bem simples) 
		será apresentada. 
		
		Para configurar uma tela de validação, definir um jsp (ou thymeleaf)
		
		<form name='f' action='/spittr/login' method='POST'>
			<table>
				<tr><td>User:</td><td>
				<input type='text' name='username' value=''></td></tr>
				<tr><td>Password:</td>
				<td><input type='password' name='password'/></td></tr>
				<tr><td colspan='2'>
				<input name="submit" type="submit" value="Login"/></td></tr>
				<input name="_csrf" type="hidden"
				value="6829b1ae-0a14-4920-aac4-5abbd7eeb9ee" />
			</table>
		</form>
		
		** o submit, user, passwd, submit e _csrf (se nao foi desabilitado) - csrf é o elemento que evita cross-site reference
		
		
		Para habilitar o basic-form authentication, tambem é no configure:
		Chamar o httpBasic() habilita o basicForm. O realmName() se um realm
		
			@Override
			protected void configure(HttpSecurity http) throws Exception {
				http
				.formLogin()
					.loginPage("/login")
				.and()
					.httpBasic()
						.realmName("Spittr")
				.and()
				...
			}
			
		
		RememberMe
			O rememberme faz com que o usuario seja lembrado por um tempo, nao precisando se autenticar novamente quando
			voltar a aplicacao. Por default o spring grava um cookie com validade de duas semanas: 
			** Como o tempo é em segundos, o tempo abaixo equivale a 4 semanas. 
			
				@Override
				protected void configure(HttpSecurity http) throws Exception {
					http
					.formLogin()
					.loginPage("/login")
					.and()
					.rememberMe()
						.tokenValiditySeconds(2419200)
						.key("spittrKey")
					...
				}
						
			Por default é gerar um MD5 de username, password, expiration time e um private key
			
			Para habilitar o remember-me, colocar na tela: 
				<input id="remember_me" name="remember-me" type="checkbox"/>
				<label for="remember_me" class="inline">Remember me</label>	
				
		
		LOGOUT
			O logout é um servlet filter que intercepta o /logout. 
			
			Em thymeleaf: <a th:href="@{/logout}">Logout</a>
			
			A classe que o fluxo é direcionado é o LogoutFilter e uma das coisas que ela faz é 
			remover o cookie do remember-me. 
			
			Apos deslogar, o usuario é direcionado para a tela de login novamente (/login?logout)
			
			Pomos mudar o comportamento com .logout(), que define as caracteristicas do logout: 
			
				.logout()
					.logoutSuccessUrl("/")
					.logoutUrl("/signout")	
			
			
	RENDERIZANDO PAGINAS COM SpringSecurity
	
		Tanto para JSP quanto para Thymeleaf, existem tags que podemos customizar para mostrar 
		paginas e conteudos dependendo das autorizacoes. 
		
		A taglib do Spring Security tem 3 tags: 
			<security:accesscontrollist> - renderiza o conteudo dentro se o usuario tiver autorizacao
											em um control list
		
			<security:authentication> - renderiza detalhes do current authentication
			
			<security:authorize> - renderiza o body se o usuario tiver autorizacao de um SpEL 
			
			
		Para usar, adicionar o namespace
			<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
			
		
		
		Authentication rules
			Hello <security:authentication property="principal.username" />!
			
			O que podemos acessar: 
				
				authorities: lista de GrantedAuthority (roles)
				credentials: comumente é o user password
				details: infomacoes adicionais como ip, session id,...
				principal: user principal
				
				
			Para adicionar a informacao em uma variavel?
				<security:authentication property="principal.username" var="loginId" scope="request" />
				
			** o scope default é o page
			
		Conditional Rendering
		
			Para fazer uma renderizacao condicional (dependendo dos roles do usuarios): 
			
			<sec:authorize access="hasRole('ROLE_SPITTER')">
				... conteudo....
			</sec:authorize>
					
			Aqui estamos usando o SpEL e podemos aninhar as condicoes: 
				
				<security:authorize access="isAuthenticated() and principal.username=='habuma'">
					<a href="/admin">Administration</a>
				</security:authorize>
			
		
		
		