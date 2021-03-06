/**
 * @author Doug Van Zee
 */

/**
 * Extends Part, adds a Machine ID variable
 */
public class InHouse extends Part {
    private int machineId;

    /**
     * Constructor
     * @param id part ID
     * @param name part Name
     * @param price part Price
     * @param stock Part inventory level
     * @param min Part minimum inventory level
     * @param max Part maximum inventory level
     * @param machineId Part machine ID that the part was made on
     */
    public  InHouse(int id, String name, double price, int stock, int min, int max, int machineId)
    {
        super( id, name, price, stock, min, max);
        this.machineId = machineId;
    }

    /**
     *
     * @param machineId sets the machine ID
     */
    public void setMachineId(int machineId) { this.machineId = machineId; }

    /**
     *
     * @return the machine ID
     */
    public int getMachineId() { return machineId; }
}
