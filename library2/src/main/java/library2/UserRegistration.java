package library2;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import libraryJPA2.LibManager;
import entities.User;

@Produces("text/plain")
@Consumes("text/html")
@Path("/register")
public class UserRegistration {

	@Inject
	private LibManager lmanager;
	
	@POST
	@Path("/new")
	public String userRegistration(@QueryParam("username") String uname,@QueryParam("password") String pword,
			@QueryParam("fname") String fname,@QueryParam("lname") String lname,@QueryParam("email") String mail) {
		User u = new User();
		u.setFirstname(fname);
		u.setLastname(lname);
		u.setEmail(mail);
		u.setUsername(uname);
		u.setPassword(pword);
		
		return lmanager.registerNewUser(u);
	}
}
