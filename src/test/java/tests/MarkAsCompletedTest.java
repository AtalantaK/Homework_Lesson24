package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Task;
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
public class MarkAsCompletedTest {
    private static final Path FILEPATH = Path.of("src/test/java/files/MarkAsCompleted.json");
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

        //Комплитим таску
        HttpPatch httpPatchRequest = new HttpPatch(endpoint + taskId);
        String requestBody = Files.readString(FILEPATH);
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

        //Комплитим таску
        HttpPatch httpPatchRequest = new HttpPatch(endpoint + taskId);
        String requestBody = Files.readString(FILEPATH);
        StringEntity stringEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        httpPatchRequest.setEntity(stringEntity);
        HttpResponse response = httpClient.execute(httpPatchRequest);

        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = EntityUtils.toString(response.getEntity());
        Task task = objectMapper.readValue(responseBody, Task.class);

        assertAll("Несколько проверок",
                () -> assertThat(task.getId()).isEqualTo(taskId),
                () -> assertThat(task.getTitle()).startsWith("New task "),
                () -> assertThat(task.getCompleted()).isEqualTo("true"));
    }

    @Test
    @Tag("ContractCase")
    @DisplayName("Проверка Content-Type")
    public void checkContentType() throws IOException {

        //Создаем таску
        int taskId = toDoHelper.createTask();

        //Комплитим таску
        HttpPatch httpPatchRequest = new HttpPatch(endpoint + taskId);
        String requestBody = Files.readString(FILEPATH);
        StringEntity stringEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        httpPatchRequest.setEntity(stringEntity);
        HttpResponse response = httpClient.execute(httpPatchRequest);
        String expectedContentType = "application/json; charset=utf-8";
        String actualContentType = response.getFirstHeader("Content-Type").getValue();

        assertThat(actualContentType).isEqualTo(expectedContentType);
    }

    @Test
    @Tag("ContractCase")
    @DisplayName("Убрать что задача выполнена")
    public void markAsNotCompleted() throws IOException {

        //Создаем таску
        int taskId = toDoHelper.createTask();

        //Комплитим таску
        toDoHelper.completeTask(taskId);

        //Убираем комплит таски
        HttpPatch httpPatchRequest = new HttpPatch(endpoint + taskId);
        String requestBody = Files.readString(FILEPATH);
        requestBody = requestBody.replaceFirst("true", "false");
        StringEntity stringEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        httpPatchRequest.setEntity(stringEntity);
        HttpResponse httpResponse = httpClient.execute(httpPatchRequest);

        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = EntityUtils.toString(httpResponse.getEntity());
        Task task = objectMapper.readValue(responseBody, Task.class);

        assertThat(task.getCompleted()).isEqualTo("false");
    }

    @Test
    @Tag("BusinessCase")
    @DisplayName("Убрать что задача выполнена. Business Case")
    public void markAsNotCompletedBusinessCase() throws IOException {

        //Создаем таску
        int taskId = toDoHelper.createTask();
        toDoHelper.createTask();

        //Комплитим таску
        HttpPatch httpPatchRequest = new HttpPatch(endpoint + taskId);
        String requestBody = Files.readString(FILEPATH);
        StringEntity stringEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        httpPatchRequest.setEntity(stringEntity);
        HttpResponse response = httpClient.execute(httpPatchRequest);

        Task[] tasks = toDoHelper.getTasks();

        assertThat(tasks).anySatisfy(task ->
        {
            assertThat(task.getId()).isEqualTo(taskId);
            assertThat(task.getCompleted()).isEqualTo("true");
        });

    }

    @AfterEach
    public void deleteAllTasks() throws IOException {

        Task[] tasks = toDoHelper.getTasks();

        for (Task task : tasks) {
            toDoHelper.deleteTask(task.getId());
        }
    }
}