package InventoryParse;

public class InventoryStat {

	private String catLoc = null;
	private int qty = 0;
	
	public InventoryStat(String catLoc, int qty) {
		this.catLoc = catLoc;
		this.qty = qty;
	}

	public String getCatLoc() {
		return catLoc;
	}

	public int getQty() {
		return qty;
	}
	
}
