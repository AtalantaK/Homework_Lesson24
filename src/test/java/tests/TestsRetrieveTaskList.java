package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

//todo: 2. создание новой задачи
//todo: 3. переименование задачи
//todo: 4. отметка задачи выполненной
//todo: 5. удаление задачи

@ExtendWith(MyWatchers.class)
public class TestsRetrieveTaskList {
    /* https://sky-todo-list.herokuapp.com */
    private final static String endpoint = "https://todo-app-sky.herokuapp.com/";

    private HttpClient httpClient;

    @BeforeEach
    public void setUp() {
        httpClient = HttpClientBuilder.create().build();
    }

    @Test
    @DisplayName("Проверка статус кода")
    public void sendGetTestCheckStatusCode() throws IOException {
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    @DisplayName("Проверка тела запроса")
    public void sendGetTestCheckBody() throws IOException {
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);

        ObjectMapper objectMapper = new ObjectMapper();
        String stringBody = EntityUtils.toString(response.getEntity());
        Task[] tasks = objectMapper.readValue(stringBody, Task[].class);

        if (tasks.length >= 1) {
            for (Task task : tasks) {
                assertAll("Несколько проверок",
                        () -> assertThat(task.getId()).isPositive(),
                        () -> assertThat(task.getTitle()).isNotEmpty(),
                        () -> assertThat(task.getCompleted()).containsAnyOf("true", "false"));
            }
        } else {
            assertThat(tasks).isEmpty();
        }
    }

    @Test
    @DisplayName("Проверка Content-Type")
    public void sendGetCheckContentType() throws IOException {
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);
        assertEquals("application/json; charset=utf-8", response.getFirstHeader("Content-Type").getValue());
    }
}
