package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import helpers.HttpCodes;
import helpers.MyWatchers;
import helpers.ToDoHelper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
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
public class CreateNewTaskTest {

    private static final Path FILEPATH = Path.of("src/test/java/files/NewTask.json");
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
        HttpPost request = new HttpPost(endpoint);
        String requestBody = Files.readString(FILEPATH);
        StringEntity stringEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        request.setEntity(stringEntity);
        HttpResponse response = httpClient.execute(request);
        int responseCode = response.getStatusLine().getStatusCode();

        assertThat(responseCode).isEqualTo(HttpCodes.OK);
    }

    @Test
    @Tag("ContractCase")
    @DisplayName("Проверка тела ответа")
    public void checkResponseBody() throws IOException {
        HttpPost request = new HttpPost(endpoint);
        String requestBody = Files.readString(FILEPATH);
        StringEntity stringEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        request.setEntity(stringEntity);
        HttpResponse response = httpClient.execute(request);

        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = EntityUtils.toString(response.getEntity());
        Task task = objectMapper.readValue(responseBody, Task.class);

        assertAll("Несколько проверок",
                () -> assertThat(task.getId()).isPositive(),
                () -> assertThat(task.getTitle()).isEqualTo("New task"),
                () -> assertThat(task.getCompleted()).isEqualTo("false"));
    }

    @Test
    @Tag("ContractCase")
    @DisplayName("Проверка Content-Type")
    public void checkContentType() throws IOException {
        HttpPost request = new HttpPost(endpoint);
        String requestBody = Files.readString(FILEPATH);
        StringEntity stringEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        request.setEntity(stringEntity);
        HttpResponse response = httpClient.execute(request);
        String expectedContentType = "application/json; charset=utf-8";
        String actualContentType = response.getFirstHeader("Content-Type").getValue();

        assertThat(actualContentType).isEqualTo(expectedContentType);
    }

    @Test
    @Tag("BusinessCase")
    @DisplayName("Проверка Content-Type. Business Case")
    public void createNewTaskBusinessCase() throws IOException {

        toDoHelper.createTask();

        HttpPost request = new HttpPost(endpoint);
        String requestBody = Files.readString(FILEPATH);
        StringEntity stringEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        request.setEntity(stringEntity);
        HttpResponse response = httpClient.execute(request);

        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = EntityUtils.toString(response.getEntity());
        Task task = objectMapper.readValue(responseBody, Task.class);

        Task expectedTask = new Task(task.getId(), "New task", "false");

        Task[] tasks = toDoHelper.getTasks();

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