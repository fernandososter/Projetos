package guava3;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.common.base.Function;

public class DateFormatFunction implements Function<Date,String> {

	@Override
	public String apply(Date input) {
		return  new SimpleDateFormat("dd/mm/yyyy").format(input); 
	}
}
