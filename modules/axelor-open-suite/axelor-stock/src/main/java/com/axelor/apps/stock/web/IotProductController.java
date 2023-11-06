package com.axelor.apps.stock.web;

import com.axelor.apps.stock.db.IotProduct;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IotProductController {

  private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public void saveIotProduct(ActionRequest request, ActionResponse response) {

    IotProduct currentProduct = request.getContext().asType(IotProduct.class);

    int currentProductId = currentProduct.getMacId();
    String currentProductMac = currentProduct.getMacAddress();
    String currentProductDescription = currentProduct.getMacDescription();
    String currentProductType = currentProduct.getMacType();

    if (currentProductId == 0 || currentProductMac == null || currentProductDescription == null) {
      response.setError(
          "One or more fields were null, please, provide all information required",
          "Null field(s)");
      return;
    }
    if (currentProductMac.length() != 12) {
      response.setError(
          "MAC Address' length should be exactly 12 characters, do not use character such as '-', ':'",
          "MAC Address length error");
      return;
    }

    String apiUrl = ""; // CHANGE TO ACTUAL URL
    String apiUser = ""; // CHANGE TO ACTUAL API USER
    String apiPassword = ""; // CHANGE TE ACTUAL API PASSWORD

    RequestConfig requestConfig = RequestConfig.custom().build();
    List<NameValuePair> postParameters = new ArrayList<>();
    postParameters.add(new BasicNameValuePair("txtId", Integer.toString(currentProductId)));
    postParameters.add(new BasicNameValuePair("txtMacAddress", currentProductMac));
    postParameters.add(new BasicNameValuePair("txtDescription", currentProductDescription));
    postParameters.add(new BasicNameValuePair("txtType", currentProductType));
    HttpClient apiPostClient =
        HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
    HttpPost apiPost = new HttpPost(apiUrl);
    String authString = apiUser + ":" + apiPassword;
    String base64Credentials = Base64.getEncoder().encodeToString(authString.getBytes());
    apiPost.addHeader("Authorization", "Basic " + base64Credentials);
    try {
      apiPost.setEntity(new UrlEncodedFormEntity(postParameters));
    } catch (Exception e) {
      response.setError("Could not set entity", "Error");
    }
    HttpResponse apiResponse = null;
    HttpEntity apiResponseEntity = null;
    String apiResponseString = "";
    JSONObject apiResponseObject = null;
    try {
      apiResponse = apiPostClient.execute(apiPost);
      int statusCode = apiResponse.getStatusLine().getStatusCode();
      if (statusCode != 200) {
        response.setError("Something went wrong while API was running", "Error");
        return;
      }
      response.setInfo("Data was succesfully added to the DB", "Success");
      return;
    } catch (Exception e) {
      response.setError(
          "An unexpected error occurred while getting response from API", "Unexpected error");
    }
  }
}
