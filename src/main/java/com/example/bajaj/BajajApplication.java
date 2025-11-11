package com.example.bajaj;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@SpringBootApplication
public class BajajApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(BajajApplication.class, args);
    }

    @Override
    public void run(String... args) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

            Map<String, String> body = Map.of(
                "name", "Sinchana H",
                "regNo", "PES1UG22CS596",
                "email", "sinchanasinchu2062004@gmail.com"
            );

            ResponseEntity<Map> response = restTemplate.postForEntity(generateUrl, body, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody == null) {
                System.out.println("Error: No response received from webhook generation.");
                return;
            }

            String webhookUrl = (String) responseBody.get("webhook");
            String accessToken = (String) responseBody.get("accessToken");

            System.out.println("Webhook URL: " + webhookUrl);
            System.out.println("Access Token: " + accessToken);

            
            String sqlQuery = "SELECT E1.EMP_ID, E1.FIRST_NAME, E1.LAST_NAME, D.DEPARTMENT_NAME, "
                    + "COUNT(E2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT "
                    + "FROM EMPLOYEE E1 "
                    + "JOIN DEPARTMENT D ON E1.DEPARTMENT = D.DEPARTMENT_ID "
                    + "LEFT JOIN EMPLOYEE E2 ON E1.DEPARTMENT = E2.DEPARTMENT "
                    + "AND E2.DOB > E1.DOB "
                    + "GROUP BY E1.EMP_ID, E1.FIRST_NAME, E1.LAST_NAME, D.DEPARTMENT_NAME "
                    + "ORDER BY E1.EMP_ID DESC;";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", accessToken);

            Map<String, String> queryBody = Map.of("finalQuery", sqlQuery);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(queryBody, headers);

            ResponseEntity<String> result = restTemplate.postForEntity(webhookUrl, request, String.class);

            System.out.println("Submission Response: " + result.getBody());
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
