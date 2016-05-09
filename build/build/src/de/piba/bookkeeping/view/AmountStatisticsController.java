package de.piba.bookkeeping.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;

/**
 * The controller for the amount statistics view.
 * @author piotr bala
 *
 */
public class AmountStatisticsController {
	
	@FXML
	private BarChart<String, Integer> barChart;
	
	@FXML
	private CategoryAxis xAxis;
	
	private ObservableList<String> dayNames = FXCollections.observableArrayList();
	
	@FXML
	private void initialize() {
		// Get an array with the days names.
		// TODO: This class is not finished now
	}
}
