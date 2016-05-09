package de.piba.bookkeeping.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Helper class to wrap a list of items. This is used for saving the
 * list of persons to XML.
 * 
 * @author piotr bala
 */
@XmlRootElement(name = "items")
public class ItemListWrapper {
	
	private List<Item> items;
	
	@XmlElement(name = "item")
	public List<Item> getItems() {
		return items;
	}
	
	public void setItems(List<Item> items) {
		this.items = items;
	}
}
