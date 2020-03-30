package tcon.paypal;

public enum ProductPackage {
		COMPANION("19.90"), PLUS("29.90"), PRO("49.90");
		String price;
	ProductPackage(String price) {
		this.price = price;
	}
}
