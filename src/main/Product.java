/**
 * @author Doug Van Zee
 */

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.text.DecimalFormat;

/**
 * Product class
 */
public class Product {
    private ObservableList<Part> associatedParts = FXCollections.observableArrayList();
    private int id;
    private String name;
    private double price;
    private int stock;
    private int min;
    private int max;

    /**
     * Constructor
     * @param id
     * @param name
     * @param price
     * @param stock
     * @param min
     * @param max
     */
    public Product(int id, String name, double price, int stock, int min, int max) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.min = min;
        this.max = max;
    }

    /**
     *
     * @param id id to set
     */
    public void setId(int id) { this.id = id; }

    /**
     *
     * @param name the name to set
     */
    public void setName(String name) { this.name = name; }

    /**
     *
     * @param price price to set
     */
    public void setPrice(double price) { this.price = price; }

    /**
     *
     * @param stock inventory level to set
     */
    public void setStock(int stock) { this.stock = stock; }

    /**
     *
     * @param min minimum inventory level
     */
    public void setMin(int min) { this.min = min; }

    /**
     *
     * @param max maximum inventory level
     */
    public void setMax(int max) { this.max = max; }

    /**
     *
     * @return the product ID
     */
    public int getId() { return id; }

    /**
     *
     * @return the name
     */
    public String getName() { return name; }

    /**
     *
     * @return the price
     */
    public double getPrice() { return price; }

    /**
     * @return the price formatted as US dollars
     */
    public String getPriceAsString() {
        DecimalFormat df = new DecimalFormat("#0.00");
        return "$" + df.format(price);
    }

    /**
     *
     * @return the inventory level
     */
    public int getStock() { return stock; }

    /**
     *
     * @return the minimum inventory level
     */
    public int getMin() { return min; }

    /**
     *
     * @return the maximum inventory level
     */
    public int getMax() { return max; }

    /**
     *
     * @param part the part to add to associated parts
     */
    public void addAssociatedPart(Part part) { associatedParts.add(part); }

    /**
     *
     * @param selectedAssociatedPart the part to delete
     * @return whether part was deleted
     */
    public boolean deleteAssociatedPart(Part selectedAssociatedPart) {
        return associatedParts.remove(selectedAssociatedPart);
    }

    /**
     *
     * @return list of all associated parts
     */
    public ObservableList<Part> getAllAssociatedParts() { return associatedParts; }
}
