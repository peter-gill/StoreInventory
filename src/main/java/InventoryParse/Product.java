package InventoryParse;

public class Product {

	private String id = null;
	private String sku = null;
	private String description = null;
	private String category = null;
	private Double price = null;
	private String location = null;
	private Long qty = null;

	Product(String id, String sku, String description, String category, Double price, String location, Long qty) {
		
		this.id = id;
		this.sku = sku;
		this.description = description;
		this.category = category;
		this.price = price;
		this.location = location;
		this.qty = qty;
		
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Long getQty() {
		return qty;
	}

	public void setQty(Long qty) {
		this.qty = qty;
	}

}
