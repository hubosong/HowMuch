package robsonmachczew.howmuch;

public class ProductQRCode {
    private int id;
    private String title;
    private Double mediumprice;
    private String date;
    private Double price;
    private int image;

    public ProductQRCode(int id, String title, Double mediumprice, String date, Double price, int image) {
        this.id = id;
        this.title = title;
        this.mediumprice = mediumprice;
        this.date = date;
        this.price = price;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Double getMediumprice() {
        return mediumprice;
    }

    public String getDate() {
        return date;
    }

    public Double getPrice() {
        return price;
    }

    public int getImage() {
        return image;
    }
}
