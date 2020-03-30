package tcon.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tcon.paypal.PayPalClient;
import tcon.paypal.ProductPackage;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/paypal")
public class PayPalController {
    String clientId = "ASZB5bk10KdqKx4r62-rRutRfe7A-RbmvJpyXTXTDcm8uHhTCQ1woN7yAdRJkoMaN07B1TLWKfa6x7AV";
    String clientSecret = "ENOV9Hk5HR1FrIT5CUEC4Jjk0wMf33kM11FFDNKB-L4Vbp47CMutExYiW6SofaeGQUg_8qv3jtQEaxo8";
    String mode = "sandbox";//"live"
    private PayPalClient payPalClient = new PayPalClient(clientId, clientSecret, mode);

    @GetMapping(value = "/createPayment")
    public Map<String, Object> makePayment(@RequestParam("package") String packageName){
        ProductPackage productPackage = ProductPackage.valueOf(packageName);
        Map<String, Object> response = new HashMap<>();
        response.put("token", payPalClient.createPayment(productPackage));
        return response;
    }

    @GetMapping(value = "/completePayment")
    public Map<String, Object> completePayment(@RequestParam("paymentId") String paymentId, @RequestParam("payerId") String payerId){
        return payPalClient.completePayment(paymentId, payerId);
    }

}
