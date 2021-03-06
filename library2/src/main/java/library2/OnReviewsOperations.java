package library2;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import libraryJPA2.LibManager;
import entities.Books;
import entities.Review;

@Produces("text/plain")
@Consumes("text/html")
@Path("/review")
public class OnReviewsOperations {

	@Inject
	private LibManager lmanager;
	
	@POST
	@Path("/new")
	public String insertReview(@QueryParam("stars") int stars,@QueryParam("comments") String comments,
			@QueryParam("userid") long userid,@QueryParam("bookid") long bookid) {
		Review r = new Review();
		
		r.setBookid(bookid);
		r.setComments(comments);
		r.setStars(stars);
		r.setUserid(userid);
		
		return lmanager.setReview(r);
	}
	
	@POST
	@Path("/edit")
	public String updateReview(@QueryParam("id") long id,@QueryParam("stars") int stars,@QueryParam("comments") String comments,
			@QueryParam("userid") long userid,@QueryParam("bookid") long bookid) {
		Review r = new Review();
		
		r.setBookid(bookid);
		r.setComments(comments);
		r.setStars(stars);
		r.setUserid(userid);
		
		return lmanager.updateReview(r, id);
	}
	
	@POST
	@Path("/delete/{id}")
	public String deleteReview(@PathParam("id") long id) {
		return lmanager.deleteReview(id);
	}
	
}
