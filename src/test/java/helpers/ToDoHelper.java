package helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import tests.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ToDoHelper {
    private final static String endpoint = "https://todo-app-sky.herokuapp.com/";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final static Path FILEPATH = Path.of("NewTask.json");

    public ToDoHelper() {
        this.httpClient = HttpClientBuilder.create().build();
    }

    public int createTask() throws IOException {

        HttpPost httpPostRequest = new HttpPost(endpoint);
        String requestBody = Files.readString(FILEPATH);
        StringEntity stringEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        httpPostRequest.setEntity(stringEntity);
        HttpResponse httpPostResponse = httpClient.execute(httpPostRequest);

        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = EntityUtils.toString(httpPostResponse.getEntity());
        Task task = objectMapper.readValue(responseBody, Task.class);
        return task.getId();
    }
}
