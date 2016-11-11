package test;

/**
 * 作者：wangsy
 * 日期：2016/7/28 14:08
 * 描述：
 */
public class Test {

    public static void main(String[] args){
        Integer int1 = new Integer(10);
        int int2 = 10;
        Integer int3 = new Integer(10);
        Long long1 = new Long(10);
        long long2 = 10L;
        Float f1 = new Float(10.23f);
        double f2 = 10.23f;
        double d1 = 10.23;
        System.out.println(int2 == int1);
        System.out.println(int1 == int3);
        System.out.println(long1 == long2);
        System.out.println(int2 == long1);
        System.out.println(int1 == long2);
        System.out.println(f1 == f2);
        System.out.println(f1 == d1);
    }

}
