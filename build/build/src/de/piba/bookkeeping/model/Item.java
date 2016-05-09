package de.piba.bookkeeping.model;

import java.time.LocalDate;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.piba.bookkeeping.util.LocalDateAdapter;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model class for a Item.
 * @author piotr bala
 *
 */
public class Item {
	
	private final ObjectProperty<LocalDate> date;
	private final StringProperty category;
	private final StringProperty use;
	private final DoubleProperty amount;
	private final StringProperty distributionKind;
	
	/**
	 * Default Constructor
	 */
	public Item() {
		this(null, null, null, 0, null);
	}
	
	public Item(LocalDate date, String category, String use, double amount, String distributionKind) {
		this.date = new SimpleObjectProperty<LocalDate>(date);
		this.category = new SimpleStringProperty(category);
		this.use = new SimpleStringProperty(use);
		this.amount = new SimpleDoubleProperty(amount);
		this.distributionKind = new SimpleStringProperty(distributionKind);
	}
	
	@XmlJavaTypeAdapter(LocalDateAdapter.class)
	public LocalDate getDate() {
		return date.get();
	}
	
	public void setDate(LocalDate date) {
		this.date.set(date);
	}
	
	public ObjectProperty<LocalDate> dateProperty() {
		return date;
	}
	
	public String getCategory() {
		return category.get();
	}
	
	public void setCategory(String category) {
		this.category.set(category);
	}
	
	public StringProperty categoryProperty() {
		return category;
	}
	
	public String getUse() {
		return use.get();
	}
	
	public void setUse(String use) {
		this.use.set(use);
	}
	
	public StringProperty useProperty() {
		return use;
	}
	
	public double getAmount() {
		return amount.get();
	}
	
	public void setAmount(double amount) {
		this.amount.set(amount);
	}
	
	public DoubleProperty amountProperty() {
		return amount;
	}
	
	public String getDistributionKind() {
		return distributionKind.get();
	}
	
	public void setDistributionKind(String distributionKind) {
		this.distributionKind.set(distributionKind);
	}
	
	public StringProperty distributionKindProperty() {
		return distributionKind;
	}
}
