package soap;

import java.util.List;

import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import entities.Review;
import libraryJPA2.LibManager;


@WebService
@SOAPBinding(style=SOAPBinding.Style.RPC,
use=SOAPBinding.Use.ENCODED)
public class BestReviews {

	@Inject
	private LibManager lmanager;
	
	public BestReviews() {}
	
	@WebMethod
	public String getBestBooks() {
		List<Review> b = lmanager.getBestReviews();
		
		StringBuilder builder = new StringBuilder();
		b.forEach(bk->{
			builder.append(bk.toString());
		});
		
		return builder.toString();
	}
}
