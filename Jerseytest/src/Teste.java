import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


public class Teste {

	
	 @POST
	 @Path("/salvarComissao")
	 @Produces(MediaType.APPLICATION_JSON)
	 public boolean salvarComissaos(List<String> produtosComissao, String arg ) {

	  System.out.println(produtosComissao);
	  
	  /*
	  
	  long funcionario = 0;
	  List<Comissao> listaProdutoComissao = new ArrayList<Comissao>();
	  for (ProdutoComissao produtoComissao : produtosComissao) {
	   Comissao comissao = new Comissao();
	   
	   if(produtoComissao.getCodigo() != null)
	    comissao.setCodigo(produtoComissao.getCodigo());
	   
	   comissao.setCdFuncionario(produtoComissao.getCdFuncionario());
	   comissao.setCdProduto(produtoComissao.getCdProduto());
	   comissao.setComissao(produtoComissao.getComissao());
	   listaProdutoComissao.add(comissao);
	   funcionario = produtoComissao.getCdFuncionario();
	  }
	  System.out.println(funcionario);
	  return new ProdutoServiceImpl().salvarComissao(listaProdutoComissao, funcionario);
	  */
	  return false; 
	 }
	
	
	
	
}
