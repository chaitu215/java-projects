import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**   
 * @author: wangshengyong
 * @date: 2016年9月14日
 * @description: 两个从小到大的有序列表合并为有序列表
 */
public class TestSort {

	public static List<Integer> merge(List<Integer> a, List<Integer> b){
		if(a==null){return b;}
		if(b == null){return a;}
		List<Integer> c = new ArrayList<>();
		if(a.get(a.size()-1) <= b.get(0)){
			for(int i=0; i<a.size(); i++){
				c.add(a.get(i));
			}
			for(int i=0; i<b.size(); i++){
				c.add(b.get(i));
			}
		}else if(a.get(0) >= b.get(b.size()-1)){
			for(int i=0; i<b.size(); i++){
				c.add(b.get(i));
			}
			for(int i=0; i<a.size(); i++){
				c.add(a.get(i));
			}
		}else {
			int i = 0, j= 0;
			while(i < a.size() && j < b.size()){
				if(a.get(i) < b.get(j)){
					c.add(a.get(i));
					i++;
				}else{
					c.add(b.get(j));
					j++;
				}
			}
			if(i< a.size()){
				for(;i<a.size();i++){
					c.add(a.get(i));
				}
			}
			if(j< b.size()){
				for(;j<b.size();j++){
					c.add(b.get(j));
				}
			}
		}
		return c;
	}


	/**
	 *  1 2 3 6  ,  6 8 9 11
	 *  6 8 9 11 ,  1 2 3 6
	 *  1 3 7 9  ,  3 5 6 10    ->  1 3 3 5 6 7 9 10
	 * @param args
	 */
	public static void main(String[] args) {
		List<Integer> a = Arrays.asList(1,3,7,9,11);
		List<Integer> b = Arrays.asList(3,5,6,10,56);
		List<Integer> c = merge(a, b);
		System.out.println(c);
	}

}
