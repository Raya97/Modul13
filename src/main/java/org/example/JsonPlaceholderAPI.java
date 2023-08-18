import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;

public class JsonPlaceholderAPI {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    public static void main(String[] args) {
        JsonPlaceholderAPI api = new JsonPlaceholderAPI();

        try {

            // Завдання 1: Створити нового користувача та отримати його ID
            int newUserId = api.createNewUser("John Doe", "johndoe@example.com");

            // Завдання 2: Отримати та зберегти коментарі для останнього поста користувача
            api.fetchAndSaveCommentsForLastPostOfUser(1);

            // Завдання 3: Отримати та вивести список відкритих завдань для користувача
            JSONArray openTasks = api.getOpenTasksForUser(1);
            System.out.println("Open Tasks for User 1: " + openTasks);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для завдання 1: Створити нового користувача та отримати його ID
    public int createNewUser(String name, String email) throws IOException {
        JSONObject newUser = new JSONObject();
        newUser.put("name", name);
        newUser.put("email", email);

        HttpPost httpPost = new HttpPost(BASE_URL + "/users");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(newUser.toString(), ContentType.APPLICATION_JSON));

        HttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = httpClient.execute(httpPost);

        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        JSONObject createdUser = new JSONObject(responseBody);
        return createdUser.getInt("id");
    }

    // Метод для завдання 2: Отримати та зберегти коментарі для останнього поста користувача
    public void fetchAndSaveCommentsForLastPostOfUser(int userId) throws IOException {
        JSONArray userPosts = fetchJSONArray(BASE_URL + "/users/" + userId + "/posts");
        if (userPosts.length() > 0) {
            JSONObject lastPost = userPosts.getJSONObject(userPosts.length() - 1);
            int postId = lastPost.getInt("id");
            JSONArray comments = fetchJSONArray(BASE_URL + "/posts/" + postId + "/comments");
            saveJSONArrayToFile(comments, "user-" + userId + "-post-" + postId + "-comments.json");
        }
    }

    // Метод для завдання 3: Отримати список відкритих завдань для користувача
    public JSONArray getOpenTasksForUser(int userId) throws IOException {
        String userTodosUrl = BASE_URL + "/users/" + userId + "/todos";
        JSONArray todos = fetchJSONArray(userTodosUrl);

        JSONArray openTasks = new JSONArray();
        for (int i = 0; i < todos.length(); i++) {
            JSONObject task = todos.getJSONObject(i);
            if (!task.getBoolean("completed")) {
                openTasks.put(task);
            }
        }

        return openTasks;
    }

    // Допоміжний метод для відправки GET-запиту і отримання JSON-масиву
    private JSONArray fetchJSONArray(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        HttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = httpClient.execute(httpGet);

        String responseBody = EntityUtils.toString(response.getEntity());
        return new JSONArray(responseBody);
    }

    // Допоміжний метод для збереження JSON-масиву у файл
    private void saveJSONArrayToFile(JSONArray jsonArray, String fileName) throws IOException {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(jsonArray.toString());
            fileWriter.flush();
        }
        System.out.println("Comments saved to " + fileName);
    }
}
