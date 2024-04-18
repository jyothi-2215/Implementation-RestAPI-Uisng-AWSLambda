package p3;

import java.util.List;
import java.util.Optional;

public interface BookDao {

    Optional<Book> findByBookId(long bookId);

    public List<Book> findByCategoryId(long categoryId);


    public List<Book> findByCategoryName(String categoryName);

    public List<Book> findRandomBook();

    public List<Book> findAll();

    public void addBook(long bookId, String title, String author, String description, double price, int rating, boolean isPublic, boolean isFeatured, long categoryId);

    public List<Book> findRandomByCategoryId(long categoryId, int limit);

    public List<Book> findRandomByCategoryName(String categoryName, int limit);

}
