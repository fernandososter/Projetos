import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;


public class Main {

	
	public static void main(String...args) {
		
		SparkConf conf = new SparkConf().setMaster("local").setAppName("My1app");
		
		JavaSparkContext ctx = new JavaSparkContext(conf);
		JavaRDD<String> input = ctx.textFile(""); 
		
		
		
	}
	
	
}
