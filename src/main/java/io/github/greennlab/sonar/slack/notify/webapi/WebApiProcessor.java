package io.github.greennlab.sonar.slack.notify.webapi;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WebApiProcessor {

    public static final long CACHE_ROTTEN_MILLIS = TimeUnit.DAYS.toMillis(1);
    public static final String API_USERS = "/api/users/search?ps=500"; // ps=pageSize
    public static final String API_LOGIN = "/api/authentication/login"; // ps=pageSize
    public static final String API_ISSUE = "/api/issues/search";
    public static final String LOGIN_USERNAME = "sysop";
    public static final String LOGIN_PASSWORD = "test123$";

    /* @formatter:off */
    public static final Type TYPE_OF_USERS = new TypeToken<List<User>>(){}.getType();
    public static final Type TYPE_OF_ISSUE = new TypeToken<List<Issue>>(){}.getType();
    /* @formatter:on */

    private static final Logger log = LoggerFactory.getLogger(WebApiProcessor.class);
    private static final ConcurrentHashMap<Key, Long> cacheExpires = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Key, Object> cacheData = new ConcurrentHashMap<>();

    private final String server;
    private final String credential;
    private final OkHttpClient client;


    public WebApiProcessor(String server, String credential) {
        this.server = server;
        this.credential = credential;
        this.client = new OkHttpClient();
    }


    public Map<String, User> findAllUsers() {
        Map<String, User> cached = getCacheData(Key.USER);
        if (cached != null) return cached;

        return webApiCall(API_USERS, body -> {
            var json = JsonParser.parseReader(body.charStream()).getAsJsonObject().get("users");

            final List<User> users = new Gson().fromJson(json, TYPE_OF_USERS);
            setCacheData(Key.USER, users);

            return users.stream().collect(Collectors.toMap(User::getLogin, user -> user));
        });
    }

    public List<Issue> findAllIssues(String componentKey) {
        List<Issue> cached = getCacheData(Key.ISSUE);
        if (cached != null) return cached;

        return webApiCall(API_ISSUE + "?componentKeys=" + componentKey, body -> {
            var gson = new Gson();
            var json = JsonParser.parseReader(body.charStream()).getAsJsonObject();

            final List<Issue> issues = gson.fromJson(json.get("issues"), TYPE_OF_ISSUE);
            final Map<String, User> users = findAllUsers();

            if (issues.isEmpty()) return issues;

            var regex = "^" + componentKey + ":";
            issues.forEach(issue -> {
                if (null != users) {
                    var user = users.get(issue.getAssignee());
                    if (null != user) issue.setAssignee(user.getName());
                }

                issue.setComponent(issue.getComponent().replaceFirst(regex, ""));
            });

            setCacheData(Key.ISSUE, issues);

            return issues;
        });
    }

    private <T> T webApiCall(String api, Function<ResponseBody, T> responseFn) {
        var request = new Request.Builder()
            .get()
            .url(server + api)
            .header("authorization", "bearer " + credential)
            .header("cookie", getLoginToken())
            .build();

        try (
            var response = client.newCall(request).execute()
        ) {
            var body = response.body();
            if (!response.isSuccessful()) throw new IllegalStateException(response.code() + " " + response.message());
            if (body == null) return null;

            return responseFn.apply(body);
        } catch (IOException e) {
            log.error("", e);
        }

        throw new IllegalStateException();
    }

    private String getLoginToken() {
        var request = new Request.Builder()
            .url(server + API_LOGIN)
            .post(new FormBody.Builder().add("login", LOGIN_USERNAME).add("password", LOGIN_PASSWORD).build())
            .build();

        try (var response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IllegalStateException();

            var cookies = String.join(";", response.headers("set-cookie"));
            setCacheData(Key.LOGIN, cookies);

            return cookies;
        } catch (IOException e) {
            log.error("", e);
        }

        throw new IllegalStateException();
    }

    private <T> T getCacheData(Key key) {
        var cachedTime = cacheExpires.get(key);
        if (cachedTime == null) return null;
        if (System.currentTimeMillis() > cachedTime + CACHE_ROTTEN_MILLIS) return null;

        @SuppressWarnings("unchecked")
        var t = (T) cacheData.get(key);

        return t;
    }

    private void setCacheData(Key key, Object data) {
        cacheExpires.put(key, System.currentTimeMillis());
        cacheData.put(key, data);
    }


    private enum Key {
        USER,
        LOGIN,
        ISSUE
    }
}
