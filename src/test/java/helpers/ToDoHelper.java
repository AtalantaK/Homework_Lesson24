package helpers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import tests.Task;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

public class ToDoHelper {
    private final static String URL = "https://todo-app-sky.herokuapp.com/";
    private final HttpClient httpClient;
    private final ObjectMapper mapper = new ObjectMapper();
    private final static Path FILEPATH = Path.of("NewTask.json");

    public ToDoHelper() {
        this.httpClient = HttpClientBuilder.create().build();
    }

    public int createTask() throws IOException {
        HttpPost request = new HttpPost(URL);
        String myContent = """
                {
                    "title": "DELETEJAVA",
                    "completed": false
                }""";
        StringEntity stringEntity = new StringEntity(myContent, ContentType.APPLICATION_JSON);
        request.setEntity(stringEntity);
        HttpResponse response = httpClient.execute(request);

        ObjectMapper objectMapper = new ObjectMapper();
        String stringBody = EntityUtils.toString(response.getEntity());
        Task task = objectMapper.readValue(stringBody, Task.class);
        return task.getId();
    }
}
