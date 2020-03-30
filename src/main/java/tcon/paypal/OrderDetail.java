package tcon.paypal;

public class OrderDetail {
	private String productName;
	private String currency;
	private String subtotal;
	private String shipping;
	private String tax;
	private String total;

	public OrderDetail(String productName, String currency, String subtotal,
					   String shipping, String tax, String total) {
		this.productName = productName;
		this.currency = currency;
		this.subtotal = subtotal;
		this.shipping = shipping;
		this.tax = tax;
		this.total = total;
	}

	public String getProductName() {
		return productName;
	}

	public String getSubtotal() {
		return subtotal;
	}

	public String getShipping() {
		return shipping;
	}

	public String getTax() {
		return tax;
	}

	public String getCurrency() {
		return currency;
	}

	public String getTotal() {
		return total;
	}
}
