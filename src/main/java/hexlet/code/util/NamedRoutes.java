package hexlet.code.util;

public class NamedRoutes {

    public static String userGetId (Long id) {
        return "/api/users/" + id;
    }

    public static String getUsers () {
        return "/api/users";
    }

    public static String userPost () {
        return "/api/users";
    }

    public static String userUpdate (Long id) {
        return "/api/users/" + id;
    }

    public static String userDelete (Long id) {
        return "/api/users/" + id;
    }

    public static String login () {
        return "/api/login";
    }

}
