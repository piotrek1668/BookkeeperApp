package de.piba.bookkeeping.view;

import java.io.File;
import java.util.Optional;

import de.piba.bookkeeping.MainApp;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

/**
 * The controller for the root layout. The root layout provides the basic
 * application layout containing a menu bar and space where other JavaFX
 * elements can be placed.
 * 
 * @author piotr bala
 */
public class RootLayoutController {

	// Reference to the main application.
	private MainApp mainApp;
	private static String version = "1.0.2";

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	/**
	 * Creates an empty item book.
	 */
	@FXML
	private void handleNew() {
		mainApp.getItemData().clear();
		mainApp.setItemFilePath(null);
		MainApp.setTableChanged(true);
		// TODO: Hier koennen dann wiederkehrende Ein- und Ausgaben direkt
		// hinzugefuegt werden
	}

	/**
	 * Opens a FileChooser to let the user select an item book to load.
	 */
	@FXML
	private void handleOpen() {
		FileChooser fileChooser = new FileChooser();

		// Set extension filter.
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML Dateien (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);

		// Show save file dialog
		File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

		if (file != null) {
			mainApp.loadItemDataFromFile(file);
		}
	}

	/**
	 * Saves the file to the item file that is currently open. If there is no
	 * open file, the "save as" dialog is shown.
	 */
	@FXML
	private void handleSave() {
		File itemFile = mainApp.getItemFilePath();
		if (itemFile != null) {
			mainApp.saveItemDataToFile(itemFile);
			MainApp.setTableChanged(false);
		} else {
			handleSaveAs();
		}
	}

	/**
	 * Opens a FileChooser to let the user select a file to save to.
	 */
	@FXML
	private void handleSaveAs() {
		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML Dateien (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);

		// Show save file dialog
		File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

		if (file != null) {
			// Make sure it has correct extension
			if (!file.getPath().endsWith(".xml")) {
				file = new File(file.getPath() + ".xml");
			}
			mainApp.saveItemDataToFile(file);
			MainApp.setTableChanged(false);
		}
	}

	/**
	 * Opens an about dialog.
	 */
	@FXML
	private void handleAbout() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Buchführung App");
		alert.setHeaderText("Über uns");
		alert.setContentText("Version: " + version
				+ "\nAutor: Piotr Bala\nWebsite: http://www.piba-soft.de/\nLizenz:\nThe MIT License (MIT)\n\nCopyright (c) 2016 Piotr Bala\n\nPermission is hereby granted, free of charge, to any person obtaining a copy\nof this software and associated documentation files (the \"Software\"), to deal\nin the Software without restriction, including without limitation the rights\nto use, copy, modify, merge, publish, distribute, sublicense, and/or sell\ncopies of the Software, and to permit persons to whom the Software is\nfurnished to do so, subject to the following conditions:\n\nThe above copyright notice and this permission notice shall be included in all\ncopies or substantial portions of the Software.\n\nTHE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\nIMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\nFITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\nAUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\nLIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\nOUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\nSOFTWARE.");
		alert.showAndWait();
	}

	/**
	 * Closes the application.
	 */
	@FXML
	public void handleExit() {
		if (MainApp.getTableChanged()) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Programm beenden");
			alert.setHeaderText("Änderungen im Programm wurden nicht gespeichert, wollen Sie das Programm\n"
					+ "ohne zu Speichern verlassen?");

			ButtonType saveAndClose = new ButtonType("Speichern und Beenden");
			ButtonType onlyClose = new ButtonType("Beenden ohne zu Speichern");
			ButtonType cancel = new ButtonType("Zurück zum Programm", ButtonData.CANCEL_CLOSE);

			alert.getButtonTypes().setAll(saveAndClose, onlyClose, cancel);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == saveAndClose) {
				handleSave();
				Platform.exit();
			} else if (result.get() == onlyClose) {
				Platform.exit();
			} else {
				// ... do nothing
			}
		} else {
			Platform.exit();
		}

	}
}
