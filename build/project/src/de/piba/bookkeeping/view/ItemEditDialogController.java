package de.piba.bookkeeping.view;

import java.time.LocalDate;

import de.piba.bookkeeping.model.Item;
import de.piba.bookkeeping.util.DateUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

/**
 * Dialog to eidt details of a item
 * 
 * @author piotr bala
 *
 */
public class ItemEditDialogController {

	@FXML
	private TextField dateField;
	@FXML
	private ComboBox<String> categoryField;
	@FXML
	private TextField useField;
	@FXML
	private TextField amountField;
	@FXML
	private TextField distributionKindField;

	private Stage dialogStage;
	private Item item;
	private boolean okClicked = false;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after fxml files has been loaded.
	 */
	@FXML
	private void initialize() {
		ObservableList<String> categories = FXCollections.observableArrayList("Haushalt", "Einkauf", "Auto", "Lohn",
				"Unterhaltung", "Versicherung", "Lebensmittel", "Sonstiges");
		categoryField.setItems(categories);
		//categoryField.setValue("Lebensmittel");
		useField.requestFocus();
	}

	/**
	 * Sets the stage of this dialog.
	 * 
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	/**
	 * Sets the item to be edited in the dialog.
	 * 
	 * @param item
	 */
	public void setItem(Item item) {
		this.item = item;

		if (item.getDate() == null)
			dateField.setText(DateUtil.format(LocalDate.now()));
		else
			dateField.setText(DateUtil.format(item.getDate()));

		dateField.setPromptText("dd.mm.yyyy");
		if (item.getCategory() == null)
			categoryField.getSelectionModel().select("Lebensmittel");
		else 
			categoryField.getSelectionModel().select(item.getCategory());
		useField.setText(item.getUse());
		amountField.setText(Double.toString(item.getAmount()).replace(".", ","));
		if (item.getDistributionKind() == null)
			distributionKindField.setText("Giro");
		else
			distributionKindField.setText(item.getDistributionKind());

		if (useField.getText() == null) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					useField.requestFocus();
				}
			});
		}
	}

	/**
	 * Returns true if the user clicked OK, false otherwise.
	 * 
	 * @return
	 */
	public boolean isOkClicked() {
		return okClicked;
	}

	/**
	 * Called when the user clicks ok.
	 */
	@FXML
	private void handleOk() {
		if (isInputValid()) {
			item.setDate(DateUtil.parse(dateField.getText()));
			item.setCategory(categoryField.getSelectionModel().getSelectedItem());
			item.setUse(useField.getText());
			
			String textBefore = amountField.getText();
			String textAfter = null;
			if(textBefore.contains(",")) {
				textAfter = textBefore.replace(",", ".");
			} else {
				textAfter = textBefore;
			}
			item.setAmount(Double.parseDouble(textAfter));
			item.setDistributionKind(distributionKindField.getText());

			okClicked = true;
			dialogStage.close();
		}
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	/**
	 * Validates the user input in the text fields.
	 * 
	 * @return true if the input is valid
	 */
	private boolean isInputValid() {
		String errorMessage = "";
		
		if (dateField.getText() == null || dateField.getText().length() == 0) {
			errorMessage += "Kein gültiges Datum!\n";
		} else {
			if (!DateUtil.validDate(dateField.getText())) {
				errorMessage += "Kein gültiges Datum. Benutze das dd.mm.yyy Format!\n";
			}
		}
		String categoryFieldSelection = categoryField.getSelectionModel().getSelectedItem();
		if (categoryFieldSelection.isEmpty() || categoryFieldSelection.length() == 0) {
			errorMessage += "Keine gültige Kategorie!\n";
		}
		if (useField.getText() == null || useField.getText().length() == 0) {
			errorMessage += "Kein gültiger Verwendungszweck!\n";
		}
		if (amountField.getText() == null || amountField.getText().length() == 0) {
			errorMessage += "Kein gültiger Betrag!\n";
		} 
		/**else {
			// try to parse the amount into a double
			try {
				Double.parseDouble(amountField.getText());
			} catch (NumberFormatException e) {
				errorMessage += "Kein zulässiger Betrag! (Nur Dezimalzahlen erlaubt)\n";
			}
		}**/
		if (distributionKindField.getText() == null || distributionKindField.getText().length() == 0) {
			errorMessage += "Keine gültige Ausgabeart!\n";
		}

		if (errorMessage.length() == 0) {
			return true;
		} else {
			// Show the error message.
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(dialogStage);
			alert.setTitle("Ungültige Eingaben");
			alert.setHeaderText("Bitte korrigieren Sie die Fehler in den Feldern.");
			alert.setContentText(errorMessage);
			alert.showAndWait();
			return false;
		}
	}
}
