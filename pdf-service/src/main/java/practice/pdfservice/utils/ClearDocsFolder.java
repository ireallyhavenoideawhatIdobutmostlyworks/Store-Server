package practice.pdfservice.utils;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.File;

@PropertySource("classpath:excel.properties")
@Component
public class ClearDocsFolder implements ApplicationRunner {

    @Value("${docs.folder.path}")
    private String docsFolderPath;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        FileUtils.cleanDirectory(new File(docsFolderPath));
    }
}
