package cap2.exemplo2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cap2.exemplo1.CDPlayer;
import cap2.exemplo1.CompactDisc;
import cap2.exemplo1.SgtPeppers;

/**
 * Aqui sem o ComponentScan, temos que declarar os bean. 
 * @author fsoster
 *
 */
@Configuration
public class CDPlayerConfigManual {
	
	@Bean
	public CompactDisc sgtPeppers() {
		return new SgtPeppers(); 
	}
	
	@Bean
	public CDPlayer cdPlayer() {
		return new CDPlayer(sgtPeppers()); 
	}
	
	
}
