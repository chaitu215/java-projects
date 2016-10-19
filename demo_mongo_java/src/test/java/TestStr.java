import static org.hamcrest.CoreMatchers.describedAs;
import static org.hamcrest.CoreMatchers.instanceOf;

import java.util.LinkedList;

/**
 * 
 * @author: wangshengyong
 * @date: 2016年10月8日
 */
public class TestStr {
    
	/**
	 * 将字符串转换为字符数组
	 * 遍历字符数组，如果遇到左括号，将计数器加1，如果遇到右括号，将计数器减1
	 * 如果计算器count小于0，则右括号不匹配
	 * 如果计数器count大于0，则左括号不匹配
	 * 如果计数器count等于0，匹配
	 */
	public static boolean check(String str) {
		char[] attr = str.toCharArray();
		int count = 0;
		for (char c : attr) {
			if(c == '('){
				count++;
			} else if(c == ')'){
				count--;
				if(count < 0){
					return false;
				}
			}
		}
		return count == 0;
	}
	
	
	/**
	 * 将字符串转换为字符数组
	 * 遍历字符数组，如果遇到左括号则入栈，如果遇到右括号则从栈中弹出一个左括号。
	 * 如果栈不为空，则左括号没有正确匹配
	 * 如果栈为空，但是还需要弹出元素，则右括号没有正确匹配。
	 * 如果栈为空，也没有需要弹出的元素，括号序列正确
	 */
	public static boolean check2(String str) {
		char[] attr = str.toCharArray();
		LinkedList<Character> queue = new LinkedList<>();
		for (int i = 0; i < attr.length; i++) {
			Character c = attr[i];
			if (c == '(') {
				queue.push(c);
			} else if (c == ')') {
				if(queue.size()==0){
				   return false;	
				}
				queue.poll();
			}
		}
		return queue.size() == 0;
	}
	
	
	/**
	 * 求最大公约数
	 * 欧几里得算法
	 */
	public static int gcd(int a, int b){
		if(b == 0){
			return a;
		}
		return gcd(b, a % b);
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String str1 = "()())";
		String str2 = "(()())";
		String str3 = "((()())";
        System.out.println(check(str1));
        System.out.println(check(str2));
        System.out.println(check(str3));
        
        System.out.println(gcd(24, 16));
	}

}
