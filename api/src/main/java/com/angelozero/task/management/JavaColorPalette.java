package com.angelozero.task.management;

// package ---> Keywords

// import ---> Keywords

import com.angelozero.task.management.entity.Pokemon;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service; // Other Class Names
import java.util.List;
import java.util.Map;

// @Service ---> Attributes
@Service
public class JavaColorPalette {

    // private ---> Keywords
    // String ---> Type Declarations
    private String name; // Project Properties and Globals

    // public ---> Keywords
    // execute ---> Project Function and Method Names
    public Pokemon execute(String name) { // Type Declarations
        // if ---> Keywords
        if (StringUtils.isBlank(name)) { // Other Function and Method Names
            // return ---> Keywords
            return null;
        }

        // new ---> Keywords
        var pokemon = new Pokemon(100, "Pikachu", "sasas"); // Numbers, Strings, Type Declarations

        // return ---> Keywords
        return pokemon;
    }

    // private ---> Keywords
    // final ---> Keywords
    private final String API_KEY = "apiKey"; // Project Constants

    // public ---> Keywords
    // SomeOtherClass ---> Other Class Names
    public static class SomeOtherClass {
        // Class content
    }
}