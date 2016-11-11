package evan.wang;

/**
 * @author: wangshengyong
 * @date: 2016年8月12日
 * @description:
 */
public class TestLinkCircle {
	
	/**
	 * 链表节点
	 */
	public static class Node<T> {
	    private T value;
	    private Node<T> next;
	    
		public Node(T value) {
			super();
			this.value = value;
		}

		public void setValue(T value) {
			this.value = value;
		}

		public T getValue() {
			return value;
		}

		public Node<T> next() {
			return next;
		}

		public void setNext(Node<T> next) {
			this.next = next;
		}
		
	}

	/**
	 * 判断是否存在闭环
	 * 使用快慢针，慢指针每次前进一个，快指针每次前进两个，如果存在闭环必定会出现快慢指针相遇。
	 */
	public boolean hasCircle(Node<Integer> node) {
		Node<Integer> slow = node;
		Node<Integer> fast = node;
		while (node.next() != null) {
			slow = slow.next();
			fast = fast.next();
			if (fast != null && fast.next() != null) {
				fast = fast.next();
			} else {
				return false;
			}
			if(slow==fast){
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		Node<Integer> node1 = new Node<>(8);
		Node<Integer> node2 = new Node<>(9);
		Node<Integer> node3 = new Node<>(10);
		node1.setNext(node2); // 1 -> 2
		node2.setNext(node3); // 2 -> 3
		node3.setNext(node1); // 3 -> 1
		TestLinkCircle test = new TestLinkCircle();
		System.out.println(test.hasCircle(node1));
	}

}
