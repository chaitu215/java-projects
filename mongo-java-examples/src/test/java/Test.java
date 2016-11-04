import java.math.BigDecimal;

/**   
 * @author: wangshengyong
 * @date: 2016年8月23日
 * @description: 
 */
public class Test {
	
	public static void main(String[] args) {
		float rate = ((float) 1652) / 56361;
		BigDecimal bg = new BigDecimal(rate);
		rate = bg.setScale(4, BigDecimal.ROUND_HALF_UP).floatValue();
		String bind_rate = ""+(rate * 100);
		System.out.println(bind_rate);
		System.out.println(0.1429*100);
		 String.format("%.2f", 0.12356) ;
		 random();
	}
	
	
	public static void random(){
		for(int i=0; i<100; i++){
			System.out.println(Math.random() *100);
		}
	}
	
}
