package cap2.exemplo1;

import org.springframework.stereotype.Component;

@Component
public class SgtPeppers implements CompactDisc{

	private String title = "Sgt. Peepers lonly"; 
	private String artist = "the beatles"; 
	
	@Override
	public void play() {
		System.out.println("playing " + title + " by " + artist);
		
	}

}
