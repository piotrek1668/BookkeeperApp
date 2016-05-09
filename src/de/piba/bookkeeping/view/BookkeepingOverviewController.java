package de.piba.bookkeeping.view;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import de.piba.bookkeeping.MainApp;
import de.piba.bookkeeping.model.Item;
import de.piba.bookkeeping.util.DateUtil;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;

public class BookkeepingOverviewController {
	@FXML
	private TableView<Item> itemTable;
	@FXML
	private TableColumn<Item, LocalDate> dateColumn;
	@FXML
	private TableColumn<Item, String> categoryColumn;
	@FXML
	private TableColumn<Item, String> useColumn;
	@FXML
	private TableColumn<Item, Double> amountColumn;
	@FXML
	private TableColumn<Item, String> distributionKindColumn;

	// Item Details
	@FXML
	private Label dateLabel;
	@FXML
	private Label categoryLabel;
	@FXML
	private Label useLabel;
	@FXML
	private Label amountLabel;
	@FXML
	private Label distributionKindLabel;

	// Table Details
	@FXML
	private Label outputLabel;
	@FXML
	private Label inputLabel;
	@FXML
	private Label totalLabel;
	
	// Selection Details
	@FXML
	private Label selectionLabel;

	// Reference to the main application.
	private MainApp mainApp;

	/**
	 * The constructor. The constructor is called before the initialize()
	 * method.
	 */
	public BookkeepingOverviewController() {
		
	}

	/**
	 * Fills all text fields to show details about the item. If the specified
	 * item is null, all text fields are cleared.
	 * 
	 * @param item
	 *            the item or null
	 */
	private void showItemDetails(Item item) {
		if (item != null) {
			// Fill the labels with info from the item object.
			dateLabel.setText(DateUtil.format(item.getDate()));
			categoryLabel.setText(item.getCategory());
			useLabel.setText(item.getUse());
			if(item.getAmount() < 0) {
				amountLabel.setTextFill(Color.RED);
			} else {
				amountLabel.setTextFill(Color.GREEN);
			}
			amountLabel.setText("€ " + String.format("%.2f", item.getAmount()));
			distributionKindLabel.setText(item.getDistributionKind());
		} else {
			// Item is null, remove all text.
			dateLabel.setText("");
			categoryLabel.setText("");
			useLabel.setText("");
			amountLabel.setText("");
			distributionKindLabel.setText("");
		}
	}

	private void showTableDetails(ObservableList<Item> observableList) {
		double inputSum = 0;
		double outputSum = 0;
		double sum = 0;
		
		for(Item item : observableList) {
			if(item.getAmount() > 0)
				inputSum += item.getAmount();
			if(item.getAmount() <= 0)
				outputSum += item.getAmount();
			sum += item.getAmount();
		}
		inputLabel.setText("€ " + String.format("%.2f", inputSum));
		inputLabel.setTextFill(Color.GREEN);
		outputLabel.setText("€ " + String.format("%.2f", outputSum));
		outputLabel.setTextFill(Color.RED);
		totalLabel.setText("€ " + String.format("%.2f", sum));

	}
	
	private void showSelectionDetails(ObservableList<Item> observableList) {
		Double amount = 0.0;
		try {
			for(Item item : observableList) {
				amount += item.getAmount();
			}
			selectionLabel.setText("€ " + String.format("%.2f", amount));
			if( amount < 0)
				selectionLabel.setTextFill(Color.RED);
			else
				selectionLabel.setTextFill(Color.GREEN);
		} catch(NullPointerException e) {
			
		}
	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml has been loaded.
	 */
	@FXML
	private void initialize() {
		DateTimeFormatter myDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		
		// Initialize the item table with the columns.
		dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
		categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
		useColumn.setCellValueFactory(cellData -> cellData.getValue().useProperty());
		amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());
		distributionKindColumn.setCellValueFactory(cellData -> cellData.getValue().distributionKindProperty());

		dateColumn.setCellFactory(column -> {
			return new TableCell<Item, LocalDate>() {
				@Override
				protected void updateItem(LocalDate item, boolean empty) {
					super.updateItem(item, empty);
					
					if(item == null || empty) {
						setText(null);
						setStyle("");
					} else {
						// Format date.
						setText(myDateFormatter.format(item));
					}
				}
			};
		});
		
		amountColumn.setCellFactory(column -> {
			return new TableCell<Item, Double>() {
				protected void updateItem(Double item, boolean empty) {
					super.updateItem(item, empty);
					
					if(item == null || empty) {
						setText(null);
						setStyle("");
					} else {
						if(item < 0) {
							setTextFill(Color.RED);
						} else {
							setTextFill(Color.GREEN);
						}
						setText("€ " + String.format("%.2f", item));
					}
				}
			};
		});
		// Clear items details.
		showItemDetails(null);

		// Listen for selection changes and show the item details when changed.
		itemTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showItemDetails(newValue));
		
		// set the selectionmode to Multiple
		itemTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		selectionLabel.setText("");
		
		// listen to selection to the list and calls the showSelectionDetails function
		itemTable.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				showSelectionDetails(itemTable.getSelectionModel().getSelectedItems());
			}
		});
	}

	/**
	 * Called when user clicks on the delete button.
	 */
	@FXML
	private void handleDeleteItem() {
		int selectedIndex = itemTable.getSelectionModel().getSelectedIndex();
		if (selectedIndex >= 0) {
			itemTable.getItems().remove(selectedIndex);
			MainApp.setTableChanged(true);
		} else {
			// Nothing selected
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle("Keine Auswahl");
			alert.setHeaderText("Kein Posten gewählt");
			alert.setContentText("Selektiere ein Posten in der Tabelle.");
			alert.showAndWait();
		}
	}

	/**
	 * Called when the user clicks the new button. Opens a dialog to edit
	 * details for a new item.
	 */
	@FXML
	private void handleNewItem() {
		Item tempItem = new Item();
		boolean okClicked = mainApp.showItemEditDialog(tempItem);
		if (okClicked) {
			mainApp.getItemData().add(tempItem);
			MainApp.setTableChanged(true);
		}
	}

	/**
	 * Called when the user clicks the edit button. Opens a dialog to edit
	 * details for the selected item.
	 */
	@FXML
	private void handleEditItem() {
		Item selectedItem = itemTable.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			boolean okClicked = mainApp.showItemEditDialog(selectedItem);
			if (okClicked) {
				showItemDetails(selectedItem);
				MainApp.setTableChanged(true);
			}
		} else {
			// Nothing selected.
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle("Keine Auswahl");
			alert.setHeaderText("Keinen Posten gewählt");
			alert.setContentText("Wähle einen Posten in der Tabelle aus.");
			alert.showAndWait();
		}
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		
		itemTable.
        setStyle("-fx-selection-bar: burlywood; -fx-selection-bar-non-focused: burlywood;");
		// Add observable list data to the table
		itemTable.setItems(mainApp.getItemData());
		mainApp.getItemData().addListener(new ListChangeListener<Item>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Item> c) {
				showTableDetails(itemTable.getItems());
			}
		});
		showTableDetails(itemTable.getItems());
	}
}
