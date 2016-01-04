package mmds.twitter.analyser.dataprocessor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class UserSurveyResultEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long id;

	@Column
	private long userId;

	@Column
	private String labels;

	@Column(columnDefinition = "varchar(1000)")
	private String source;

	public UserSurveyResultEntity() {
	}

	public UserSurveyResultEntity(long userId, String labels, String source) {
		this.userId = userId;
		this.labels = labels;
		this.source = source;
	}

}
