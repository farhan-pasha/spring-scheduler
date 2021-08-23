package com.keembay.scheduler.job.reader;

import com.bugsnag.Bugsnag;
import com.google.gson.Gson;
import com.keembay.scheduler.dto.TokenDto;
import com.keembay.scheduler.dto.UserRenewalDTO;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class RestUserRenewalReader implements ItemReader<UserRenewalDTO> {
    private int nextUserIndex;
    private List<UserRenewalDTO> userRenewalData;

    @Autowired
    private Bugsnag bugsnag;

    @Value("${expired_user.api.apiUrl}")
    private String apiURL;

    @Value("${expired_user.api.username}")
    private String username;

    @Value("${expired_user.api.password}")
    private String password;

    @Value("${expired_user.api.renewalBaseUrl}")
    private String renewalBaseUrl;

    @Value("${expired_user.api.authenticationBaseUrl}")
    private String authenticationBaseUrl;

    public RestUserRenewalReader() {
        nextUserIndex = 0;
    }

    private String login(RestTemplate restTemplate) throws Exception {

        try {
            URI uri = new URI(renewalBaseUrl + authenticationBaseUrl);
            String token;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            JSONObject paymentJsonObject = new JSONObject();

            paymentJsonObject.put("username", username);
            paymentJsonObject.put("password", password);

            HttpEntity<String> request =
                    new HttpEntity<>(paymentJsonObject.toString(), headers);

            ResponseEntity<String> webHookResponse =
                    restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

            log.info("Payment Response, status code = {}, Body = {} ",
                    webHookResponse.getStatusCode(), webHookResponse.getBody());

            Gson gson = new Gson();

            TokenDto tokenDto = gson.fromJson(webHookResponse.getBody(), TokenDto.class);
            return "Bearer " + tokenDto.getToken();
        } catch (final URISyntaxException e) {
            bugsnag.notify(e);
            e.printStackTrace();
        } catch (final Exception e) {
            bugsnag.notify(e);
            e.printStackTrace();
        }
        return null;
    }

    private boolean userRenewalDataIsNotInitialized() {
        return this.userRenewalData == null;
    }

    private List<UserRenewalDTO> fetchUserDataFromAPI() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String token = login(restTemplate);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<UserRenewalDTO[]> response =
                restTemplate.exchange(apiURL, HttpMethod.GET, request, UserRenewalDTO[].class);
        UserRenewalDTO[] data = response.getBody();
        return Arrays.asList(data);
    }

    @Override
    public UserRenewalDTO read() throws Exception {
        if(userRenewalDataIsNotInitialized()) {
            userRenewalData = fetchUserDataFromAPI();
        }

        UserRenewalDTO nextUser = null;

        if(nextUserIndex < userRenewalData.size()) {
            nextUser = userRenewalData.get(nextUserIndex);
            nextUserIndex++;
        } else {
            nextUserIndex = 0;
            userRenewalData =null;
        }
        return nextUser;
    }


}
