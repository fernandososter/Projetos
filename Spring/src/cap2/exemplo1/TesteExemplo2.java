package cap2.exemplo1;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.StandardOutputStreamLog;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=CDPlayerConfig.class)
public class TesteExemplo2 {

	@Rule
	public final StandardOutputStreamLog log = new StandardOutputStreamLog(); 
	
	@Autowired
	private MediaPlayer player; 
	
	@Autowired
	private CompactDisc cd; 
	
	@Test 
	public void play() { 
		player.play(); 
		assertEquals("123",log.getLog()); 
	}
}
