package store.domain_model.inventory;

import java.util.Date;

public class Food extends Product{
    private String type;
    private Date expirationDate;

    public Food(String id, String name, String brand, double price, Date expirationDate, String type) {
        super(id, name, brand, price);
        this.expirationDate = expirationDate;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}
