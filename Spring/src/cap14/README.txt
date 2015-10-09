This chapter covers
- Securing method invocations
- Defining security rules with expressions
- Creating security expression evaluators




	Esquemas de segurança complementar para metodos que rodam por tras das cenas. 
	
	usa: 
		- @Secured do Spring Security
		- @RolesAllowe da JSR250
		- Expression-driven annotation: @PreAuthorize, @PostAuthorize, @PreFilter e  @PostFilter
	
	Para poder usar as configurações de segurança precisamos colocar o @EnableGlobalMethodSecurity
	em um dos configurations: 
	
		@Configuration
		@EnableGlobalMethodSecurity(securedEnabled=true)
		public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
	
		}
			
			
	Extender a classe GlobalMethodSecurityConfiguration nos permite afinar algumas configuracoes: 
	
		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth
			.inMemoryAuthentication()
			.withUser("user").password("password").roles("USER");
		}
	
	O Spring Security usa AOP? Quando colocamos o securedEnable=true ele cria o aspect? 
	
	@Secured
		
		
		Quando usamos o @Secure, a lista de roles é transformada em array e o usuario que esta invocando 
		o addSpittle deve ter ao menos um dos roles. 
		
			@Secured({"ROLE_SPITTER", "ROLE_ADMIN"})
			public void addSpittle(Spittle spittle) {
				// ...
			}
				
			
	Se o usuario nao tiver os roles ira ocorrer uma exception (AuthenticationException ou AccessDeniedException)
	
	No entanto o Secured é uma annotation do Spring. Para usar uma do java é melhor usar da 
	JSR250, @RolesAllowed. 
	
	Para poder usar o @RolesAllowed (o funcionamento é igual ao @Secured), precisamos alterar
	o @EnableGlobalMethodSecurity: jsr250Enabled=true
	
		@Configuration
		@EnableGlobalMethodSecurity(jsr250Enabled=true)
		public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
			...
		}
		
	No entanto o security do spring e da jsr250 nao sao mutualmente exclusivas, podemos 
	usar ambos ao mesmo tempo. 
	
	Ex: 
	
		@RolesAllowed("ROLE_SPITTER")
		public void addSpittle(Spittle spittle) {
			// ...
		}
	
	
	Para aumentar o uso do SpEL com questoes de segurança, apartir do Spring 3 foram introduzidos
	novas annotations: 
	
		- @PreAuthorize		restringe o acesso a um metodo antes da invocacao baseado no SpEL
		- @PostAuthorize	permite a chamada do metodo mas se o SpEL for falso throws Exception
		- @PostFilter 		permite um metodo ser chamado mas filtra o resultado com uma expressao
		- @PreFilter		permite a chamada do metodo, mas filtra os inputs do metodo
	
	
	para habilitar o uso dos annotations acima, precisamos adicionar o elemento: 
	@EnableGlobalMethodSecurity com configuracao prePostEnable=true
	
	
		@Configuration
		@EnableGlobalMethodSecurity(prePostEnabled=true)
		public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
		}
		
	
	A diferenca entre @PreAuthorize e @PostAuthorize é que: 
		@PreAuthorize: a condicao SpEL descrita será avaliada antes do processo ser executado. 
		@PostAuthorize: a condicao SpEL será avaliada apos a execucao do metodo. 
		
		
	Ex de como usar o @preauthorize igual o @Secured ou @RolesAllowed
	
		@PreAuthorize("hasRole('ROLE_SPITTER')")
		public void addSpittle(Spittle spittle) {
			// ...
		}	
			
	
	Exemplo de uso mais avançado, que verifica alumas caracterisca dos inputs do metodo: 
	
			@PreAuthorize(
				"(hasRole('ROLE_SPITTER') and #spittle.text.length() <= 140)"
				+"or hasRole('ROLE_PREMIUM')")
			public void addSpittle(Spittle spittle) {
				// ...
			}
	** o nome usado no SpEL é o mesmo do input. 
	
	Para usar o @PostAuthorize, o metodo será executado e entao a condicao validada: 
	
	Ex: validar se a informação sendo retornada pertence ao usuario que esta solicitando 
	
		@PostAuthorize("returnObject.spitter.username == principal.username")
		public Spittle getSpittleById(long id) {
			// ...
		}
			
	Se a condicao for falsa (nao for do usuario), será retornada uma AccessDeniedException. 
	
	
	O @PostFilter vai atuar no objeto que esta sendo retornado. Se for uma lista, ele vai remover 
	os elementos que a condicao nao for true. 
	
		@PreAuthorize("hasAnyRole({'ROLE_SPITTER', 'ROLE_ADMIN'})")
		@PostFilter( "hasRole('ROLE_ADMIN') || "
			+ "filterObject.spitter.username == principal.name")
		public List<Spittle> getOffensiveSpittles() {
			...
		}	
	
	
	Ja o @PreFilter vai atuar na lista de entrada do metodo: 
	
		@PreAuthorize("hasAnyRole({'ROLE_SPITTER', 'ROLE_ADMIN'})")
		@PreFilter( "hasRole('ROLE_ADMIN') || "
			+ "targetObject.spitter.username == principal.name")
		public void deleteSpittles(List<Spittle> spittles) { ... }
	
	
	
	
	Existe a opção de desenolver nossas proprias funcoes SpEL para agrupar as verificacoes
	quando elas começarem a ficar grandes e complexar: 
	
	Ex: 
		@PreAuthorize("hasAnyRole({'ROLE_SPITTER', 'ROLE_ADMIN'})")
		@PreFilter("hasPermission(targetObject, 'delete')")
		public void deleteSpittles(List<Spittle> spittles) { ... }
	
	e a funcao hasPermission vem: 
	
			public class SpittlePermissionEvaluator implements PermissionEvaluator {
		
				private static final GrantedAuthority ADMIN_AUTHORITY = new GrantedAuthorityImpl("ROLE_ADMIN");
				
				public boolean hasPermission(Authentication authentication,Object target, Object permission) {
					if (target instanceof Spittle) {
						Spittle spittle = (Spittle) target;
						String username = spittle.getSpitter().getUsername();
						if ("delete".equals(permission)) {
							return isAdmin(authentication) ||username.equals(authentication.getName());
						}
					}
					throw new UnsupportedOperationException("hasPermission not supported for object <" + target
							+ "> and permission <" + permission + ">");
				}
			
				public boolean hasPermission(Authentication authentication,
						Serializable targetId, String targetType, Object permission) {
					throw new UnsupportedOperationException();
				}
				
				private boolean isAdmin(Authentication authentication) {
					return authentication.getAuthorities().contains(ADMIN_AUTHORITY);
				}
			}
				
			
			
	
	
	
	
	
	
	