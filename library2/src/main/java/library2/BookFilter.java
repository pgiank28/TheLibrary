package library2;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;

import utils.BCrypt;
import libraryJPA2.LibManager;
import entities.User;
import utils.UserSession;
import java.util.Base64.Decoder;

import org.apache.log4j.Logger;

public class BookFilter implements ContainerRequestFilter {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String AUTHORIZATION_HEADER_PREFIX = "Basic ";
	private static final String SECURED_URL_PREFIX = "book";
	private static final String SECURED_URL_PREFIX_2 = "review";
	private static final String SECURED_URL_PREFIX_3 = "user";
	
	@Inject
	private LibManager lmanager;
	
	@Inject
	private UserSession session;
	
	private Logger log = Logger.getLogger(BookFilter.class);
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		if(requestContext.getUriInfo().getPath().contains(SECURED_URL_PREFIX)
				|| requestContext.getUriInfo().getPath().contains(SECURED_URL_PREFIX_2)
				|| requestContext.getUriInfo().getPath().contains(SECURED_URL_PREFIX_3)) {
			
			List<String> authorized = requestContext.getHeaders().get(AUTHORIZATION_HEADER);
			if(authorized != null && authorized.size()>0) {
				String authToken = authorized.get(0);
				authToken = authToken.replace(AUTHORIZATION_HEADER_PREFIX, "");
			
				Decoder dec = Base64.getDecoder();
			
				String[] chunks = authToken.split("\\.");
				String header = new String(dec.decode(chunks[0]));
				String[] credentials = header.split(":");
				String username = new String(credentials[0]);
				String password = new String(credentials[1]);
				log.warn(username);
				log.warn(password);
				for(User i:lmanager.getRegisteredUsers()) {
					if(username.equals(i.getUsername()) && BCrypt.bcryptHash(password).equals(i.getPassword())) {
						log.info("Authorized user entered");
						session.setUsername(username);
						session.setUserid(i.getId());
						return;
					}
				}
			}
			
			log.warn("Unauthorized user tried to access a resource");
			Response unauthorized = Response.status(Response.Status.UNAUTHORIZED).entity("User can't access the resource").build();
			requestContext.abortWith(unauthorized);
		}
	}

}
