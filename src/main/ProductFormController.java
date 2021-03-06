/**
 * @author Doug Van Zee
 */

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import java.text.DecimalFormat;

/**
 * Code-behind of Product Form
 */
public class ProductFormController {

    //<editor-fold desc="Private Members">

    private enum EFormType
    {
        ADDPRODUCT,
        MODIFYPRODUCT
    }

    final private ProductFormController.EFormType formType;
    private Product existingProduct;
    private int existingProductIndex;
    FilteredList<Part> filteredPartList;

    private int id;
    private String name;
    private double price;
    private int inv;
    private int min;
    private int max;
    ObservableList<Part> associatedParts = FXCollections.observableArrayList();

    //</editor-fold>

    //<editor-fold desc="FXML Variables">

    @FXML
    private Label windowLabel;

    @FXML
    private TextField idTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField invTextField;

    @FXML
    private TextField priceTextField;

    @FXML
    private TextField maxTextField;

    @FXML
    private TextField minTextField;

    @FXML
    private Label invErrorLabel;

    @FXML
    private Label priceErrorLabel;

    @FXML
    private TextField partSearchTextField;

    @FXML
    private TableView allPartsTableView;

    @FXML
    private TableColumn allPartsIdColumn;

    @FXML
    private TableColumn allPartsNameColumn;

    @FXML
    private TableColumn allPartsInvColumn;

    @FXML
    private TableColumn allPartsPriceColumn;

    @FXML
    private TableView assocPartsTableView;

    @FXML
    private TableColumn assocPartsIdColumn;

    @FXML
    private TableColumn assocPartsNameColumn;

    @FXML
    private TableColumn assocPartsInvColumn;

    @FXML
    private TableColumn assocPartsPriceColumn;

    @FXML
    private Button addPartButton;

    @FXML
    private Button removePartButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;



    //</editor-fold>

    //<editor-fold desc="Constructors">

    /**
     * Constructor for Add Product Form
     */
    public ProductFormController()
    {
        formType = EFormType.ADDPRODUCT;
    }

    /**
     * Constructor for Modify Product Form
     * @param productIndex product index to modify
     */
    public ProductFormController(int productIndex)
    {
        existingProductIndex = productIndex;
        existingProduct = Inventory.getAllProducts().get(productIndex);
        formType = EFormType.MODIFYPRODUCT;
    }

    //</editor-fold>

    //<editor-fold desc="Initialization">

