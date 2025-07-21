package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import helpers.HttpCodes;
import helpers.MyWatchers;
import helpers.ToDoHelper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(MyWatchers.class)
public class RenameTaskTest {
    private static final Path FILEPATH = Path.of("src/test/java/files/RenameTask.json");
    private static final String endpoint = "https://todo-app-sky.herokuapp.com/";
    private HttpClient httpClient;
    private ToDoHelper toDoHelper;

    @BeforeEach
    public void createHttpClient() throws IOException {
        httpClient = HttpClientBuilder.create().build();
        toDoHelper = new ToDoHelper();

        Task[] tasks = toDoHelper.getTasks();

        for (Task task : tasks) {
            toDoHelper.deleteTask(task.getId());
        }
    }

    @Test
    @Tag("ContractCase")
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

        assertThat(responseCode).isEqualTo(HttpCodes.OK);
    }

    @Test
    @Tag("ContractCase")
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

        assertAll("Несколько проверок",
                () -> assertThat(task.getId()).isEqualTo(taskId),
                () -> assertThat(task.getTitle()).isEqualTo("Renamed Task"),
                () -> assertThat(task.getCompleted()).isEqualTo("false"));
    }

    @Test
    @Tag("ContractCase")
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

        assertThat(actualContentType).isEqualTo(expectedContentType);
    }

    @Test
    @Tag("ContractCase")
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

    @Test
    @Tag("BusinessCase")
    @DisplayName("Переименовать таску. Business case")
    public void renameTaskBusinessCase() throws IOException {
        //Создаем таску
        int taskId = toDoHelper.createTask();

        //Переименовываем таску
        HttpPatch httpPatchRequest = new HttpPatch(endpoint + taskId);
        String requestBody = Files.readString(FILEPATH);
        requestBody = requestBody.replaceFirst("1000", "" + taskId);
        StringEntity stringEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        httpPatchRequest.setEntity(stringEntity);
        httpClient.execute(httpPatchRequest);

        Task[] tasks = toDoHelper.getTasks();
        Task expectedTask = new Task(taskId, "Renamed Task", "false");

        assertThat(tasks).usingRecursiveFieldByFieldElementComparator().contains(expectedTask);
    }

    @AfterEach
    public void deleteAllTasks() throws IOException {

        Task[] tasks = toDoHelper.getTasks();

        for (Task task : tasks) {
            toDoHelper.deleteTask(task.getId());
        }
    }
}
