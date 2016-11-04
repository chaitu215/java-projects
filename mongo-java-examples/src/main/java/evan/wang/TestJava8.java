package evan.wang;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**   
 * 
 * @author: wangshengyong
 * @date: 2016年11月2日
 */
public class TestJava8 {
	private String xx = "xx";

	@Override
	public String toString() {
		return "hello Test";
	}
	
	public void scope(){
		 Runnable r1 = () -> {System.out.println(this);System.out.println(this.getClass());};
		 Runnable r2 = () -> {System.out.println(toString());};
		 Runnable r3 = () -> {System.out.println(xx);};
		 Runnable r4 = () -> {String xx = "yy"; System.out.println(xx);};
		 r1.run();
		 r2.run();
		 r3.run();
		 r4.run();
	}
	
	/**
	 * 
	 * @param numbers
	 * @param p 谓词
	 * @return
	 */
	public int sumAll(List<Integer> numbers, Predicate<Integer> p) {  
	    int total = 0;  
	    for (int number : numbers) {  
	        if (p.test(number)) {  
	            total += number;  
	        }  
	    }  
	    return total;  
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//new Thread(() -> {System.out.println(a + b);});
		List<String> list = Arrays.asList("XXX","adfa","fasdf","Bffdas");
		Collections.sort(list, (String a, String b) -> a.compareToIgnoreCase(b) );
		System.out.println(list);
		
		Supplier<Runnable> c = () -> () -> { System.out.println("hi"); };
	    new Thread(c.get()).start();
	    TestJava8 test = new TestJava8();
	    test.scope();
	   
	    List<Integer> numbers = Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15);
	    test.sumAll(numbers, n -> true);
	    test.sumAll(numbers, n -> n % 2 ==0);
	    test.sumAll(numbers, n -> n > 3);
	}

}