    /**
     * Main initialization
     */
    public void initialize()
    {
        // Filtered list linked to All Parts
        filteredPartList = new FilteredList<>(Inventory.getAllParts());

        // Setup all parts table
        allPartsTableView.setItems(filteredPartList);
        allPartsTableView.setPlaceholder(new Label("No Parts Available"));
        allPartsIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        allPartsNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        allPartsPriceColumn.setCellValueFactory(new PropertyValueFactory<>("PriceAsString"));
        allPartsInvColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));

        // Setup associated parts table
        assocPartsTableView.setItems(associatedParts);
        assocPartsTableView.setPlaceholder(new Label("No Associated Parts"));
        assocPartsIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        assocPartsNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        assocPartsPriceColumn.setCellValueFactory(new PropertyValueFactory<>("PriceAsString"));
        assocPartsInvColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));

        // Add listeners
        addNameTextFieldListener(nameTextField);
        addPriceTextFieldListeners(priceTextField);
        addInventoryTextFieldListeners(invTextField);
        addInventoryTextFieldListeners(maxTextField);
        addInventoryTextFieldListeners(minTextField);
        addPartSearchFieldListener(partSearchTextField);
        checkInvErrorMessage();

        // Continue with form-specific initializations
        switch (formType) {
            case ADDPRODUCT:
                initializeAddProducttForm();
                break;

            case MODIFYPRODUCT:
                initializeModifyProductForm();
                break;
        }
    }

    /**
     * Initializations specific to Add Product Form
     */
    private void initializeAddProducttForm()
    {
        windowLabel.setText("Add Product");
        idTextField.setText(Integer.toString(generateProductId()));

        bIsPriceAboveCost();
    }

    /**
     * Initializations specific to Modify Part Form
     */
    private void initializeModifyProductForm()
    {
        windowLabel.setText("Modify Product");

        // Get existing product variables
        id = existingProduct.getId();
        idTextField.setText(Integer.toString(existingProduct.getId()));
        nameTextField.setText(existingProduct.getName());
        priceTextField.setText(Double.toString(existingProduct.getPrice()));
        invTextField.setText(Integer.toString(existingProduct.getStock()));
        maxTextField.setText(Integer.toString(existingProduct.getMax()));
        minTextField.setText(Integer.toString(existingProduct.getMin()));

        // Get existing product's associated parts
        associatedParts.addAll(existingProduct.getAllAssociatedParts());

        bIsPriceAboveCost();
    }

    //</editor-fold>

    //<editor-fold desc="Listeners">

    /**
     * Listener sets textfield value to name var
     * @param textField
     */
    private void addNameTextFieldListener(TextField textField) {
        textField.textProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) ->
                        name = textField.getText()
        );
    }

    /**
     * Listener formats textfield to be US Dollars
     * Sets textfield value to price var
     * @param textField
     */
    private void addPriceTextFieldListeners(TextField textField) {
        // If text changes
        textField.textProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{0,7}([.]\\d{0,2})?")) {
                        textField.setText(oldValue);
                    }
                    if (textField.getText().matches("[.]"))
                        textField.setText("0.");

                    if( !textField.getText().isEmpty()) {
                        price = Double.parseDouble(textField.getText());
                    }
                });

        // If text field loses focus
        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                textField.setText(formatCurrencyText(textField.getText()));
            }
            if (!textField.getText().isEmpty()) {
                price = Double.parseDouble(textField.getText());
                bIsPriceAboveCost();
            }
        });

        // If "Enter" is pressed
        textField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) {
            textField.setText(formatCurrencyText(textField.getText()));

            if (!textField.getText().isEmpty()) {
                price = Double.parseDouble(textField.getText());
                bIsPriceAboveCost();
            }
        } });
    }

    /**
     * Listener checks to see if inventory levels are valid
     * Sets inventory levels to inv, min, and max vars
     * @param textField
     */
    private void addInventoryTextFieldListeners(TextField textField) {
        // If text changes
        textField.textProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{0,7}?")) { textField.setText(oldValue); }
                    if (!invTextField.getText().isEmpty()) { inv = Integer.parseInt(invTextField.getText()); }
                    if (!minTextField.getText().isEmpty()) { min = Integer.parseInt(minTextField.getText()); }
                    if (!maxTextField.getText().isEmpty()) { max = Integer.parseInt(maxTextField.getText()); }
                });

        // If "Enter" is pressed
        textField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) { checkInvErrorMessage(); } });

        // If loses focus
        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) { checkInvErrorMessage(); }
        });
    }

    /**
     * Listener filters list of All Parts dispalyed in All Parts Table
     * @param textField
     */
    private void addPartSearchFieldListener(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Change table placeholder text
            if (newValue == null || newValue.isEmpty())
                allPartsTableView.setPlaceholder(new Label("No Parts Available"));
            else
                allPartsTableView.setPlaceholder(new Label("No Parts Found in Search"));

            // Set filter predicate
            filteredPartList.setPredicate(myObject -> {
                // If filter text is empty, display all parts
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (String.valueOf(myObject.getName()).toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches part name

                } else if (String.valueOf(myObject.getId()).toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches part id
                }

                // No match
                return false;
            });
        });
    }

    /**
     * Adds selected part to associated parts list
     */
    public void addPartButtonListener() {
        int selectedIndex = allPartsTableView.getSelectionModel().getSelectedIndex();

        // If no part selected, display message and return
        if (selectedIndex == -1) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "No item selected to modify.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        // Add part to associated parts
        associatedParts.add(filteredPartList.get(selectedIndex));
        bIsPriceAboveCost();
    }

    /**
     * Removes selected part from associated parts
     * Displays confirmation message
     */
    public void removePartButtonListener() {
        int selectedIndex = assocPartsTableView.getSelectionModel().getSelectedIndex();

        // If no part selected, display message and return
        if (selectedIndex == -1) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "No item selected to modify.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        // Create and display deletion confirmation message
        Alert confirmDeleteAlert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete the product \""
                        + associatedParts.get(selectedIndex).getName()
                        + "\" from the inventory?",
                ButtonType.YES, ButtonType.CANCEL);
        confirmDeleteAlert.showAndWait();

        // If deletion confirmed
        if (confirmDeleteAlert.getResult() == ButtonType.YES) {
            associatedParts.remove(selectedIndex);
        }

        bIsPriceAboveCost();
    }

    /**
     * Listener checks if save is valid and adds/updates part in Inventory
     */
    public void saveButtonListener() {
        // If not all textfields filled out
        if (!bAllTextFieldsFilled()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Not all fields are filled out.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        // If inventory levels invalid
        if (!bIsInvValuesCorrect()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Inventory must be between min and max.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        // If product price isn't high enough
        if(!bIsPriceAboveCost()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Price of product is below parts cost. Adjust price before saving", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        // If no parts associated, display confirmation message
        if (associatedParts.size() == 0) {
            Alert confirmDeleteAlert = new Alert(Alert.AlertType.CONFIRMATION, "No parts are associated with this product. Are you sure you want to continue?", ButtonType.YES, ButtonType.CANCEL);
            confirmDeleteAlert.showAndWait();

            // If save is canceled
            if (confirmDeleteAlert.getResult() == ButtonType.CANCEL)
                return;
        }

        // Create new product
        Product newProduct = new Product(id, name, price, inv, min, max);

        // Add associated parts to new product
        for (Part part: associatedParts) {
            newProduct.addAssociatedPart(part);
        }

        // If add product
        if (formType == EFormType.ADDPRODUCT) {
            Inventory.addProduct(newProduct);
        }
        // If modify product
        else {
            Inventory.updateProduct(existingProductIndex, newProduct);
        }

        // Close window
        closeWindow();
    }

    /**
     * Listener closes the window without saving anything
     */
    public void cancelButtonListener() {
        closeWindow();
    }

    //</editor-fold>

    //<editor-fold desc="Helpers">

    /**
     *
     * @param text String to format
     * @return String formatted as US Dollars, returns empty string if empty string provided
     */
    private String formatCurrencyText(String text) {
        if (!text.isEmpty()) {
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            return decimalFormat.format(Double.parseDouble(text));
        }
        else {
            return "";
        }
    }

    /**
     * Closes the window without saving anything
     */
    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Displays inventory error message if inventory levels not correct
     */
    private void checkInvErrorMessage() {
        invErrorLabel.setVisible(bIsInvMaxMinFilledOut() && !bIsInvValuesCorrect());
    }

    /**
     *
     * @return Returns true if price above cost of all parts
     */
    private boolean bIsPriceAboveCost() {
        if (getPriceOfAllAssocParts() == 0) {
            priceErrorLabel.setVisible(false);
            return true;
        }
        else if (price > getPriceOfAllAssocParts()) {
            priceErrorLabel.setVisible(false);
            return true;
        }
        else {
            priceErrorLabel.setVisible(true);
            return false;
        }
    }

    /**
     *
     * @return returns sum of prices of all associated parts
     */
    private double getPriceOfAllAssocParts() {
        if (associatedParts.size() == 0)
            return 0;

        double total = 0;
        for (Part part: associatedParts) {
            total += part.getPrice();
        }

        return total;
    }

    /**
     *
     * @return Returns true if inventory levels correct
     */
    private boolean bIsInvValuesCorrect() {
        if (!bIsInvMaxMinFilledOut())
            return false;

        double inv = Double.parseDouble(invTextField.getText());
        double max = Double.parseDouble(maxTextField.getText());
        double min = Double.parseDouble(minTextField.getText());

        return  min <= inv && inv <= max;
    }

    /**
     *
     * @return Returns true if inventory levels are filled out
     */
    private boolean bIsInvMaxMinFilledOut() {
        return !invTextField.getText().isEmpty()
                && !maxTextField.getText().isEmpty()
                && !minTextField.getText().isEmpty();
    }

    /**
     *
     * @return Returns true if all fields are filled out
     */
    private boolean bAllTextFieldsFilled() {
        return !nameTextField.getText().isEmpty()
                && !invTextField.getText().isEmpty()
                && !priceTextField.getText().isEmpty()
                && !maxTextField.getText().isEmpty()
                && !minTextField.getText().isEmpty();
    }

    /**
     *
     * @return Returns a product ID based upon last Id found, else returns 1000
     */
    public int generateProductId() {
        if (Inventory.getAllProducts().isEmpty()) { return id = 1000; }

        int lastProductId = -1;
        for (Product product: Inventory.getAllProducts())
        {
            if (product.getId() > lastProductId) { lastProductId = product.getId(); }
        }
        return id = lastProductId + 1;
    }

    //</editor-fold>
}
