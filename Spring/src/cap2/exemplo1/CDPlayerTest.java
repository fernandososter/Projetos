package cap2.exemplo1;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class) // vai criar automaticamente um context quando iniciar o teste
@ContextConfiguration(classes=CDPlayerConfig.class) // diz qual configuration usar. 
									//Como CDPlayerConfig tem um componentscan, vai procurar o bean automaticamente
public class CDPlayerTest {
	@Autowired
	private CompactDisc cd; 
	
	@Test
	public void cdShouldNotBeNull() {
		assertNotNull(cd); 
	}
}
