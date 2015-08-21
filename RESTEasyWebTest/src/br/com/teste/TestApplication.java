package br.com.teste;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


@ApplicationPath("/")
public class TestApplication extends Application {
	    @SuppressWarnings("unchecked")
	    public Set<Class<?>> getClasses() {
	        return new HashSet<Class<?>>(Arrays.asList(BookSeller.class,Users.class));
	    }
	
}
