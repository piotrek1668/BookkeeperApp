package de.piba.bookkeeping;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import de.piba.bookkeeping.model.Item;
import de.piba.bookkeeping.model.ItemListWrapper;
import de.piba.bookkeeping.view.BookkeepingOverviewController;
import de.piba.bookkeeping.view.ItemEditDialogController;
import de.piba.bookkeeping.view.RootLayoutController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;
	private ObservableList<Item> itemData = FXCollections.observableArrayList();
	private static boolean tableChanged = false;
	
	/**
	 * Constructor
	 */
	public MainApp() {
	}
	
	/**
	 * Returns the data as an observable list of items.
	 * @return
	 */
	public ObservableList<Item> getItemData() {
		return itemData;
	}
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Buchführung App");
		// Set the application item.
		this.primaryStage.getIcons().add(new Image("file:resources/images/Icon.ico"));
		initRootLayout();
		showBookkeepingOverview();
	}

	/**
	 * Shows the bookkeeping overview inside the root layout.
	 */
	public void showBookkeepingOverview() {
		try {
			// Load booking overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/BookkeepingOverview.fxml"));
			AnchorPane bookkeepingOverview = (AnchorPane) loader.load();
			
			// Set bookkeeping overview into the center of root layout.
			rootLayout.setCenter(bookkeepingOverview);
			
			// Give the controller access to the main app.
			BookkeepingOverviewController controller = loader.getController();
			controller.setMainApp(this);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Opens a dialog to edit details for the specified item. If the user
	 * clicks OK, the changes are saved into the provided item object and true
	 * is returned.
	 * 
	 * @param item the item object to be edited
	 * @return true if the user clicked OK, false otherwise.
	 */
	public boolean showItemEditDialog(Item item) {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/ItemEditDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			
			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Posten bearbeiten");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			
			// Set the item into the controller.
			ItemEditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setItem(item);
			
			// Show the dialog an wait until the user closes it
			dialogStage.showAndWait();
			
			return controller.isOkClicked();
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Initializes the root layout.
	 */
	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			
			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			
			// Give the controller access to the main app.
			RootLayoutController controller = loader.getController();
			controller.setMainApp(this);
			Platform.setImplicitExit(false);
			this.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent event) {
					event.consume();
					controller.handleExit();
				}
			});
			primaryStage.show();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		// Try to load last opened item file.
		File file = getItemFilePath();
		if(file != null) {
			loadItemDataFromFile(file);
		}
	}
	
	/**
	 * Returns the item file preference, i.e. the file that was last opened.
	 * The preference is read from the OS specific registry. If no such
	 * preference can be found, null is returned.
	 * 
	 * @return
	 */
	public File getItemFilePath() {
		Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
		String filePath = prefs.get("filePath", null);
		if(filePath != null) {
			return new File(filePath);
		} else {
			return null;
		}
	}
	
	/**
	 * Sets the file path of the currently loaded file. The path is persisted in
	 * the OS specific registry.
	 * 
	 * @param file the file or null to remove the path
	 */
	public void setItemFilePath(File file) {
		Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
		if(file != null) {
			prefs.put("filePath", file.getPath());
			
			// Update the stage title.
			primaryStage.setTitle("Buchführung App - " + file.getName());
		} else {
			prefs.remove("filePath");
			
			// Update the stage title.
			primaryStage.setTitle("Buchführung App");
		}
	}
	
	/**
	 * Loads item data from the specified file. The current item data will
	 * be replaced.
	 * 
	 * @param file
	 */
	public void loadItemDataFromFile(File file) {
		try {
			JAXBContext context = JAXBContext.newInstance(ItemListWrapper.class);
			Unmarshaller um = context.createUnmarshaller();
			
			// Reading XML from the file and unmarshalling.
			ItemListWrapper wrapper = (ItemListWrapper) um.unmarshal(file);
			
			itemData.clear();
			itemData.addAll(wrapper.getItems());
			
			// Save the file path to the registry.
			setItemFilePath(file);
		} catch(Exception e) { // Catches ANY exception
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Konnte keine Daten laden");
			alert.setContentText("Konnte Datei mit folgendem Pfad nicht öffnen:\n" + file.getPath());
			alert.showAndWait();
		}
	}
	
	public void saveItemDataToFile(File file) {
		try {
			JAXBContext context = JAXBContext.newInstance(ItemListWrapper.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			// Wrapping our item data.
			ItemListWrapper wrapper = new ItemListWrapper();
			wrapper.setItems(itemData);
			
			// Marshalling and saving XML to the file.
			m.marshal(wrapper, file);
			
			// Saving the file path to the registry.
			setItemFilePath(file);
		} catch(Exception e) { // Caches ANY exception
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Konnte keine Daten speichern.");
			alert.setContentText("Konnte die Daten in folgender Datei nicht speichern:\n" + file.getPath());
			alert.showAndWait();
		}
	}
	
	/**
	 * Returns the main stage.
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	public static boolean getTableChanged() {
		return tableChanged;
	}
	
	public static void setTableChanged(boolean bool) {
		tableChanged = bool;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
