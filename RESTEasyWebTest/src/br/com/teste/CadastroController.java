package br.com.teste;

import java.util.Hashtable;

public class CadastroController {

	private static Hashtable<String,Usuario> htCadastro;
	
	static {
		htCadastro = new Hashtable<String,Usuario>(); 
	}
	
	
	
	public static void addUser(Usuario urs) {
		htCadastro.put(urs.getId(), urs);
	}
	
	
	public static Usuario getUser(String id) {
		return htCadastro.get(id); 
	}
	
	
	
	/*
	 *
	 * 
	 * GsonBuilder builder = new GsonBuilder(); 
	 * builder.setDateFormat("");
	 * 
	 * Gson gson = builder.create(); 
	 * return gson.toJson(obj);
	 * 
	 * 
	 * 
	 * 
	 */
	
	
}
