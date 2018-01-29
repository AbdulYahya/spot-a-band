
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class App {
    public static void main(String[] args) {
        staticFileLocation("/public");

        // Root - Index
        get("/", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            /*
            *   Below auth/user vars are used for testing header-nav only
            * */
            boolean authenticated = true;
            String user = "Abdul";

            if (authenticated) {
                model.put("authenticated", authenticated);
                model.put("user", user);
            }

            return new HandlebarsTemplateEngine().render(new ModelAndView(model, "index.hbs"));
        });

        get("/signin", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            return new HandlebarsTemplateEngine().render(new ModelAndView(model, "signin.hbs"));
        });

        get("/profile/:user", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            String user = request.params("user");
            boolean authenticated = true;
            model.put("authenticated", authenticated);
            model.put("user", user);
            return new HandlebarsTemplateEngine().render(new ModelAndView(model, "profile.hbs"));
        });

        // FILTERS
        before((request, response) -> {
            Map<String, Object> model = new HashMap<>();
            boolean authenticated = true;
            /*
            *  Logic to check if user is Authenticated
            * */
            if (!authenticated) {
                halt(401, new HandlebarsTemplateEngine().render(new ModelAndView(model, "signin.hbs")));
            }
        });
    }
}
