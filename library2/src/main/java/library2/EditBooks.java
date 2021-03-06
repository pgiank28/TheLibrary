package library2;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import entities.Books;
import libraryJPA2.LibManager;

@Produces("text/plain")
@Consumes("text/html")
@Path("/book")
public class EditBooks {

	@Inject
	private LibManager lmanager;
	
	@GET
	@Path("/{id}")
	public String getBook(@PathParam("id") long id) {
		return lmanager.getBook(id);
	}
	
	@POST
	@Path("/new")
	public String insertNewBook(@QueryParam("author") String author,@QueryParam("title") String title) {
		return lmanager.insertNewBook(author, title);
	}
	
	@POST
	@Path("/edit")
	public String updateBook(@QueryParam("id") long id,@QueryParam("author") String author,@QueryParam("title") String title) {
		Books b = new Books();
		b.setAuthor(author);
		b.setTitle(title);
		
		return lmanager.updateBook(b, id);
	}
	
	@POST
	@Path("/delete/{id}")
	public String deleteBook(@PathParam("id") long id) {
		return lmanager.deleteBook(id);
	}
}
