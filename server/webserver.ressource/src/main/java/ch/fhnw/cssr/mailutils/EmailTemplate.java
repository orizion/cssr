package ch.fhnw.cssr.mailutils;

import com.github.mustachejava.DefaultMustacheFactory;

import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.IOException;
import java.io.StringWriter;

/**
 * A simple utility class for rendering mustache templates.
 */
public class EmailTemplate {

    /**
     * This renders a mustache template.
     */
    public static String getValue(String templateName, Object variables) {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("mailtemplates/" + templateName + ".moustache");
        StringWriter writer = new StringWriter();
        try {
            mustache.execute(writer, variables).flush();
        } catch (IOException e) { 
            // We really should not get an IO Exception
            // for writing to a string
            throw new RuntimeException(e);
        }
        return writer.toString();
    }
}
