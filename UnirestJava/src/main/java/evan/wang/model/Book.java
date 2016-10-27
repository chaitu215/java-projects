package evan.wang.model;

public class Book {
	private int id;
	private String bookName;
	private String bookDesc;
	private String auth;
	
	public Book() {
		super();
	}
	public Book(int id, String bookName, String bookDesc, String auth) {
		super();
		this.id = id;
		this.bookName = bookName;
		this.bookDesc = bookDesc;
		this.auth = auth;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getBookName() {
		return bookName;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	public String getBookDesc() {
		return bookDesc;
	}
	public void setBookDesc(String bookDesc) {
		this.bookDesc = bookDesc;
	}
	public String getAuth() {
		return auth;
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}
	@Override
	public String toString() {
		return "Book [id=" + id + ", bookName=" + bookName + ", bookDesc=" + bookDesc + ", auth=" + auth + "]";
	}
	
	
	
}
