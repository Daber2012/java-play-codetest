package com.sprreact.demo.controller;

import com.sprreact.demo.converter.FileToJSONConverter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.io.File;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SortingRouter {

    @Value("#{systemProperties['user.dir']}")
    private String dir;

    private final @NonNull FileToJSONConverter fileToJsonConverter;

    @Bean
    @SuppressWarnings("unchecked")
    RouterFunction<ServerResponse> multipartRouter() {
        return RouterFunctions.route(RequestPredicates.POST("/"), request ->
                request.body(BodyExtractors.toMultipartData())
                        .filter(multiValueMap -> !multiValueMap.toSingleValueMap().isEmpty())
                        .flatMap(parts -> {
                                    Map<String, Part> map = parts.toSingleValueMap();
                                    FilePart filePart = (FilePart) map.get("file");
                                    File file = new File(dir + "/" + filePart.filename()
                                            + Long.valueOf(System.currentTimeMillis()).hashCode());
                                    filePart.transferTo(file);
                                    String result = fileToJsonConverter.fileToCustomersSortedJSONArray(file);
                                    file.delete();
                                    return ServerResponse.ok()
                                            .contentType(APPLICATION_JSON)
                                            .body(fromObject(result));
                                }
                        )).and(RouterFunctions.resources("/**", new ClassPathResource("template/")));
    }
}
