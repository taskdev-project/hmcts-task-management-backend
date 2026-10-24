package uk.gov.hmcts.reform.dev.config;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenApiCustomizer taskResponseSchemas() {
        return openApi -> {
            if (openApi.getPaths() == null) {
                return;
            }

            Schema<?> taskSchema = new Schema<>()
                .type("object")
                .addProperty("id", new IntegerSchema())
                .addProperty("title", new Schema<String>().type("string"))
                .addProperty("description", new Schema<String>().type("string"))
                .addProperty(
                    "status",
                    new Schema<String>().type("string")
                )
                .addProperty("dueDate", new DateTimeSchema());

            Schema<?> patchErrorSchema = new Schema<>()
                .type("object")
                .addProperty("status", new IntegerSchema())
                .addProperty("error", new Schema<String>().type("string"))
                .addProperty("message", new Schema<String>().type("string"));

            Schema<?> postErrorSchema = new Schema<>()
                .type("object")
                .addProperty("status", new IntegerSchema())
                .addProperty("error", new Schema<String>().type("string"))
                .addProperty(
                    "messages",
                    new Schema<>()
                        .type("object")
                        .additionalProperties(new Schema<String>().type("string"))
                );

            var patchPath = openApi.getPaths()
                .get("/api/tasks/{id}/status");

            if (patchPath != null && patchPath.getPatch() != null) {
                Operation operation = patchPath.getPatch();

                if (operation.getResponses() != null) {
                    var success = operation.getResponses().get("200");
                    if (success != null) {
                        success.setContent(new Content().addMediaType(
                            "application/json",
                            new MediaType().schema(taskSchema)
                        ));
                    }

                    var badRequest = operation.getResponses().get("400");
                    if (badRequest != null) {
                        badRequest.setContent(new Content().addMediaType(
                            "application/json",
                            new MediaType().schema(patchErrorSchema)
                        ));
                    }

                    var notFound = operation.getResponses().get("404");
                    if (notFound != null) {
                        notFound.setContent(null);
                    }
                }
            }

            var getTaskPath = openApi.getPaths().get("/api/tasks/{id}");

            if (getTaskPath != null && getTaskPath.getGet() != null) {
                var notFound = getTaskPath.getGet().getResponses().get("404");
                if (notFound != null) {
                    notFound.setContent(null);
                }
            }

            var postPath = openApi.getPaths().get("/api/tasks");

            if (postPath != null && postPath.getPost() != null) {
                Operation operation = postPath.getPost();

                if (operation.getResponses() != null) {
                    var created = operation.getResponses().get("201");
                    if (created != null) {
                        created.setContent(new Content().addMediaType(
                            "application/json",
                            new MediaType().schema(taskSchema)
                        ));
                    }

                    var badRequest = operation.getResponses().get("400");
                    if (badRequest != null) {
                        badRequest.setContent(new Content().addMediaType(
                            "application/json",
                            new MediaType().schema(postErrorSchema)
                        ));
                    }
                }
            }
        };
    }
}
