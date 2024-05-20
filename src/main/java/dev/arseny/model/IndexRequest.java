package dev.arseny.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class IndexRequest {
    private String indexName;
    private Map<String, String> fieldAnalyzers;
    private List<Map<String, Object>> documents;
}
