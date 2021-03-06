package libraryJPA2;

import java.sql.Timestamp;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.apache.log4j.Logger;

import utils.UserSession;
import entities.*;


@ApplicationScoped
public class LibManager {

	@PersistenceContext(type=PersistenceContextType.TRANSACTION)
	protected EntityManager emanager;
	
	static Logger log = Logger.getLogger(LibManager.class);
	
	@Inject
	private UserSession session;
	
	public String getReviews(long id) {
		try {
			TypedQuery<Review> query = emanager.createQuery("SELECT r FROM Review r WHERE r.userid=:id",Review.class);
			query.setParameter("id", id);
			List<Review> userReviews = query.getResultList();
		
			if(userReviews.isEmpty()) {
				log.warn("Request for user reviews returned an empty set");
				return "Empty";
			}
			StringBuilder r = new StringBuilder();
		
			for(Review rr:userReviews) {
				r.append("User :"+id+" For book :"+rr.getBookid()+" Wrote:"+rr.getComments()+" STARS:"+rr.getStars());
			}
		
			log.info("User reviews send to the client");
			return r.toString();
		}catch(Exception e) {
			log.error("Error querying database: "+e.getStackTrace().toString());
			return "error";
		}
	}
	
	public String getBookReviews(long id) {
		try {
			TypedQuery<Review> query = emanager.createQuery("SELECT r FROM Review r WHERE r.bookid=:id",Review.class);
			query.setParameter("id", id);
			List<Review> userReviews = query.getResultList();
		
			if(userReviews.isEmpty()) {
				log.warn("Request for book reviews returned an empty set");
				return "empty";
			}
			StringBuilder r = new StringBuilder();
		
			for(Review rr:userReviews) {
				r.append("User :"+rr.getUserid()+" For book :"+id+" Wrote:"+rr.getComments()+" STARS:"+rr.getStars());
			}
			log.info("Book reviews send");
			return r.toString();
		}catch(Exception e) {
			log.error("Error querying database: "+e.getStackTrace().toString());
			return "Error";
		}
	}
	
	@Transactional
	public String getBook(long id) {
		try {
			Books b = emanager.find(Books.class, id);
			StringBuilder r = new StringBuilder();
			
			if(!isBookOwner(id)) {
				return "Error";
			}
			r.append("Book received:\n");
			r.append("id = "+id+"\n");
			r.append("title = "+b.getTitle()+"\n");
			r.append("author = "+b.getAuthor()+"\n");
			
			log.info("Received book with id = "+id+" from "+session.getUsername());
			
			return r.toString();
		}catch(Exception e) {
			log.error("Request can't be processed "+e.getStackTrace().toString());
			return e.getMessage();
		}
	}
	
	@Transactional
	public String insertNewBook(String author,String title) {
		
		Books b=new Books();
		b.setAuthor(author);
		b.setTitle(title);
		try {
			emanager.persist(b);
			log.info("Book inserted: "+b.toString());
			logWrite(session.getUserid(),"INSERT","Book inserted "+b.toString());
		}catch(Exception e) {
			log.error("Can't insert this book to the library "+e.getStackTrace().toString());
			return e.getMessage();
		}
		emanager.flush();
		
		try {
			Owns o = new Owns();
			o.setBookid(b.getId());
			o.setUid(session.getUserid());
			emanager.persist(o);
			logWrite(session.getUserid(),"UPDATE","Book ownser is "+session.getUserid());
			return "Book inserted successfully";
			
		}catch(Exception e) {
			log.error("Can't insert this book ownership to the library "+e.getStackTrace().toString());
			return e.getMessage();
		}
		
	}
	
	@Transactional
	public String updateBook(Books b,long id) {
		try {
			Books updated = emanager.find(Books.class, id);
			
			if(!isBookOwner(id)) {
				log.warn("User "+session.getUsername()+" tried to update book without ownership");
				return "Permission denied";
			}
			updated.setAuthor(b.getAuthor()==null?updated.getAuthor():b.getAuthor());
			updated.setTitle(b.getTitle()==null?updated.getTitle():b.getTitle());
		
			log.info("Book updated: "+updated.toString());
			logWrite(session.getUserid(),"UPDATE","Book updated "+updated.toString());
			
			return "Book inserted successfully";
		}catch(Exception e) {
			log.error("Can't update the book info "+e.getStackTrace().toString());
			return e.getMessage();
		}
	}
	
	@Transactional
	public String deleteBook(long id) {
		try {
			Books b=emanager.find(Books.class, id);
			
			if(!isBookOwner(id)) {
				log.warn("User "+session.getUsername()+" tried to delete a book without permission");
				return "Permission denied";
			}
			emanager.remove(b);
		
			log.info("Book deleted: "+b.toString());
			logWrite(session.getUserid(),"DELETE","Book deleted "+b.toString());
			
			return "Book deleted successfully";
		}catch(Exception e) {
			log.error("Book can't be deleted: "+e.getStackTrace().toString());
			return e.getMessage();
		}
	}
	
	@Transactional
	public String registerNewUser(User u) {
		try {
			List<User> curUsers = getRegisteredUsers();
			for(User a:curUsers) {
				if(a.getUsername().equals(u.getUsername())) {
					log.warn("Username taken");
					return "Username already taken";
				}
			}
			
			emanager.persist(u);
			log.info("New user registered to the system");
			log.info(u.getId());
			logWrite(-1,"INSERT","User registered "+u.toString());
			return "User registered successfully";
		}catch(Exception e) {
			log.error("Can't create new user: "+e.getStackTrace().toString());
			return e.getMessage();
		}
	}
	
