import java.text.DecimalFormat;
import java.text.NumberFormat;


public class Teste {

	public static void main(String...args) {
		
		String vlr = "005"; 
		System.out.println(Teste.format(vlr)); 
	
	}

	
	
	public static String format(String value) {
		
		
		while(value.length() < 3) {
			if(value.length() == 2 ) {
				value = "0."+value;
				break;
			} else {
				value = "0"+value; 
			}
		}
		
		if(value.length() >= 3 && value.indexOf(".") <0) {
			value = value.substring(0, value.length()-2)+"."+value.substring(value.length()-2, value.length());
		}
		return value; 
	}

}
