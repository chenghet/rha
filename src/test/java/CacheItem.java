import java.io.Serializable;
import java.util.Date;

public class CacheItem implements Serializable {
	private static final long serialVersionUID = 1L;

	private String key;
	private int status;
	private long anything;
	private String desc;
	private Date date;
	private Date date2;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getAnything() {
		return anything;
	}

	public void setAnything(long anything) {
		this.anything = anything;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate2() {
		return date2;
	}

	public void setDate2(Date date2) {
		this.date2 = date2;
	}
}
