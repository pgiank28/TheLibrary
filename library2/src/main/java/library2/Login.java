package library2;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;


import utils.BCrypt;
import libraryJPA2.LibManager;
import entities.User;

@Consumes("text/html")
@Produces("text/json")
@Path("/login")
public class Login {
	
	@Inject
	private LibManager lmanager;
	
	@POST
	public String Authentication(@PathParam("username") String uname,@PathParam("password") String password) {
		List<User> authUsers = lmanager.getRegisteredUsers();
		
		for(User u:authUsers) {
			if(u.getUsername().equals(uname) && u.getPassword().equals(BCrypt.bcryptHash(password))) {
				return createToken(u.getUsername());
			}
		}
		return "This account doesn't exist";
	}
	
	public String createToken(String username) {

	    StringBuilder header = new StringBuilder();
	    StringBuilder payload = new StringBuilder();
	    StringBuilder verify_signature = new StringBuilder();
	    
	    Date exp = new Date(System.currentTimeMillis()+10^6);
	    
	    header.append("{alg:HS256,type:JWT}");
	    payload.append("{sub:"+username)
	            .append("exp:"+exp.toString())
	            .append("name:"+username+"}");
	    
	    verify_signature.append("{HMACSHA256(base64UrlEncode(header) + base64UrlEncode(payload)");
	    
	    return header.toString()+payload.toString()+verify_signature.toString();
		
	}
}
