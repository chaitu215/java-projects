
class A {
	static{
		System.out.println("1、 A static {}");
	}
	
	{
		System.out.println("2、A instance {}");
	}
	
	public A(){
		System.out.println("3、A constructor");
	}
	
	public void sayA(){
		System.out.println("4、A asy a");
	}
	
}

class B extends A {
	static{
		System.out.println("5、 B static {}");
	}
	
	{
		System.out.println("6、B instance {}");
	}
	
	public B(){
		System.out.println("7、B constructor");
	}
	
	public void sayB(){
		System.out.println("8、B asy b");
	}
}

public class TestInit {
	
	public static void main(String[] args) {
		A a = new B();
		B b = (B)a;
		b.sayB();
		
		System.out.println("---------");
		A aa = new A();
		aa.sayA();
	}

}
