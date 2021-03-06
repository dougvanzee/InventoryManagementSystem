/**
 * @author Doug Van Zee
 */

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Static inventory class the stores all parts and products
 */
public class Inventory {
    private static ObservableList<Part> allParts = FXCollections.observableArrayList();
    private static ObservableList<Product> allProducts = FXCollections.observableArrayList();

    /**
     * Adds part to allParts
     * @param newPart Part to add
     */
    public static void addPart(Part newPart) { allParts.add(newPart); }

    /**
     * Adds product to allProducts
     * @param newProduct Part to add
     */
    public static void addProduct(Product newProduct) { allProducts.add(newProduct); }

    /**
     *
     * @param partId the ID to search for
     * @return returns first element with Part ID
     */
    public static Part lookupPart(int partId) {
        Part requestedPart = allParts.get(0);
        for (Part part : allParts) {
            if(part.getId() == partId)
            {
                requestedPart = part;
                break;
            }
        }
        return requestedPart;
    }

    /**
     *
     * @param productId the ID to search for
     * @return returns first element with Product ID
     */
    public static Product lookupProduct(int productId) {
        Product requestedProduct = allProducts.get(0);
        for (Product product : allProducts) {
            if(product.getId() == productId)
            {
                requestedProduct = product;
                break;
            }
        }
        return requestedProduct;
    }

    /**
     *
     * @param partName Name to search for
     * @return Returns list of parts that contain substring
     */
    public static ObservableList<Part> lookupPart(String partName) {
        ObservableList<Part> foundParts = FXCollections.observableArrayList();
        for (Part part: allParts)
        {
            if (part.getName().contains(partName))
                foundParts.add(part);
        }
        return foundParts;
    }

    /**
     *
     * @param productName Name to search for
     * @return Returns list of products that contain substring
     */
    public static ObservableList<Product> lookupProduct(String productName) {
        ObservableList<Product> foundProducts = FXCollections.observableArrayList();
        for (Product product: allProducts)
        {
            if (product.getName().contains(productName))
                foundProducts.add(product);
        }
        return foundProducts;
    }

    /**
     * Updates (replaces) part at index
     * @param index index in allParts
     * @param selectedPart new part to replace item at index
     */
    public static void updatePart(int index, Part selectedPart) {
        allParts.set(index, selectedPart);
    }

    /**
     * Updates (replaces) product at index
     * @param index index in allProducts
     * @param selectedProduct new product to replace item at index
     */
    public static void updateProduct(int index, Product selectedProduct) {
        allProducts.set(index, selectedProduct);
    }

    /**
     *
     * @param selectedPart part to delete
     * @return bool if part was deleted
     */
    public static boolean deletePart(Part selectedPart) {
        return allParts.removeIf(element -> element.getId() == selectedPart.getId());
    }

    /**
     *
     * @param selectedProduct product to delete
     * @return bool if product was deleted
     */
    public static boolean deleteProduct(Product selectedProduct) {
        return allProducts.removeIf(element -> element.getId() == selectedProduct.getId());
    }

    /**
     *
     * @return reference to all parts in inventory
     */
    public static ObservableList<Part> getAllParts() { return allParts; }

    /**
     *
     * @return reference to all products in inventory
     */
    public static ObservableList<Product> getAllProducts() { return allProducts; }
}
