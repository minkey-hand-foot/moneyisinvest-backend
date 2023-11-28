package org.knulikelion.moneyisinvest.config.security;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
@Service
public class KISApprovalTokenProvider {
    private String approvalToken;

    @Value("${KIS.APP.KEY}")
    private String app_Key;

    @Value("${KIS.APP.SECRET}")
    private String app_Secret;
    private static final String CREATE_APPROVAL_TOKEN_API_URL = "https://openapi.koreainvestment.com:9443/oauth2/tokenP";
    private static final String DESTROY_APPROVAL_TOKEN_API_URL = "https://openapi.koreainvestment.com:9443/oauth2/revokeP";

    @PostConstruct
    protected void init() {
        log.info("[init] ApprovalToken 초기화 시작");
        scheduleTokenRefresh();
        log.info("[init] ApprovalToken 초기화 완료");
    }
    private void scheduleTokenRefresh() {
        TimerTask timerTask = new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                try {
                    JSONObject firstBody = createBody();
                    approvalToken = createApprovalToken(firstBody);
                    System.out.println("[init] First ApprovalToken : " + approvalToken);

                    JSONObject destroyBody = createDestroyBody();
                    String msg = destroyApprovalToken(destroyBody);
                    System.out.println("[init Destroy ApprovalToken] :" + msg);

                    JSONObject secondBody = createBody();
                    approvalToken = createApprovalToken(secondBody);
                    System.out.println("[init] Second ApprovalToken : " + approvalToken);
                } catch (JSONException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Timer timer = new Timer();
        long delay = 0;
        long interval = 12 * 60 * 60 * 1000;

        timer.scheduleAtFixedRate(timerTask, delay, interval);
    }

    public JSONObject createBody() throws JSONException { /*승인 키 받아올 때 사용되는 body 생성 코드 입니다.*/
        JSONObject body = new JSONObject();
        body.put("grant_type", "client_credentials");
        body.put("appkey", app_Key);
        body.put("appsecret", app_Secret);
        return body;
    }

    private JSONObject createDestroyBody(){ /*승인 키 폐기 용 body 생성 코드*/
        JSONObject body = new JSONObject();
        body.put("appkey" , app_Key);
        body.put("appsecret", app_Secret);
        body.put("token", approvalToken);
        System.out.println("[createDestroyBody] : "+ approvalToken);
        return body;
    }

    private String destroyApprovalToken(JSONObject body) throws IOException {
        JSONObject result;
        HttpURLConnection connection;
        URL url = new URL(DESTROY_APPROVAL_TOKEN_API_URL);

        connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) { /*outPutStream 으로 connection 형태 가져옴*/
            byte[] input = body.toString().getBytes("utf-8"); /*body 값을 json 형태로 입력*/
            os.write(input, 0, input.length);
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            result = new JSONObject(response.toString());
        }
        return result.getString("message");
    }

    public String createApprovalToken(JSONObject body) throws IOException, JSONException { /*승인 키 반환하는 코드 입니다.*/
        JSONObject result;
        HttpURLConnection connection;
        URL url = new URL(CREATE_APPROVAL_TOKEN_API_URL);

        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) { /*outPutStream 으로 connection 형태 가져옴*/
            byte[] input = body.toString().getBytes("utf-8"); /*body 값을 json 형태로 입력*/
            os.write(input, 0, input.length);
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            result = new JSONObject(response.toString());
        }
        return result.getString("access_token");
    }
    public String getApprovalToken() {
        return this.approvalToken;
    }
}
