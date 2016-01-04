package mmds.twitter.analyser.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import mmds.twitter.analyser.dataprocessor.UserDataModel;
import mmds.twitter.analyser.dataprocessor.UserEntity;

@Path("/getTweetsForUser")
public class GetTweetsController {

	private static ObjectMapper objectMapper = new ObjectMapper();

	{
		objectMapper.disable(MapperFeature.AUTO_DETECT_CREATORS, MapperFeature.AUTO_DETECT_FIELDS,
				MapperFeature.AUTO_DETECT_GETTERS, MapperFeature.AUTO_DETECT_IS_GETTERS);
	}

	private UserDataModel userDataModel = UserDataModel.INSTANCE;

	@GET
	@Produces(value = MediaType.APPLICATION_JSON)
	public Response getTweets() {
		UserEntity nextUser = userDataModel.getNextUser(10);
		String nextUserJson;
		try {
			nextUserJson = objectMapper.writeValueAsString(nextUser);
		} catch (JsonProcessingException e) {
			return Response.serverError().build();
		}
		return Response.ok(nextUserJson).build();
	}
}
