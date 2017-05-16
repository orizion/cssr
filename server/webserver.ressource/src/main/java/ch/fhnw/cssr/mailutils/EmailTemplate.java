package ch.fhnw.cssr.mailutils;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.Scanner;

/**
 * A simple utility class for rendering mustache templates.
 */
public class EmailTemplate {

    private static String getFile(String fileName) {

        StringBuilder result = new StringBuilder("");

        // Get file from resources folder
        ClassLoader classLoader = EmailTemplate.class.getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();

    }

    /**
     * This renders a mustache template.
     */
    public static String getValue(String templateName, Object variables) {

        String template = getFile("mailtemplates/" + templateName + ".mustache");
        Template tmpl = Mustache.compiler().compile(template);
        return tmpl.execute(variables);
    }

    /**
     * This renders a mustache template.
     */
    public static String getSubject(String keyName, String template, Object variables) {
        Template tmpl = Mustache.compiler().compile(template);
        return tmpl.execute(variables);

    }
}
