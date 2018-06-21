package util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ReadSPL {

    public static String readLineByLineJava8(String filePath)
    {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8))
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e)
        {
            if(filePath.contains("lists.py")){
                return readLineByLineJava8("lists.py");
            } else if(filePath.contains("tuples.py")){
                return readLineByLineJava8("tuples.py");
            }
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }
}
