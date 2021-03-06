/**
 * @author Doug Van Zee
 */

/**
 * Extends part and adds company name variable
 */
public class Outsourced extends Part {
    private String companyName;

    /**
     * Constructor
     * @param id
     * @param name
     * @param price
     * @param stock
     * @param min
     * @param max
     * @param companyName
     */
    public Outsourced(int id, String name, double price, int stock, int min, int max, String companyName) {
        super( id, name, price, stock, min, max);
        this.companyName = companyName;
    }

    /**
     *
     * @param companyName name to set
     */
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    /**
     *
     * @return name of company
     */
    public String getCompanyName() { return companyName;}
}
