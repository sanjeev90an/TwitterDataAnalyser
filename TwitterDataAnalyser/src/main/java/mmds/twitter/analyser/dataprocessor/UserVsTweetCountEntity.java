package mmds.twitter.analyser.dataprocessor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserVsTweetCountEntity {
	@Id
	private long id;

	@Column
	private long count;

	public UserVsTweetCountEntity() {
	}

	public UserVsTweetCountEntity(long id, long count) {
		this.id = id;
		this.count = count;
	}

	public long getCount() {
		return count;
	}

	public long getId() {
		return id;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public void setId(long id) {
		this.id = id;
	}
}
