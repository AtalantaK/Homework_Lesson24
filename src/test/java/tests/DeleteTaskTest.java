package tests;

import helpers.HttpCodes;
import helpers.MyWatchers;
import helpers.ToDoHelper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(MyWatchers.class)
public class DeleteTaskTest {

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

        //Удаляем таску
        HttpDelete httpDeleteRequest = new HttpDelete(endpoint + taskId);
        HttpResponse httpDeleteResponse = httpClient.execute(httpDeleteRequest);
        int responseCode = httpDeleteResponse.getStatusLine().getStatusCode();

        assertThat(responseCode).isEqualTo(HttpCodes.OK);
    }

    @Test
    @Tag("ContractCase")
    @DisplayName("Проверка тела ответа")
    public void checkResponseBodyListEmpty() throws IOException {

        //Создаем таску
        int taskId = toDoHelper.createTask();

        //Удаляем таску
        HttpDelete httpDeleteRequest = new HttpDelete(endpoint + taskId);
        HttpResponse httpDeleteResponse = httpClient.execute(httpDeleteRequest);
        String responseBody = EntityUtils.toString(httpDeleteResponse.getEntity());

        assertThat(responseBody).isEqualTo("\"todo was deleted\"");
    }

    @Test
    @Tag("ContractCase")
    @DisplayName("Проверка Content-Type")
    public void checkContentType() throws IOException {

        //Создаем таску
        int taskId = toDoHelper.createTask();

        //Удаляем таску
        HttpDelete httpDeleteRequest = new HttpDelete(endpoint + taskId);
        HttpResponse httpDeleteResponse = httpClient.execute(httpDeleteRequest);
        String expectedContentType = "application/json; charset=utf-8";
        String actualContentType = httpDeleteResponse.getFirstHeader("Content-Type").getValue();

        assertThat(actualContentType).isEqualTo(expectedContentType);
    }

    @Test
    @Tag("ContractCase")
    @DisplayName("Удаление таски с несуществующим taskId")
    public void deleteTaskWithNonExistentTaskId() throws IOException {

        int taskId = 1;
        HttpDelete httpDeleteRequest = new HttpDelete(endpoint + taskId);
        HttpResponse httpDeleteResponse = httpClient.execute(httpDeleteRequest);
        int responseCode = httpDeleteResponse.getStatusLine().getStatusCode();
        String responseBody = EntityUtils.toString(httpDeleteResponse.getEntity());

        assertAll("Несколько проверок",
                () -> assertThat(responseCode).isEqualTo(HttpCodes.OK),
                () -> assertThat(responseBody).isEqualTo("task by taskId not found"));
    }

    @Test
    @Tag("BusinessCase")
    @DisplayName("Удаление таски. Business Case")
    public void deleteTaskBusinessCase() throws IOException {

        int taskId = toDoHelper.createTask();
        toDoHelper.createTask();

        HttpDelete httpDeleteRequest = new HttpDelete(endpoint + taskId);
        httpClient.execute(httpDeleteRequest);

        Task[] tasks = toDoHelper.getTasks();

        assertThat(tasks).extracting(Task::getId).doesNotContain(taskId);
    }

    @AfterEach
    public void deleteAllTasks() throws IOException {

        Task[] tasks = toDoHelper.getTasks();

        for (Task task : tasks) {
            toDoHelper.deleteTask(task.getId());
        }
    }
}
