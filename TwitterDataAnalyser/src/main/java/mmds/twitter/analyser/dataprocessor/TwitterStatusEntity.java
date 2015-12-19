package mmds.twitter.analyser.dataprocessor;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import twitter4j.GeoLocation;
import twitter4j.Place;
import twitter4j.Status;

@Entity
public class TwitterStatusEntity {

	@Id
	private long id;

	@Column
	private Date createdAt;

	@Column
	private String text;

	@Column
	private String source;

	@Column
	private double geoLongitude;

	@Column
	private double geoLatitude;

	@Column
	private int favoriteCount;

	@ManyToOne
	private UserEntity userEntity;

	@Column
	private String lang;

	@Column
	private String country;

	@Column
	private String placeName;

	@Column
	private String countryCode;

	// private boolean isRetweeted;
	//
	// private boolean isRetweet;
	//
	// private int retweetCount;
	//
	// private boolean isRetweetedByMe;
	//
	// private long currentUserRetweetId;

	public TwitterStatusEntity() {
	}

	public TwitterStatusEntity(Status status) {
		this.id = status.getId();
		this.createdAt = status.getCreatedAt();
		this.text = status.getText();
		this.lang = status.getLang();
		this.source = status.getSource();
		this.favoriteCount = status.getFavoriteCount();
		Place place = status.getPlace();
		if (place != null) {
			this.country = place.getCountry();
			this.countryCode = place.getCountryCode();
			this.placeName = place.getName();
		}
		GeoLocation geoLocation = status.getGeoLocation();
		if (geoLocation != null) {
			this.geoLatitude = geoLocation.getLatitude();
			this.geoLongitude = geoLocation.getLongitude();
		}
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public double getGeoLongitude() {
		return geoLongitude;
	}

	public void setGeoLongitude(double geoLongitude) {
		this.geoLongitude = geoLongitude;
	}

	public double getGeoLatitude() {
		return geoLatitude;
	}

	public void setGeoLatitude(double geoLatitude) {
		this.geoLatitude = geoLatitude;
	}

	public int getFavoriteCount() {
		return favoriteCount;
	}

	public void setFavoriteCount(int favoriteCount) {
		this.favoriteCount = favoriteCount;
	}

	@JsonIgnore
	public UserEntity getUserEntity() {
		return this.userEntity;
	}

	public void setUserEntity(UserEntity userEntity) {
		this.userEntity = userEntity;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

}
