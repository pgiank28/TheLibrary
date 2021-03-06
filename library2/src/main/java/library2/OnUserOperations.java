package library2;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import libraryJPA2.LibManager;
import entities.User;


@Produces("text/plain")
@Consumes("text/html")
@Path("/user")
public class OnUserOperations {
	
	@Inject
	private LibManager lmanager;
	
	@POST
	@Path("/edit")
	public String updateUser(@QueryParam("id") long id,@QueryParam("username") String uname,@QueryParam("password") String pword,
			@QueryParam("fname") String fname,@QueryParam("lname") String lname,@QueryParam("email") String mail) {
		
		User u = new User();
		u.setFirstname(fname);
		u.setLastname(lname);
		u.setEmail(mail);
		u.setUsername(uname);
		u.setPassword(pword);
		
		return lmanager.updateUser(u, id);
	}
	
	@POST
	@Path("/delete/{id}")
	public String deleteUser(@PathParam("id") long id) {
		return lmanager.deleteUser(id);
	}
}
