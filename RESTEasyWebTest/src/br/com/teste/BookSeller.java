package br.com.teste;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement(name = "bookseller")
@Path("/bookseller")	
public class BookSeller {

	
	@GET
	@Produces({"application/xml"})
	@Path("/status")
	public Response getStatus() {
		return Response.ok("testado 123").build(); 
	}
	
	
	
	
	
}
