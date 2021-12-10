package practice.mailservice.utils;

import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@PropertySource("classpath:file.properties")
@Component
@Log4j2
class ClearDocsFolder implements ApplicationRunner {

    @Value("${docs.folder.path}")
    private String docsFolderPath;


    @Override
    public void run(ApplicationArguments args) throws IOException {
        FileUtils.cleanDirectory(new File(docsFolderPath));
        log.info("Removed all files from directory: {}", docsFolderPath);
    }
}
