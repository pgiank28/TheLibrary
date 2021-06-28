package library2;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/TheLibrary")
public class MyApplication extends Application{

	@Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> classes = new HashSet<>();
        classes.add(BookFilter.class);
        classes.add(EditBooks.class);
        classes.add(Login.class);
        classes.add(OnReviewsOperations.class);
        classes.add(OnUserOperations.class);
        classes.add(GetReviews.class);
        classes.add(UserRegistration.class);
        classes.add(soap.BestReviews.class);
        return classes;
	}
}
