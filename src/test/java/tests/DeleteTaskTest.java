package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import helpers.HttpCodes;
import helpers.ToDoHelper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class DeleteTaskTest {

    private static final String endpoint = "https://todo-app-sky.herokuapp.com/";
    private HttpClient httpClient;
    private ToDoHelper toDoHelper;

    @BeforeEach
    public void createHttpClient() {
        httpClient = HttpClientBuilder.create().build();
        toDoHelper = new ToDoHelper();
    }

    @Test
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
}

//todo: добавить бизнес кейс

//todo: добавить e2e: add task, edit task, complete task, delete task
