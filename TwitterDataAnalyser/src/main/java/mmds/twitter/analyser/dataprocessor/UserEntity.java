package mmds.twitter.analyser.dataprocessor;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import twitter4j.User;

@Entity
public class UserEntity {

	@Id
	private long id;

	@Column
	private String screenName;

	@Column
	private String name;

	@Column
	private String location;

	@Column
	private String description;

	@Column
	private String url;

	@Column
	private long followerCount;

	@Column
	private String status;

	@Column
	private Date createdAt;

	@Column
	private String timezone;

	@Column
	private String lang;

	@Column
	private long statusCount;

	@Column
	private boolean geoEnabled;

	@Transient
	private List<TwitterStatusEntity> tweets;

	public UserEntity() {
	}

	public UserEntity(User user) {
		this.id = user.getId();
		this.screenName = user.getScreenName();
		this.name = user.getName();
		this.location = user.getLocation();
		this.description = user.getDescription();
		this.url = user.getURL();
		this.followerCount = user.getFollowersCount();
		this.status = user.getStatus() != null ? user.getStatus().getText() : "";
		this.createdAt = user.getCreatedAt();
		this.timezone = user.getTimeZone();
		this.lang = user.getLang();
		this.statusCount = user.getStatusesCount();
		this.geoEnabled = user.isGeoEnabled();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getFollowerCount() {
		return followerCount;
	}

	public void setFollowerCount(long followerCount) {
		this.followerCount = followerCount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public long getStatusCount() {
		return statusCount;
	}

	public void setStatusCount(long statusCount) {
		this.statusCount = statusCount;
	}

	public boolean isGeoEnabled() {
		return geoEnabled;
	}

	public void setGeoEnabled(boolean geoEnabled) {
		this.geoEnabled = geoEnabled;
	}

	public List<TwitterStatusEntity> getTweets() {
		return tweets;
	}

	public void setTweets(List<TwitterStatusEntity> tweets) {
		this.tweets = tweets;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserEntity other = (UserEntity) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
