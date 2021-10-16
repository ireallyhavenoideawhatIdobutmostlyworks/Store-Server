package practice.pdfservice.utils;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@PropertySource("classpath:excel.properties")
@Component
public class ClearDocsFolder {

    @Value("${docs.folder.path}")
    private String docsFolderPath;


    @Scheduled(fixedRate = 30000)
    public void cleanDirectory() throws IOException {
        FileUtils.cleanDirectory(new File(docsFolderPath));
    }
}
