package robsonmachczew.howmuch;

public class ProductQRCode {
    private int id;
    private String title;
    private String market;
    private Double mediumprice;
    private String date;
    private Double price;
    private int image;

    public ProductQRCode(int id, String title, String market, Double mediumprice, String date, Double price, int image) {
        this.id = id;
        this.title = title;
        this.market = market;
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

    public String getMarket() {
        return market;
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
