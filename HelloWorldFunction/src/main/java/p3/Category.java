
package p3;

public record Category(long categoryId, String name) {

    public long categoryId() {
        return categoryId;
    }
    public String name() {
        return name;
    }
}
