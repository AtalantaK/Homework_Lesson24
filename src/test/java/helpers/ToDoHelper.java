package helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import entities.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class ToDoHelper {
    private final static String endpoint = "https://todo-app-sky.herokuapp.com/";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final static Path FILEPATH_CREATE = Path.of("src/test/java/files/NewTask.json");
    private final static Path FILEPATH_COMPLETE = Path.of("src/test/java/files/MarkAsCompleted.json");

    public ToDoHelper() {
        this.httpClient = HttpClientBuilder.create().build();
    }

    public int createTask() throws IOException {

        HttpPost httpPostRequest = new HttpPost(endpoint);
        String requestBody = Files.readString(FILEPATH_CREATE);
        requestBody = requestBody.replaceFirst("New task", "New task " + randomId());
        StringEntity stringEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        httpPostRequest.setEntity(stringEntity);
        HttpResponse httpPostResponse = httpClient.execute(httpPostRequest);

        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = EntityUtils.toString(httpPostResponse.getEntity());
        Task task = objectMapper.readValue(responseBody, Task.class);

        return task.getId();
    }

    public void deleteTask(int taskId) throws IOException {
        HttpDelete httpDeleteRequest = new HttpDelete(endpoint + taskId);
        HttpResponse httpDeleteResponse = httpClient.execute(httpDeleteRequest);
    }

    public void completeTask(int taskId) throws IOException {
        HttpPatch httpPatchRequest = new HttpPatch(endpoint + taskId);
        String requestBody = Files.readString(FILEPATH_COMPLETE);
        StringEntity stringEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        httpPatchRequest.setEntity(stringEntity);
        HttpResponse response = httpClient.execute(httpPatchRequest);
    }

    public Task[] getTasks() throws IOException {
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);

        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = EntityUtils.toString(response.getEntity());
        Task[] tasks = objectMapper.readValue(responseBody, Task[].class);

        return tasks;
    }

    public int randomId() {
        Random random = new Random();
        return random.nextInt(1000) + 1;
    }
}
