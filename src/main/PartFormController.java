/**
 * @author Doug Van Zee
 */

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import java.text.DecimalFormat;

/**
 * Code-behind of Part Form
 */
public class PartFormController {

    //<editor-fold desc="Private Members">

    private enum EFormType
    {
        ADDPART,
        MODIFYPART
    }

    final private EFormType formType;
    private Part existingPart;
    private int existingPartIndex;

    private int id;
    private String name;
    private double price;
    private int inv;
    private int min;
    private int max;
    private int machineId = -1;
    private String companyName = "";

    //</editor-fold>

    //<editor-fold desc="FXML Variables">

    @FXML
    private Label windowLabel;

    @FXML
    private RadioButton inHouseRadioButton;

    @FXML
    private RadioButton outsourcedRadioButton;

    @FXML
    private Label partSourceLabel;

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
    private TextField partSourceTextField;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label invErrorLabel;

    //</editor-fold>

    //<editor-fold desc="Constructors">

    /**
     * Constructor for creating an Add Part Form
     */
    public PartFormController()
    {
        formType = EFormType.ADDPART;
    }

    /**
     * Constructor for creating Modify Part Form
     * @param partIndex part index of part to modify
     */
    public PartFormController(int partIndex)
    {
        existingPart = Inventory.getAllParts().get(partIndex);
        existingPartIndex = partIndex;
        formType = EFormType.MODIFYPART;
    }

    //</editor-fold>

    //<editor-fold desc="Initialization">

    /**
     * Initializes controller when linked to PartFormController.fxml
     */
    public void initialize()
    {
        // Add listeners
        addNameTextFieldListener(nameTextField);
        addPriceTextFieldListeners(priceTextField);
        addInventoryTextFieldListeners(invTextField);
        addInventoryTextFieldListeners(maxTextField);
        addInventoryTextFieldListeners(minTextField);
        addPartSourceListener(partSourceTextField);

        // Set inventory error message
        checkInvErrorMessage();


        // Initialize specific form type
        switch (formType) {
            case ADDPART:
                initializeAddPartForm();
                break;

            case MODIFYPART:
                initializeModifyPartForm();
                break;
        }
    }

    /**
     * Initializations specific to Add Part Form
     */
    private void initializeAddPartForm()
    {
        windowLabel.setText("Add Part");
        radioButtonListener();
        idTextField.setText(Integer.toString(generateProductId()));
    }

