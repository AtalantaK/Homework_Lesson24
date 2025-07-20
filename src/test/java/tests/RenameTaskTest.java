package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import helpers.HttpCodes;
import helpers.ToDoHelper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class RenameTaskTest {
    private static final Path FILEPATH = Path.of("src/test/java/files/RenameTask.json");
    private static final String endpoint = "https://todo-app-sky.herokuapp.com/";
    private HttpClient httpClient;
    private ToDoHelper toDoHelper;

    @BeforeEach
    public void createHttpClient() throws IOException {
        httpClient = HttpClientBuilder.create().build();
        toDoHelper = new ToDoHelper();

        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);

        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = EntityUtils.toString(response.getEntity());
        Task[] tasks = objectMapper.readValue(responseBody, Task[].class);

        for (Task task : tasks) {
            toDoHelper.deleteTask(task.getId());
        }
    }

    @Test
    @DisplayName("Проверка статус кода")
    public void checkStatusCode() throws IOException {

        //Создаем таску
        int taskId = toDoHelper.createTask();

        //Переименовываем таску
        HttpPatch httpPatchRequest = new HttpPatch(endpoint + taskId);
        String requestBody = Files.readString(FILEPATH);
        requestBody = requestBody.replaceFirst("1000", "" + taskId);
        StringEntity stringEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        httpPatchRequest.setEntity(stringEntity);
        HttpResponse response = httpClient.execute(httpPatchRequest);
        int responseCode = response.getStatusLine().getStatusCode();

        //Удаляем таску за собой
        toDoHelper.deleteTask(taskId);

        assertThat(responseCode).isEqualTo(HttpCodes.OK);
    }

    @Test
    @DisplayName("Проверка тела ответа")
    public void checkResponseBody() throws IOException {

        //Создаем таску
        int taskId = toDoHelper.createTask();

        //Переименовываем таску
        HttpPatch httpPatchRequest = new HttpPatch(endpoint + taskId);
        String requestBody = Files.readString(FILEPATH);
        requestBody = requestBody.replaceFirst("1000", "" + taskId);
        StringEntity stringEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        httpPatchRequest.setEntity(stringEntity);
        HttpResponse response = httpClient.execute(httpPatchRequest);
        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = EntityUtils.toString(response.getEntity());
        Task task = objectMapper.readValue(responseBody, Task.class);

        //Удаляем таску за собой
        toDoHelper.deleteTask(taskId);

        assertAll("Несколько проверок",
                () -> assertThat(task.getId()).isEqualTo(taskId),
                () -> assertThat(task.getTitle()).isEqualTo("renamed title"),
                () -> assertThat(task.getCompleted()).isEqualTo("false"));
    }

    @Test
    @DisplayName("Проверка Content-Type")
    public void checkContentType() throws IOException {

        //Создаем таску
        int taskId = toDoHelper.createTask();

        //Переименовываем таску
        HttpPatch httpPatchRequest = new HttpPatch(endpoint + taskId);
        String requestBody = Files.readString(FILEPATH);
        requestBody = requestBody.replaceFirst("1000", "" + taskId);
        StringEntity stringEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        httpPatchRequest.setEntity(stringEntity);
        HttpResponse response = httpClient.execute(httpPatchRequest);
        String expectedContentType = "application/json; charset=utf-8";
        String actualContentType = response.getFirstHeader("Content-Type").getValue();

        //Удаляем таску за собой
        toDoHelper.deleteTask(taskId);

        assertThat(actualContentType).isEqualTo(expectedContentType);
    }

    @Test
    @DisplayName("Переименовать таску на пустой title")
    public void renameTaskWithEmptyTitle() throws IOException {

        //Создаем таску
        int taskId = toDoHelper.createTask();

        //Переименовываем таску
        HttpPatch httpPatchRequest = new HttpPatch(endpoint + taskId);
        String requestBody = Files.readString(FILEPATH);
        requestBody = requestBody.replaceFirst("1000", "" + taskId);
        requestBody = requestBody.replaceFirst("renamed title", "");
        StringEntity stringEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        httpPatchRequest.setEntity(stringEntity);
        HttpResponse httpResponse = httpClient.execute(httpPatchRequest);
        int responseCode = httpResponse.getStatusLine().getStatusCode();
        String response = EntityUtils.toString(httpPatchRequest.getEntity());

        assertAll("Несколько проверок",
                () -> assertThat(responseCode).isEqualTo(HttpCodes.OK),
                () -> assertThat(response).isEqualTo("title cannot be empty"));
    }
}

//todo: добавить бизнес кейс
