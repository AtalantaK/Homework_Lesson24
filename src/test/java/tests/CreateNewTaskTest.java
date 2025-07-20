package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import helpers.HttpCodes;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

// 2. создание новой задачи

public class CreateNewTaskTest {
    private static final String FILEPATH = "NewTask.json";

    private static final String endpoint = "https://todo-app-sky.herokuapp.com/";

    private HttpClient httpClient;

    @BeforeEach
    public void createHttpClient() {
        httpClient = HttpClientBuilder.create().build();
    }

    @Test
    @DisplayName("Проверка статус кода")
    public void checkStatusCode() throws IOException {
        HttpPost request = new HttpPost(endpoint);
        String requestBody = Files.readString(Path.of(FILEPATH));
        StringEntity stringEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        request.setEntity(stringEntity);
        HttpResponse response = httpClient.execute(request);
        int responseCode = response.getStatusLine().getStatusCode();

        assertThat(responseCode).isEqualTo(HttpCodes.OK);
    }

    @Test
    @DisplayName("Проверка тела ответа")
    public void checkResponseBody() throws IOException {
        HttpPost request = new HttpPost(endpoint);
        String requestBody = Files.readString(Path.of(FILEPATH));
        StringEntity stringEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        request.setEntity(stringEntity);
        HttpResponse response = httpClient.execute(request);
        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = EntityUtils.toString(response.getEntity());
        Task task = objectMapper.readValue(responseBody, Task.class);

        assertAll("Несколько проверок",
                () -> assertThat(task.getId()).isPositive(),
                () -> assertThat(task.getTitle()).isEqualTo("new task"),
                () -> assertThat(task.getCompleted()).isEqualTo("false"));
    }

    @Test
    @DisplayName("Проверка Content-Type")
    public void checkContentType() throws IOException {
        HttpPost request = new HttpPost(endpoint);
        String requestBody = Files.readString(Path.of(FILEPATH));
        StringEntity stringEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        request.setEntity(stringEntity);
        HttpResponse response = httpClient.execute(request);
        String expectedContentType = "application/json; charset=utf-8";
        String actualContentType = response.getFirstHeader("Content-Type").getValue();

        assertThat(actualContentType).isEqualTo(expectedContentType);
    }

    //todo: добавить удаление таски за собой (подчищение)
    //todo: добавить бизнес кейс
    //todo: добавить добавление таски с completed true
}
