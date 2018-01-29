
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
            return new HandlebarsTemplateEngine().render(new ModelAndView(model, "index.hbs"));
        });

        // FILTERS
        before((request, response) -> {
            Map<String, Object> model = new HashMap<>();
           boolean authenticated = false;

            /*
            *  Check if user is Authenticated
            * */

            if (!authenticated) {
                halt(401, new HandlebarsTemplateEngine().render(new ModelAndView(model, "signin.hbs")));
            }
        });
    }
}
