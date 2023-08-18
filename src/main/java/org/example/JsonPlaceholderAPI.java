import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
            // Завдання 1
            JSONObject newUser = new JSONObject();
            newUser.put("id", 11);  // Приклад значення для id
            newUser.put("username", "newUser123"); // Приклад значення для username
            newUser.put("email", "newuser@example.com"); // Приклад значення для email

            // Завдання 2
            api.fetchAndSaveCommentsForLastPostOfUser(1);

            // Завдання 3
            JSONArray openTasks = api.getOpenTasksForUser(1);
            System.out.println("Open Tasks for User 1: " + openTasks);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Решта методів класу JsonPlaceholderAPI

    public void fetchAndSaveCommentsForLastPostOfUser(int userId) throws IOException {
        String userPostsUrl = BASE_URL + "/users/" + userId + "/posts";
        JSONArray userPosts = fetchJSONArray(userPostsUrl);

        if (userPosts.length() > 0) {
            JSONObject lastPost = userPosts.getJSONObject(userPosts.length() - 1);
            int postId = lastPost.getInt("id");
            String postCommentsUrl = BASE_URL + "/posts/" + postId + "/comments";
            JSONArray postComments = fetchJSONArray(postCommentsUrl);

            String fileName = "user-" + userId + "-post-" + postId + "-comments.json";
            saveJSONArrayToFile(postComments, fileName);
        } else {
            System.out.println("User has no posts.");
        }
    }


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

        return openTasks; // Повернення openTasks після циклу
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

    public JSONObject getUserByUsername(String username) throws IOException {
        String userByUsernameUrl = BASE_URL + "/users?username=" + username;
        JSONArray usersArray = fetchJSONArray(userByUsernameUrl);

        if (usersArray.length() > 0) {
            return usersArray.getJSONObject(0);
        } else {
            throw new IOException("User with username " + username + " not found.");
        }
    }
}
