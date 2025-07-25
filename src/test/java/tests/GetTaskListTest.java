package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Task;
import helpers.HttpCodes;
import helpers.MyWatchers;
import helpers.ToDoHelper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(MyWatchers.class)
public class GetTaskListTest {

    private final static String endpoint = "https://todo-app-sky.herokuapp.com/";
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
    @DisplayName("Проверка статус кода")
    public void checkStatusCode() throws IOException {

        //Создаем хотя бы одну таску
        toDoHelper.createTask();

        //Получаем список тасок
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);
        int responseCode = response.getStatusLine().getStatusCode();

        assertThat(responseCode).isEqualTo(HttpCodes.OK);
    }

    @Test
    @DisplayName("Проверка тела ответа если список пуст")
    public void checkResponseBodyListEmpty() throws IOException {
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);

        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = EntityUtils.toString(response.getEntity());
        Task[] tasks = objectMapper.readValue(responseBody, Task[].class);

        assertThat(tasks).isEmpty();
    }

    @Test
    @DisplayName("Проверка тела ответа если список не пуст")
    public void checkResponseBodyListNotEmpty() throws IOException {

        //Создаем хотя бы одну таску
        toDoHelper.createTask();

        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);

        ObjectMapper objectMapper = new ObjectMapper();
        String stringBody = EntityUtils.toString(response.getEntity());
        Task[] tasks = objectMapper.readValue(stringBody, Task[].class);

        for (Task task : tasks) {
            assertAll("Несколько проверок",
                    () -> assertThat(task.getId()).isPositive(),
                    () -> assertThat(task.getTitle()).isNotEmpty(),
                    () -> assertThat(task.getCompleted()).containsAnyOf("true", "false"));
        }
    }

    @Test
    @DisplayName("Проверка Content-Type")
    public void checkContentType() throws IOException {

        //Создаем хотя бы одну таску
        toDoHelper.createTask();

        //Получаем список тасок
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);
        String expectedContentType = "application/json; charset=utf-8";
        String actualContentType = response.getFirstHeader("Content-Type").getValue();

        assertThat(actualContentType).isEqualTo(expectedContentType);
    }

    @AfterEach
    public void deleteAllTasks() throws IOException {

        Task[] tasks = toDoHelper.getTasks();

        for (Task task : tasks) {
            toDoHelper.deleteTask(task.getId());
        }
    }
}
