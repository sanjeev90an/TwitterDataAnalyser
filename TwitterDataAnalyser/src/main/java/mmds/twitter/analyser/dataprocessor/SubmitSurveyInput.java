package mmds.twitter.analyser.dataprocessor;

import java.util.List;

public class SubmitSurveyInput {

	private long userId;

	private List<String> surveyResults;

	public SubmitSurveyInput() {
	}

	public SubmitSurveyInput(String json) {
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public List<String> getSurveyResults() {
		return surveyResults;
	}

	public void setSurveyResults(List<String> surveyResults) {
		this.surveyResults = surveyResults;
	}
}
