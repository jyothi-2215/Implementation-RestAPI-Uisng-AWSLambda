package p3;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;


public class DBLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private CategoryDaoJdbc categoryDb = new CategoryDaoJdbc();
    private BookDaoJdbc bookDb = new BookDaoJdbc();


    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent req, Context context) {
        Map<String, String> CORS = Map.of("access-control-allow-origin", "*");
        String path = req.getPath();
        System.out.println("Received path: " + path);
        switch (path) {


            case "/api/addBook":
                System.out.println("Trying to print path..."+path);
                return addNewBook(req, CORS);
            case "/api/getBookById":
                System.out.println("Trying to print path..."+path);
                return getBookById(req, CORS);
            case "/api/getBookByCategoryId":
                System.out.println("Trying to print path..."+path);
                return getBookByCategoryId(req, CORS);
            case "/api/getBookByCategoryName":
                System.out.println("Trying to print path..."+path);
                return getBookByCategoryName(req, CORS);
            case "/api/getRandomBook":
                System.out.println("Trying to print path..."+path);
                return getRandomBooks(CORS);
            case "/api/getAllCategory":
                System.out.println("Trying to print path..."+path);
                return getAllCategories(CORS);
            case "/api/getCategoryName":
                System.out.println("Trying to print path..."+path);
                return getCategoryName(req, CORS);
            case "/api/getCategoryId":
                System.out.println("Trying to print path..."+path);
                return getCategoryId(req, CORS);
            case "/api/addCategory":
                System.out.println("Trying to print path..."+path);
                return addCategory(req, CORS);
            case "/api/getAllBook":
                System.out.println("Trying to print path..."+path);
                return getAllBook(CORS);

            default:
                return new APIGatewayProxyResponseEvent()
                        .withBody("Invalid request")
                        .withHeaders(CORS)
                        .withStatusCode(404);
        }
    }



    private APIGatewayProxyResponseEvent getAllCategories(Map<String, String> CORS) {
        System.out.println("Entered in getALlCategories function...");
        List<Category> categories = categoryDb.findAll();
        Gson gson = new Gson();
        String json = gson.toJson(categories);
        System.out.println("Printing json data..."+json);

        return new APIGatewayProxyResponseEvent()
                .withBody(json)
                .withHeaders(CORS)
                .withStatusCode(200);
    }


    //changed

    private APIGatewayProxyResponseEvent getCategoryName(APIGatewayProxyRequestEvent req, Map<String, String> CORS) {
        System.out.println("Entered in getCategoryName function...");
        Map<String, String> queryParams = req.getQueryStringParameters();
        System.out.println("Printing QueryParams..."+queryParams);
        if (queryParams == null || !queryParams.containsKey("categoryId")) {
            return new APIGatewayProxyResponseEvent()
                    .withBody("{\"message\":\"categoryId query parameter is missing\"}")
                    .withHeaders(CORS)
                    .withStatusCode(400);
        }

        long categoryId;
        try {
            categoryId = Long.parseLong(queryParams.get("categoryId"));
        } catch (NumberFormatException e) {
            System.out.println(e);
            return new APIGatewayProxyResponseEvent()
                    .withBody("{\"message\":\"Invalid categoryId format\"}")
                    .withHeaders(CORS)
                    .withStatusCode(400);
        }

        Category category = categoryDb.findByCategoryId(categoryId);
        if (category == null) {
            return new APIGatewayProxyResponseEvent()
                    .withBody("{\"message\":\"Category not found\"}")
                    .withHeaders(CORS)
                    .withStatusCode(404);
        }

        Gson gson = new Gson();
        String json = gson.toJson(Map.of("name", category.name()));
        System.out.println("Printing json data..."+json);

        return new APIGatewayProxyResponseEvent()
                .withBody(json)
                .withHeaders(CORS)
                .withStatusCode(200);
    }



    //changed

    private APIGatewayProxyResponseEvent getCategoryId(APIGatewayProxyRequestEvent req, Map<String, String> CORS) {
        System.out.println("Entered in getCategoryId function...");
        Map<String, String> queryParams = req.getQueryStringParameters();
        if (queryParams == null || !queryParams.containsKey("categoryName")) {
            return new APIGatewayProxyResponseEvent()
                    .withBody("{\"message\":\"Category name query parameter is missing\"}")
                    .withHeaders(CORS)
                    .withStatusCode(400);
        }

        String categoryName = queryParams.get("categoryName");
        Category category = categoryDb.findByName(categoryName);
        if (category == null) {
            return new APIGatewayProxyResponseEvent()
                    .withBody("{\"message\":\"Category not found\"}")
                    .withHeaders(CORS)
                    .withStatusCode(404);
        }

        Gson gson = new Gson();
        String json = gson.toJson(Map.of("categoryId", category.categoryId()));
        System.out.println("Printing json data..."+json);

        return new APIGatewayProxyResponseEvent()
                .withBody(json)
                .withHeaders(CORS)
                .withStatusCode(200);
    }






    private APIGatewayProxyResponseEvent addCategory(APIGatewayProxyRequestEvent req, Map<String, String> CORS) {
        System.out.println("Entered in addCategory function...");
        String addedCategory = req.getBody();
        JSONObject obj = new JSONObject(addedCategory);
        String categoryName = obj.getString("categoryName");
        long categoryId = obj.getLong("categoryId");

        Category newCategory = new Category(categoryId, categoryName);
        categoryDb.addCategory(newCategory.categoryId(), newCategory.name());

        Gson gson = new Gson();
        String json = gson.toJson(Map.of("message", "Category added: " + newCategory.name()));
        System.out.println("Printing json data..."+json);
        return new APIGatewayProxyResponseEvent()
                .withBody(json)
                .withHeaders(CORS)
                .withStatusCode(201);
    }




    private APIGatewayProxyResponseEvent getAllBook(Map<String, String> CORS) {
        System.out.println("Entered in getAllBook function...");
        List<Book> books = bookDb.findAll();
        Gson gson = new Gson();
        String json = gson.toJson(books);
        System.out.println("Printing json data..."+json);
        return new APIGatewayProxyResponseEvent()
                .withBody(json)
                .withHeaders(CORS)
                .withStatusCode(200);
    }




    private APIGatewayProxyResponseEvent addNewBook(APIGatewayProxyRequestEvent req, Map<String, String> CORS) {
        System.out.println("Entered in addNewBook function...");
        String addedBook = req.getBody();
        JSONObject obj = new JSONObject(addedBook);

        Book newBook = new Book(
                obj.getLong("bookId"),
                obj.getString("title"),
                obj.getString("author"),
                obj.getString("description"),
                obj.getInt("price"),
                obj.getInt("rating"),
                obj.getBoolean("isPublic"),
                obj.getBoolean("isFeatured"),
                obj.getLong("categoryId"));

        bookDb.addBook(
                newBook.bookId(),
                newBook.title(),
                newBook.author(),
                newBook.description(),
                newBook.price(),
                newBook.rating(),
                newBook.isPublic(),
                newBook.isFeatured(),
                newBook.categoryId());

        Gson gson = new Gson();
        String json = gson.toJson(Map.of("message", "Book added: " + newBook.title()));
        System.out.println("Printing json data..."+json);
        return new APIGatewayProxyResponseEvent()
                .withBody(json)
                .withHeaders(CORS)
                .withStatusCode(201);
    }



    //changed

    private APIGatewayProxyResponseEvent getBookById(APIGatewayProxyRequestEvent req, Map<String, String> CORS) {
        System.out.println("Entered in getBookById function...");
        Map<String, String> queryParams = req.getQueryStringParameters();
        if (queryParams == null || !queryParams.containsKey("bookId")) {
            return new APIGatewayProxyResponseEvent()
                    .withBody("{\"message\":\"bookId query parameter is missing\"}")
                    .withHeaders(CORS)
                    .withStatusCode(400);
        }

        long bookId;
        try {
            bookId = Long.parseLong(queryParams.get("bookId"));
        } catch (NumberFormatException e) {
            return new APIGatewayProxyResponseEvent()
                    .withBody("{\"message\":\"Invalid bookId format\"}")
                    .withHeaders(CORS)
                    .withStatusCode(400);
        }

        return bookDb.findByBookId(bookId)
                .map(book -> {
                    Gson gson = new Gson();
                    String json = gson.toJson(book);
                    return new APIGatewayProxyResponseEvent()
                            .withBody(json)
                            .withHeaders(CORS)
                            .withStatusCode(200);
                })
                .orElseGet(() -> new APIGatewayProxyResponseEvent()
                        .withBody("{\"message\":\"Book not found\"}")
                        .withHeaders(CORS)
                        .withStatusCode(404));
    }






    private APIGatewayProxyResponseEvent getBookByCategoryId(APIGatewayProxyRequestEvent req, Map<String, String> CORS) {
        System.out.println("Entered in getBookByCategoryId function...");
        Map<String, String> queryParams = req.getQueryStringParameters();
        if (queryParams == null || !queryParams.containsKey("categoryId")) {
            return new APIGatewayProxyResponseEvent()
                    .withBody("{\"message\":\"categoryId query parameter is missing\"}")
                    .withHeaders(CORS)
                    .withStatusCode(400);
        }

        long categoryId;
        try {
            categoryId = Long.parseLong(queryParams.get("categoryId"));
        } catch (NumberFormatException e) {
            return new APIGatewayProxyResponseEvent()
                    .withBody("{\"message\":\"Invalid categoryId format\"}")
                    .withHeaders(CORS)
                    .withStatusCode(400);
        }

        List<Book> books = bookDb.findByCategoryId(categoryId);
        if (books == null || books.isEmpty()) {
            return new APIGatewayProxyResponseEvent()
                    .withBody("{\"message\":\"No books found for this category\"}")
                    .withHeaders(CORS)
                    .withStatusCode(404);
        }

        Gson gson = new Gson();
        String json = gson.toJson(books);
        System.out.println("Printing json data..."+json);

        return new APIGatewayProxyResponseEvent()
                .withBody(json)
                .withHeaders(CORS)
                .withStatusCode(200);
    }






    private APIGatewayProxyResponseEvent getBookByCategoryName(APIGatewayProxyRequestEvent req, Map<String, String> CORS) {
        System.out.println("Entered in getBookByCategoryName function...");
        Map<String, String> queryParams = req.getQueryStringParameters();
        if (queryParams == null || !queryParams.containsKey("categoryName")) {
            return new APIGatewayProxyResponseEvent()
                    .withBody("{\"message\":\"categoryName query parameter is missing\"}")
                    .withHeaders(CORS)
                    .withStatusCode(400);
        }

        String categoryName = queryParams.get("categoryName");
        List<Book> books = bookDb.findByCategoryName(categoryName);
        Gson gson = new Gson();
        String json = gson.toJson(books);
        System.out.println("Printing json data..."+json);

        return new APIGatewayProxyResponseEvent()
                .withBody(json)
                .withHeaders(CORS)
                .withStatusCode(200);
    }





    private APIGatewayProxyResponseEvent getRandomBooks(Map<String, String> CORS) {
        System.out.println("Entered in getRandomBooks function...");
        List<Book> books = bookDb.findRandomBook();
        if (books.isEmpty()) {
            return new APIGatewayProxyResponseEvent()
                    .withBody("{\"message\":\"No books available\"}")
                    .withHeaders(CORS)
                    .withStatusCode(404);
        } else {
            Gson gson = new Gson();
            String json = gson.toJson(books);
            System.out.println("Printing json data..."+json);
            return new APIGatewayProxyResponseEvent()
                    .withBody(json)
                    .withHeaders(CORS)
                    .withStatusCode(200);
        }
    }
}