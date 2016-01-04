package mmds.twitter.analyser.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mmds.twitter.analyser.dataprocessor.SubmitSurveyInput;
import mmds.twitter.analyser.dataprocessor.UserDataModel;
import mmds.twitter.analyser.utils.JSONUtils;

@Path("/submitSurvey")
public class SubmitSurvey {

	private UserDataModel userModel = UserDataModel.INSTANCE;

	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@POST
	public Response submitSurvey(@Context HttpHeaders headers, String inputDataJson) {
		SubmitSurveyInput inputData;
		String headerString;
		try {
			inputData = JSONUtils.parseJSON(inputDataJson, SubmitSurveyInput.class);
			headerString = JSONUtils.getJsonStringForObject(headers.getRequestHeader("user-agent"));
		} catch (Exception e) {
			return Response.serverError().build();
		}
		userModel.submitSurveyForUser(inputData.getUserId(), inputData.getSurveyResults(), headerString);
		return Response.ok().build();
	}
}
