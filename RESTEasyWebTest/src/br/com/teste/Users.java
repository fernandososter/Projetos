package br.com.teste;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


@Path("/users")
public class Users {

	@POST
	@Path("/{nome}")
	public Response addUser(@PathParam("nome") String nome) {
		System.out.println("Adionando usuario " + nome );
		return Response.ok(200).build();
	}
	
	
	
	@PUT
	@Path("/{id: [0-9]}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateUser(@PathParam("id") String id) {
		Response.status(401).entity("MSG de Erro").build(); 
		return Response.ok().build();
	}
	
	@GET
	@Path("/getUsuario/{id: [0-9]}")
	@Produces(MediaType.APPLICATION_JSON)
	public Usuario getUser(@PathParam("id") String id) {
		return CadastroController.getUser(id); 
	}
	
	
	/**
	 * Converter de objeto para json
	 * @param obj
	 * @return
	 */
	private String toJson(Object obj) {
		GsonBuilder builder = new GsonBuilder(); 
		Gson gson = builder.create(); 
		return gson.toJson(obj); 
	}
	
	/**
	 * converter de json para objeto
	 * @param payload
	 * @return
	 */
	private Object toJava(String payload) {
		GsonBuilder builder = new GsonBuilder(); 
		Gson gson = builder.create(); 
		return gson.fromJson(payload, Usuario.class) ; 
	}
}
