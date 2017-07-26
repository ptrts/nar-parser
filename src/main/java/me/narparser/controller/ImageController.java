package me.narparser.controller;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
public class ImageController {

    @RequestMapping("resources/image/{variant}/{fileName}")
    public void image(@PathVariable("variant") String variantId, @PathVariable("fileName") String fileName, HttpServletResponse response) {

        String folder = "E:/nar-photos";

        fileName = fileName.replaceAll("-(\\w+)$", ".$1");

        File file = new File(folder + "/" + variantId + "/" + fileName);

        try (FileInputStream in = new FileInputStream(file)) {

            response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
            ServletOutputStream out = response.getOutputStream();

            IOUtils.copy(in, out);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
