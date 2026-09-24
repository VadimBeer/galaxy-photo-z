package com.vadimbeer.backendservice;

import com.vadimbeer.backendservice.config.UploadProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Проверяет, что свойства app.upload.* реально читаются в UploadProperties.
 * Обычные юнит-тесты StorageService мокают UploadProperties и такие ошибки не видят
 * (например, allowed-extensions в YAML вместо allowed-file-types).
 */
class UploadPropertiesBindingTest {

    @EnableConfigurationProperties(UploadProperties.class)
    static class Config {
    }

    private final ApplicationContextRunner runner =
            new ApplicationContextRunner().withUserConfiguration(Config.class);

    @Test
    @DisplayName("Свойства из явно заданных значений корректно биндятся")
    void bindsAllProperties() {
        runner.withPropertyValues(
                "app.upload.max-file-size=10485760",
                "app.upload.storage-path=/var/data/",
                "app.upload.bucket-name=galaxy-photos",
                "app.upload.allowed-file-types[0]=image/jpeg",
                "app.upload.allowed-file-types[1]=image/png",
                "app.upload.allowed-file-types[2]=image/fits"
        ).run(ctx -> {
            assertThat(ctx).hasNotFailed();
            UploadProperties props = ctx.getBean(UploadProperties.class);

            assertThat(props.getMaxFileSize()).isEqualTo(10_485_760L);
            assertThat(props.getStoragePath()).isEqualTo("/var/data/");
            assertThat(props.getBucketName()).isEqualTo("galaxy-photos");
            assertThat(props.getAllowedFileTypes())
                    .containsExactlyInAnyOrder("image/jpeg", "image/png", "image/fits");
        });
    }

    @Test
    @DisplayName("Список через запятую тоже биндится в Set")
    void bindsCommaSeparatedList() {
        runner.withPropertyValues(
                "app.upload.bucket-name=galaxy-photos",
                "app.upload.allowed-file-types=image/jpeg,image/png"
        ).run(ctx -> {
            assertThat(ctx).hasNotFailed();
            assertThat(ctx.getBean(UploadProperties.class).getAllowedFileTypes())
                    .containsExactlyInAnyOrder("image/jpeg", "image/png");
        });
    }

    @Test
    @DisplayName("Старое имя ключа allowed-extensions: приложение не стартует (@NotEmpty)")
    void wrongKeyName_failsValidation() {
        runner.withPropertyValues(
                "app.upload.bucket-name=galaxy-photos",
                "app.upload.allowed-extensions[0]=png"
        ).run(ctx -> {
            assertThat(ctx).hasFailed();
            assertThat(ctx.getStartupFailure()).hasStackTraceContaining("allowedFileTypes");
        });
    }

    @Test
    @DisplayName("Список типов не задан вовсе: приложение не стартует")
    void missingAllowedTypes_failsValidation() {
        runner.withPropertyValues("app.upload.bucket-name=galaxy-photos")
                .run(ctx -> {
                    assertThat(ctx).hasFailed();
                    assertThat(ctx.getStartupFailure()).hasStackTraceContaining("allowedFileTypes");
                });
    }

    @Test
    @DisplayName("Реальный application.yml из проекта содержит ожидаемые значения")
    void realApplicationYml_isBoundCorrectly() {
        runner.withInitializer(new ConfigDataApplicationContextInitializer())
                .run(ctx -> {
                    assertThat(ctx).hasNotFailed();
                    UploadProperties props = ctx.getBean(UploadProperties.class);

                    assertThat(props.getBucketName()).isEqualTo("galaxy-photos");
                    assertThat(props.getAllowedFileTypes())
                            .containsExactlyInAnyOrder("image/jpeg", "image/png", "image/fits");
                });
    }
}