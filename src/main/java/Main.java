import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        String key = "iC5Y0hrs7XpMlVihnn7uh3MfcBCzMTh4haxbQyLV";
        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false).build())
                .build()) {
            HttpGet request = new HttpGet("https://api.nasa.gov/planetary/apod?api_key=" + key);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String body = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);

                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();

                ApiNASA apiNASA = gson.fromJson(body, ApiNASA.class);
                String urlImage = apiNASA.getHdurl();
                HttpGet requestImg = new HttpGet(urlImage);
                CloseableHttpResponse responseImg = httpClient.execute(requestImg);
                HttpEntity entity = responseImg.getEntity();
                InputStream inputStream = entity.getContent();

                String[] arr = urlImage.split("/");
                String fileName = arr[arr.length - 1];
                try (FileOutputStream fos = new FileOutputStream(fileName)) {
                    int bytes;
                    while ((bytes = inputStream.read()) != -1) {
                        fos.write(bytes);
                    }
                } catch (FileNotFoundException f) {
                    f.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}