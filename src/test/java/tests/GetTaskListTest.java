package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import helpers.HttpCodes;
import helpers.MyWatchers;
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

//todo: 3. переименование задачи
//todo: 4. отметка задачи выполненной

// 1. Получение списка

@ExtendWith(MyWatchers.class)
public class GetTaskListTest {
    /* https://sky-todo-list.herokuapp.com */
    private final static String endpoint = "https://todo-app-sky.herokuapp.com/";

    private HttpClient httpClient;

    @BeforeEach
    public void createHttpClient() {
        httpClient = HttpClientBuilder.create().build();
    }

    @Test
    @DisplayName("Проверка статус кода")
    public void checkStatusCode() throws IOException {
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
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);
        String expectedContentType = "application/json; charset=utf-8";
        String actualContentType = response.getFirstHeader("Content-Type").getValue();

        assertThat(actualContentType).isEqualTo(expectedContentType);
    }

    //todo: добавить пререквизиты для пустого списка и не пустого списка
    //todo: добавить подчищение за собой
}