    /**
     * Initializations speicific to Modify Part Form
     */
    private void initializeModifyPartForm()
    {
        windowLabel.setText("Modify Part");

        // Get existing part class
        if (existingPart.getClass() == InHouse.class) {
            inHouseRadioButton.setSelected(true);
            partSourceTextField.setText(Integer.toString(((InHouse) existingPart).getMachineId()));
        }
        else {
            outsourcedRadioButton.setSelected(true);
            partSourceTextField.setText(((Outsourced)existingPart).getCompanyName());
        }

        // Get existing part variables
        id = existingPart.getId();
        idTextField.setText(Integer.toString(existingPart.getId()));
        nameTextField.setText(existingPart.getName());
        priceTextField.setText(Double.toString(existingPart.getPrice()));
        invTextField.setText(Integer.toString(existingPart.getStock()));
        maxTextField.setText(Integer.toString(existingPart.getMax()));
        minTextField.setText(Integer.toString(existingPart.getMin()));
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
     * Listener only accepts text that is formatted like US Dollars
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

                    if( !textField.getText().isEmpty())
                        price = Double.parseDouble(textField.getText());
        });

        // If textfield loses focus
        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                textField.setText(formatCurrencyText(textField.getText()));
            }
            if (!textField.getText().isEmpty())
                price = Double.parseDouble(textField.getText());
        });
    }

    /**
     * Listener sets textfield value to inventory, min, and max vars
     * Displays error message if inventory values aren't valid
     * @param textField
     */
    private void addInventoryTextFieldListeners(TextField textField) {
        textField.textProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("\\d{0,7}?")) { textField.setText(oldValue); }
                    if (!invTextField.getText().isEmpty()) { inv = Integer.parseInt(invTextField.getText()); }
                    if (!minTextField.getText().isEmpty()) { min = Integer.parseInt(minTextField.getText()); }
                    if (!maxTextField.getText().isEmpty()) { max = Integer.parseInt(maxTextField.getText()); }
                });

        textField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) { checkInvErrorMessage(); } });

        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) { checkInvErrorMessage(); }
        });
    }

    /**
     * Listener formats text to be numbers if InHouse part selected
     * Listener formats text to be a String if Outsourced part is selected
     * Sets textfield value to machine Id or company name accordingly
     * @param textField
     */
    private void addPartSourceListener(TextField textField) {
        textField.textProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (inHouseRadioButton.isSelected()) {
                        if (!newValue.matches("\\d{0,7}?")) { textField.setText(oldValue); }
                        if (!textField.getText().isEmpty()) { machineId = Integer.parseInt(textField.getText()); }
                    }
                    else { companyName = textField.getText(); }
                });
    }

    /**
     * Listener determines if InHouse or Outsourced part is selected
     * Changes interface accordingly
     */
    public void radioButtonListener() {
        // In-House selected
        if (inHouseRadioButton.isSelected()) {
            partSourceLabel.setText("Machine ID");
            if (machineId != -1)
                partSourceTextField.setText(Integer.toString(machineId));
            else
                partSourceTextField.setText("");
        }
        // Outsource selected
        else {
            partSourceLabel.setText("Company Name");
            partSourceTextField.setText(companyName);
        }
    }

    /**
     * Listener checks to see if save is valid
     * If valid, saves part and adds part to Inventory
     */
    public void saveButtonListener() {
        // Check if all fields filled out
        if (!bAllTextFieldsFilled()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Not all fields are filled out.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        // Check if inventory levels valid
        if (!bIsInvValuesCorrect()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Inventory must be between min and max", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        // Save new part, either InHouse or Outsource
        if (formType == EFormType.ADDPART) {
            if (inHouseRadioButton.isSelected())
                Inventory.addPart(new InHouse(id, name, price, inv, min, max, machineId));
            else
                Inventory.addPart(new Outsourced(id, name, price, inv, min, max, companyName));
        }
        // Update part, either InHouse or Outsource
        else {
            if (inHouseRadioButton.isSelected())
                Inventory.updatePart(existingPartIndex, new InHouse(id, name, price, inv, min, max, machineId));
            else
                Inventory.updatePart(existingPartIndex, new Outsourced(id, name, price, inv, min, max, companyName));
        }

        // Close Window
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Closes form without making any changes
     */
    public void cancelButtonListener() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    //</editor-fold>

    //<editor-fold desc="Private Helpers">

    /**
     * Formats text as US Dollar
     * @param text text to format
     * @return Returns string formatted as US dollars. Returns empty string if empty string provided
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
     * Displays inventory error message if levels invalid
     */
    private void checkInvErrorMessage() {
        invErrorLabel.setVisible(bIsInvMaxMinFilledOut() && !bIsInvValuesCorrect());
    }

    /**
     * Checks that inventory is between min and max
     * @return
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
                && !minTextField.getText().isEmpty()
                && !partSourceTextField.getText().isEmpty();
    }

    /**
     *
     * @return Creates product ID based upon last found ID number. Else returns 1000
     */
    public int generateProductId() {
        if (Inventory.getAllParts().isEmpty()) {
            return id = 1000;
        }

        int lastPartId = -1;
        for (Part part: Inventory.getAllParts())
        {
            if (part.getId() > lastPartId)
                lastPartId = part.getId();
        }
        return id = lastPartId + 1;
    }

    //</editor-fold>
}