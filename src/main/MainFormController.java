/**
 * @author Doug Van Zee
 */

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

/**
 * Code-behind of Main Form
 */
public class MainFormController
{
    //<editor-fold desc="Private Members">

    FilteredList<Part> filteredPartList; // List that is filtered from Part Search Field, displayed in Part Table
    FilteredList<Product> filteredProductList; // List that is filtered for Product Search Field, displayed in Product Table

    //</editor-fold>

    //<editor-fold desc="FXML Members">

    @FXML
    private Button exitButton;

    @FXML
    private Button addPartButton;

    @FXML
    private Button modifyPartButton;

    @FXML
    private Button deletePartButton;

    @FXML
    private TableView partTableView;

    @FXML
    private TableColumn partIdColumn;

    @FXML
    private TableColumn partNameColumn;

    @FXML
    private TableColumn partInventoryColumn;

    @FXML
    private TableColumn partPriceColumn;

    @FXML
    private Button addProductButton;

    @FXML
    private Button modifyProductButton;

    @FXML
    private Button deleteProductButton;

    @FXML
    private TableView productTableView;

    @FXML
    private TableColumn productIdColumn;

    @FXML
    private TableColumn productNameColumn;

    @FXML
    private TableColumn productInventoryColumn;

    @FXML
    private TableColumn productPriceColumn;

    @FXML
    private TextField partSearchField;

    @FXML
    private TextField productSearchField;

    //</editor-fold>

    //<editor-fold desc="Initialization">

