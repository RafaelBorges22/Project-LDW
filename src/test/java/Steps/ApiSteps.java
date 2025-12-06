package Steps;

import io.cucumber.java.en.*;
import io.restassured.response.Response;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;

public class ApiSteps {

    private Response response;
    private String baseUrl;
    private Map<String, String> variables = new HashMap<>();

    @Given("the API base url is {string}")
    public void the_api_base_url_is(String url) {
        this.baseUrl = url;
    }

    @When("I GET {string}")
    public void i_get(String endpoint) {

        endpoint = replaceVariables(endpoint);

        response = given()
                .when()
                .get(baseUrl + endpoint)
                .then()
                .extract()
                .response();
    }

    @When("I POST multipart {string} with:")
    public void i_post_multipart_with(String endpoint, io.cucumber.datatable.DataTable dataTable) {

        endpoint = replaceVariables(endpoint);

        Map<String, String> data = dataTable.asMap(String.class, String.class);

        io.restassured.specification.RequestSpecification req =
                given().contentType("multipart/form-data");

        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (!entry.getKey().equalsIgnoreCase("image")) {
                req = req.multiPart(entry.getKey(), entry.getValue()); // Adiciona o fieldName e o valor como parte do formulário
            }
        }

        if (data.containsKey("image")) {
            File file = new File(data.get("image"));
            if (!file.exists()) {
                throw new RuntimeException("Arquivo não encontrado: " + file.getAbsolutePath());
            }
            req = req.multiPart("image", file);
        }

        response = req
                .when()
                .post(baseUrl + endpoint)
                .then()
                .extract()
                .response();
    }


    @When("I POST {string} with JSON:")
    public void i_post_with_json(String endpoint, String body) {

        endpoint = replaceVariables(endpoint);

        response = given()
                .contentType("application/json")
                .body(body)
                .post(baseUrl + endpoint)
                .then()
                .extract()
                .response();
    }
    @And("save the response field {string} as {string}")
    public void saveField(String jsonField, String variableName) {
        String value = response.jsonPath().getString(jsonField);
        variables.put(variableName, value);
    }

    @When("I PUT {string} with JSON:")
    public void i_put_with_json(String endpoint, String body) {

        endpoint = replaceVariables(endpoint);

        response = given()
                .contentType("application/json")
                .body(body)
                .put(baseUrl + endpoint)
                .then()
                .extract()
                .response();
    }

    @When("I DELETE {string}")
    public void i_delete(String endpoint) {

        endpoint = replaceVariables(endpoint);

        response = given()
                .delete(baseUrl + endpoint)
                .then()
                .extract()
                .response();
    }

    @Then("the response status should be {int}")
    public void the_response_status_should_be(Integer statusCode) {
        response.then().statusCode(statusCode);
    }

    private String replaceVariables(String endpoint) {
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            endpoint = endpoint.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return endpoint;
    }
}

