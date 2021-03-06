package library2;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import libraryJPA2.LibManager;

@Produces("text/plain")
@Consumes("text/html")
@Path("/get")
public class GetReviews {

	@Inject
	private LibManager lmanager;
	
	@GET
	@Path("/usr/{id}")
	public String GetUserReviews(@PathParam("id")long id) {
		return lmanager.getReviews(id);
	}
	
	@GET
	@Path("/bok/{id}")
	public String GetBookReviews(@PathParam("id")long id) {
		return lmanager.getBookReviews(id);
	}
}