    /**
     * Initializes controller when linked to MainForm.fxml
     */
    public void initialize() {
        // Link filtered list to allParts in Inventory
        filteredPartList = new FilteredList<>(Inventory.getAllParts());

        // Setup part table view
        partTableView.setItems(filteredPartList);
        partTableView.setPlaceholder(new Label("No Parts Have Been Created"));
        partIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        partNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        partPriceColumn.setCellValueFactory(new PropertyValueFactory<>("PriceAsString"));
        partInventoryColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));

        // Link filtered list to allProducts in Inventory
        filteredProductList = new FilteredList<>(Inventory.getAllProducts());

        // Setup product table view
        productTableView.setItems(filteredProductList);
        productTableView.setPlaceholder(new Label("No Products Have Been Created"));
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        productPriceColumn.setCellValueFactory(new PropertyValueFactory<>("PriceAsString"));
        productInventoryColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));

        // Add listeners to search fields
        addPartSearchFieldListener(partSearchField);
        addProductSearchFieldListener(productSearchField);
    }

    //</editor-fold>

    //<editor-fold desc="Listeners">

    /**
     * Exit program
     */
    public void exitButtonListener()
    {
        Platform.exit();
    }

    /**
     * Displays add part form
     * @throws IOException
     */
    public void addPartButtonListener() throws IOException {
        // Create add part form
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PartForm.fxml"));
        loader.setController(new PartFormController());
        Parent root = loader.load();
        Stage popupwindow = new Stage();

        // Set add part form parameters
        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.initStyle(StageStyle.UTILITY);
        popupwindow.setTitle("");
        popupwindow.setResizable(false);

        // Display add part form
        Scene scene1= new Scene(root, 600, 600);
        popupwindow.setScene(scene1);
        popupwindow.showAndWait();
    }

    /**
     * Display modify part form if valid part is selected in Part Table
     * @throws IOException
     */
    public void modifyPartButtonListener() throws IOException {
        int selectedIndex = partTableView.getSelectionModel().getSelectedIndex();

        // If nothing is selected, display message and return
        if (selectedIndex == -1) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "No item selected to modify.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        // Create modify part form
        int sourceIndex = filteredPartList.getSourceIndex(selectedIndex);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PartForm.fxml"));
        loader.setController(new PartFormController(sourceIndex));

        // Set modify part form parameters
        Parent root = loader.load();
        Stage popupwindow = new Stage();
        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.initStyle(StageStyle.UTILITY);
        popupwindow.setTitle("");
        popupwindow.setResizable(false);

        // Display modify part form
        Scene scene1= new Scene(root, 600, 600);
        popupwindow.setScene(scene1);
        popupwindow.showAndWait();
    }

    /**
     * Deletes part if valid part is selected in part table
     * Displays error message if part is associated with product
     * @throws IOException
     */
    public void deletePartButtonListener() throws IOException {
        int selectedIndex = partTableView.getSelectionModel().getSelectedIndex();

        // If nothing is selected, display message and return
        if (selectedIndex == -1) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "No part selected to delete.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        // Get part to delete
        int sourceIndex = filteredPartList.getSourceIndex(selectedIndex);
        Part part = Inventory.getAllParts().get(sourceIndex);

        // If part is associated with product, display error message and return
        if (bIsPartInUse(part)) {
            displayProductsContainPartMessage(part);
            return;
        }

        // Display delete part confirmation message
        Alert confirmDeleteAlert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete the part \""
                        + part.getName()
                        + "\" from the inventory?",
                ButtonType.YES, ButtonType.CANCEL);
        confirmDeleteAlert.showAndWait();

        // If cancel delete
        if (confirmDeleteAlert.getResult() == ButtonType.CANCEL) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "No parts were deleted", ButtonType.OK);
            alert.showAndWait();
        }

        // If confirm delete
        else if (confirmDeleteAlert.getResult() == ButtonType.YES) {
            if (!Inventory.deletePart(part)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "The selected part could not be deleted.", ButtonType.OK);
                alert.showAndWait();
            }
        }
    }

    /**
     * Display add product form
     * @throws IOException
     */
    public void addProductButtonListener() throws IOException {
        // Create add product form
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ProductForm.fxml"));
        loader.setController(new ProductFormController());
        Parent root = loader.load();
        Stage popupwindow = new Stage();

        // Set add product form parameters
        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.initStyle(StageStyle.UTILITY);
        popupwindow.setTitle("");

        // Display form
        Scene scene1= new Scene(root, 1100, 700);
        popupwindow.setScene(scene1);
        popupwindow.showAndWait();
    }

    /**
     * Display modify product form if valid product selected from product table
     * @throws IOException
     */
    public void modifyProductButtonListener() throws IOException {
        int selectedIndex = productTableView.getSelectionModel().getSelectedIndex();

        // If nothing is selected, display message and return
        if (selectedIndex == -1) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "No item selected to modify.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        // Create modify product form
        int sourceIndex = filteredProductList.getSourceIndex(selectedIndex);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ProductForm.fxml"));
        loader.setController(new ProductFormController(sourceIndex));
        Parent root = loader.load();
        Stage popupwindow = new Stage();

        // Set modify product form parameters
        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.initStyle(StageStyle.UTILITY);
        popupwindow.setTitle("");

        // Display form
        Scene scene1= new Scene(root, 1100, 700);
        popupwindow.setScene(scene1);
        popupwindow.showAndWait();
    }

    /**
     * Deletes product if valid product is selected in product table
     * @throws IOException
     */
    public void deleteProductButtonListener() throws IOException {
        int selectedIndex = productTableView.getSelectionModel().getSelectedIndex();

        // If nothing is selected, display message and return
        if (selectedIndex == -1) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "No product selected to delete.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        // Get product to delete
        int sourceIndex = filteredProductList.getSourceIndex(selectedIndex);
        Product product = Inventory.getAllProducts().get(sourceIndex);

        // Check if parts associated with product
        if (bProductHasPartsAssociated(product)) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Cannot delete product \""
                    + product.getName()
                    + "\". Product still has parts associated with it.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        // Create confirmation message for deletion
        Alert confirmDeleteAlert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete the product \""
                        + product.getName()
                        + "\" from the inventory?",
                ButtonType.YES, ButtonType.CANCEL);
        confirmDeleteAlert.showAndWait();

        // If cancel delete
        if (confirmDeleteAlert.getResult() == ButtonType.CANCEL) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "No products were deleted", ButtonType.OK);
            alert.showAndWait();
        }

        // If confirm delete
        else if (confirmDeleteAlert.getResult() == ButtonType.YES) {
            if (!Inventory.deleteProduct(product)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "The selected product could not be deleted.", ButtonType.OK);
                alert.showAndWait();
            }
        }
    }

    /**
     * Adds listener to part search field
     * Linked to filteredPartList
     * Searches part name and ID
     * @param textField
     */
    private void addPartSearchFieldListener(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Change placeholder text of part table
            if (newValue == null || newValue.isEmpty())
                partTableView.setPlaceholder(new Label("No Parts Have Been Created"));
            else
                partTableView.setPlaceholder(new Label("No Parts Found in Search"));

            // Search field predicate
            filteredPartList.setPredicate(myObject -> {
                // If filter text is empty, display all parts.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (String.valueOf(myObject.getName()).toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches name
                } else if (String.valueOf(myObject.getId()).toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches ID
                }

                // No match
                return false;
            });
        });
    }

    /**
     * Adds listener to product search field
     * Linked to filteredProductList
     * Searches product name and ID
     * @param textField
     */
    private void addProductSearchFieldListener(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Change placeholder text of product table
            if (newValue == null || newValue.isEmpty())
                productTableView.setPlaceholder(new Label("No Products Have Been Created"));
            else
                productTableView.setPlaceholder(new Label("No Products Found in Search"));

            // Search field predicate
            filteredProductList.setPredicate(myObject -> {
                // If filter text is empty, display all parts.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (String.valueOf(myObject.getName()).toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches name
                } else if (String.valueOf(myObject.getId()).toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches ID
                }

                // No match
                return false;
            });
        });
    }

    //</editor-fold>

    //<editor-fold desc="Private Helpers">

    /**
     *
     * @param partToCheck Part to check
     * @return returns if part is associated with at least one product
     */
    private boolean bIsPartInUse(Part partToCheck) {
        boolean bInUse = false;

        for (Product product: Inventory.getAllProducts()) {
            for (Part part: product.getAllAssociatedParts()) {
                if (part == partToCheck)
                    bInUse = true;
            }
        }

        return bInUse;
    }

    /**
     *
     * @param partToCheck Part to check
     * @return returns the products associated with the part
     */
    private ObservableList<String> getProductsContainingPart(Part partToCheck) {
        ObservableList<String> listOfProductNames = FXCollections.observableArrayList();

        for (Product product: Inventory.getAllProducts()) {
            for (Part part: product.getAllAssociatedParts()) {
                if (part == partToCheck) {
                    listOfProductNames.addAll(product.getName());
                    break;
                }
            }
        }
        return listOfProductNames;
    }

    /**
     * Displays message with list of products associated with part
     * @param part part
     */
    public void displayProductsContainPartMessage(Part part) {
        String messageText = "Part cannot be deleted. The follow products still contain a reference to the part: \n\n";
        for (String productName: getProductsContainingPart(part)) {
            messageText += "\"" + productName + "\"\n";
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, messageText, ButtonType.OK);
        alert.showAndWait();
    }

    /**
     *
     * @param product Product to check
     * @return Returns true if product has parts associated with it
     */
    public boolean bProductHasPartsAssociated(Product product) {
        if (product.getAllAssociatedParts().size() != 0)
            return true;
        else
            return false;
    }

    //</editor-fold>
}
