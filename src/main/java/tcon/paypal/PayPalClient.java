package tcon.paypal;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

public class PayPalClient {
    private String clientId;
    private String clientSecret;
    private String mode;
    private String cancelUrl = "/admin/account-info";
    private String returnUrl = "/admin/account-info";

    public PayPalClient(String clientId, String clientSecret, String mode){
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.mode = mode;
    }

    public String createPayment(ProductPackage productPackage){
        OrderDetail orderDetail = new OrderDetail(productPackage.name(), "USD", productPackage.price, "0", "0", productPackage.price);
        return createPayment(orderDetail);
    }

    public String createPayment(OrderDetail orderDetail){
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment("sale", payer)
                .setTransactions(singletonList(transaction(orderDetail)))
                .setRedirectUrls(new RedirectUrls()
                        .setCancelUrl(cancelUrl)
                        .setReturnUrl(returnUrl));
        try {
            return token(payment.create(new APIContext(clientId, clientSecret, mode)));
        } catch (PayPalRESTException e) {
            throw new RuntimeException("Error happened during payment creation!", e);
        }
    }

    private String token(Payment createdPayment) {
        return createdPayment.getLinks().stream()
                .filter(link -> link.getRel().equalsIgnoreCase("approval_url"))
                .findFirst()
                .get()
                .getHref()
                .split("token=")[1];
    }

    private Transaction transaction(OrderDetail orderDetail) {
        Details details = new Details()
                .setShipping(orderDetail.getShipping())
                .setSubtotal(orderDetail.getSubtotal())
                .setTax(orderDetail.getTax());

        Amount amount = new Amount()
                .setCurrency(orderDetail.getCurrency())
                .setTotal(orderDetail.getTotal())
                .setDetails(details);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription(orderDetail.getProductName());

        ItemList itemList = new ItemList();
        List<Item> items = new ArrayList<>();

        Item item = new Item()
                .setCurrency(orderDetail.getCurrency())
                .setName(orderDetail.getProductName())
                .setPrice(orderDetail.getSubtotal())
                .setTax(orderDetail.getTax())
                .setQuantity("1");

        items.add(item);
        itemList.setItems(items);
        transaction.setItemList(itemList);

        return transaction;
    }

    public Map<String, Object> completePayment(String paymentId, String payerId){
        Map<String, Object> response = new HashMap();
        PaymentExecution paymentExecution = new PaymentExecution()
                .setPayerId(payerId);
        APIContext context = new APIContext(clientId, clientSecret, mode);

        try {
            Payment createdPayment = new Payment()
                    .setId(paymentId)
                    .execute(context, paymentExecution);

            Transaction transaction = createdPayment.getTransactions().get(0);
            //43D80682DY151700D , SaleID is seen as Transaction ID on paypal account of merchant
            response.put("saleId", transaction.getRelatedResources().get(0).getSale().getId());
            response.put("package", transaction.getItemList().getItems().get(0).getName());
            response.put("payer", createdPayment.getPayer().getPayerInfo());

            return response;
        } catch (PayPalRESTException e) {
            throw new RuntimeException("Error happened during completion of payment with payment id:" + paymentId + ", payer id: " + payerId, e);
        }
    }
}
