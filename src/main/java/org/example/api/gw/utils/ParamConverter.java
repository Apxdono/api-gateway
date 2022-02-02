package org.example.api.gw.utils;

import io.javalin.http.UploadedFile;
import kotlin.Pair;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ParamConverter {

    public static void convertParams(Map<String, String> source, GenericAccumulator<String, String> acc) {
        for (Map.Entry<String, String> entry: source.entrySet()) {
            acc.processEntry(entry.getKey(), entry.getValue());
        }
    }

    public static void convertMultiValueParams(Map<String, List<String>> source, GenericAccumulator<String, String> acc) {
        for (Map.Entry<String, List<String>> entry: source.entrySet()) {
            for (String value : entry.getValue()) {
                acc.processEntry(entry.getKey(), value);
            }
        }
    }

    public static void convertMultiPartParams(Map<String, List<String>> source, GenericAccumulator<String, String> acc) {
        for (Map.Entry<String, List<String>> entry: source.entrySet()) {
            for (String value : entry.getValue()) {
                acc.processEntry(entry.getKey(), value);
            }
            if (entry.getValue().isEmpty()) {
                acc.processEntry(entry.getKey(), "");
            }
        }
    }

    public static Map<String, UploadedFile> extractFileParameters(Map<String, List<String>> multiParams,
                                                                  Function<String, UploadedFile> fileExtractor) {
        return multiParams.keySet().stream()
                .map(part -> new Pair<>(part, fileExtractor.apply(part)))
                .filter(fileCandidate -> fileCandidate.getSecond() != null)
                .collect(Collectors.toMap(Pair<String, UploadedFile>::getFirst, Pair<String, UploadedFile>::getSecond));
    }

    public static String asFileName(UploadedFile file) {
        return String.join(".", file.getFilename(), file.getExtension());
    }

    @FunctionalInterface
    public interface GenericAccumulator<K, V> {
        void processEntry(K key, V value);
    }
}
