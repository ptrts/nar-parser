package me.narparser.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ImageController {

    @Value("${application.images.directory}")
    private String folder;

    @RequestMapping("image/{variant}/{fileName}")
    public void image(@PathVariable("variant") String variantId, @PathVariable("fileName") String fileName,
                      HttpServletResponse response) {

        fileName = fileName.replaceAll("-(\\w+)$", ".$1");

        Path path = Paths.get(folder, variantId, fileName);
        
        File file = path.toFile();

        try (FileInputStream in = new FileInputStream(file)) {

            response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
            ServletOutputStream out = response.getOutputStream();

            IOUtils.copy(in, out);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
