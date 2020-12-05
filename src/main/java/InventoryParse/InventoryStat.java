package InventoryParse;

public class InventoryStat {

	private String productType = null;
	private String location = null;
	private int qty = 0;
	
	public InventoryStat(String productType, String location, int qty) {
		this.productType = productType;
		this.location = location;
		this.qty = qty;
	}

	public String getProductType() {
		return productType;
	}

	public String getLocation() {
		return location;
	}

	public int getQty() {
		return qty;
	}
	
}