	@Transactional
	public String updateUser(User u,long id) {
		try {
			User updated = emanager.find(User.class,id);
		
			updated.setEmail(u.getEmail()==null?updated.getEmail():u.getEmail());
			updated.setFirstname(u.getFirstname()==null?updated.getFirstname():u.getFirstname());
			updated.setLastname(u.getLastname()==null?updated.getLastname():u.getLastname());
			updated.setUsername(u.getUsername()==null?updated.getUsername():u.getUsername());
			updated.setPassword(u.getPassword()==null?updated.getPassword():u.getPassword());
		
			emanager.persist(updated);
		
			log.info("User updated :"+updated.toString());
			logWrite(id,"UPDATE","User updated "+updated.toString());
			
			return "User updated successfully";
		}catch(Exception e) {
			log.error("User can't be updated: "+e.getStackTrace().toString());
			return e.getMessage();
		}
	}
	
	@Transactional
	public String deleteUser(long id) {
		try {
			User u = emanager.find(User.class, id);
			emanager.remove(u);
			log.info("User deleted: "+u.toString());
			logWrite(id,"DELETE","User deleted "+u.toString());
			
			return "User deleted successfully";
		}catch(Exception e) {
			log.error("User can't be deleted: "+e.getStackTrace().toString());
			return e.getMessage();
		}
	}
	
	@Transactional
	public String setReview(Review r) {
		try {
			emanager.persist(r);
			log.info("Reviews inserted: "+r.toString());
			logWrite(session.getUserid(),"INSERT","Review inserted "+r.toString());
			
			return "Review inserted successfully";
		}catch(Exception e) {
			log.error("Review can't be created: "+e.getStackTrace().toString());
			return e.getMessage();
		}
	}
	
	@Transactional
	public String updateReview(Review r,long id) {
		try {
			Review updated = emanager.find(Review.class,id);
		
			if(!isReviewOwner(id)) {
				log.warn("User "+session.getUsername()+" tried to update review without owning it");
				return "Permission denied";
			}
			
			updated.setBookid(r.getBookid()==0?updated.getBookid():r.getBookid());
			updated.setComments(r.getComments()==null?updated.getComments():r.getComments());
			updated.setStars(r.getStars()==0?updated.getStars():r.getStars());
		
			emanager.persist(updated);
		
			log.info("Review updated: "+updated.toString());
			logWrite(session.getUserid(),"UPDATE","Review updated "+updated.toString());
			
			return "Review updated successfully";
		}catch(Exception e) {
			log.error("Reviews can't be updated: "+e.getStackTrace().toString());
			return e.getMessage();
		}
	}
	
	@Transactional
	public String deleteReview(long id) {
		try {
			Review r = emanager.find(Review.class, id);
			
			if(!isReviewOwner(id)) {
				log.warn("User "+session.getUsername()+" tried to update review without owning it");
				return "Permission denied";
			}
			emanager.remove(r);
		
			log.info("Review deleted: "+r.toString());
			logWrite(session.getUserid(),"DELETE","Review deleted "+r.toString());
			
			return "Review deleted successfully";
		}catch(Exception e) {
			log.error("Reviews can't be deleted: "+e.getStackTrace().toString());
			return e.getMessage();
		}
	}
	
	@Transactional
	public void logWrite(long userid,String operation,String message) {
		UserActionLogs ua = new UserActionLogs();
		if(userid>0) {
			ua.setUid(userid);
		}else {
			ua.setUid(null);
		}
		ua.setOperation(operation);
		ua.setMessage(message);
		ua.setTstamp(new Timestamp(System.currentTimeMillis()));
		try {
			emanager.persist(ua);
		}catch(Exception e) {
			log.fatal("Can't write logs to the database: "+e.getStackTrace().toString());
		}
	}
	
	public List<User> getRegisteredUsers(){
		TypedQuery<User> query = emanager.createQuery("SELECT a FROM User a",User.class);
		return query.getResultList();
	}
	
	
	public boolean isBookOwner(long id) {
		try {
			TypedQuery<Owns> query = emanager.createQuery("SELECT r FROM Owns r WHERE r.bookid=:id",Owns.class);
			query.setParameter("id", id);
			List<Owns> owner = query.getResultList();
			
			if(owner.isEmpty()) {
				log.warn("User "+session.getUsername()+" tried to access missing item ");
				return false;
			}
			
			Owns o = query.getSingleResult();
			if(o.getUid()!=session.getUserid()) {
				log.warn("User "+session.getUsername()+" tried to access item without owning it");
				return false;
			}
			
			return true;
		}catch(NonUniqueResultException ex) {
			log.fatal("Database violation detected: "+ex.getMessage());
			return false;
		}
		catch(Exception e) {
			log.error("Error querying database: "+e.getStackTrace().toString());
			e.printStackTrace();
			return false;
		}
	}
	
	@Transactional
	public boolean isReviewOwner(long id) {
		try {
			TypedQuery<Review> query = emanager.createQuery("SELECT r FROM Review r WHERE r.id=:id",Review.class);
			query.setParameter("id", id);
			List<Review> owner = query.getResultList();
			
			if(owner.isEmpty()) {
				log.warn("User "+session.getUsername()+" tried to access missing item ");
				return false;
			}
			
			Review r = query.getSingleResult();
			
			if(r.getUserid()!=session.getUserid()) {
				log.warn("User "+session.getUsername()+" tried to access item without owning it");
				return false;
			}
			return true;
		}catch(NonUniqueResultException ex) {
				log.fatal("Database violation detected: "+ex.getMessage());
				return false;
		}catch(Exception e) {
				log.error("Error querying database: "+e.getStackTrace().toString());
				return false;
		}
		
	}
	
	public List<Review> getBestReviews() {
		TypedQuery<Review> query = emanager.createQuery("SELECT r FROM Review r WHERE r.stars>=3",Review.class);
		return query.getResultList();
	}
}
