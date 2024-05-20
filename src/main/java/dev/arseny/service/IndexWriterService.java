package dev.arseny.service;

import dev.arseny.RequestUtils;
import dev.arseny.model.IndexRequest;
import dev.arseny.utils.ReflectionUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.NoDeletionPolicy;
import org.apache.lucene.store.FSDirectory;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


@ApplicationScoped
public class IndexWriterService {
    private static final Logger LOG = Logger.getLogger(RequestUtils.class);

    public IndexWriter getIndexWriter(IndexRequest request) {

        final StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
        final String indexName = request.getIndexName();
        PerFieldAnalyzerWrapper perFieldAnalyzer = null;

        if (request.getFieldAnalyzers() != null) {
            perFieldAnalyzer = new PerFieldAnalyzerWrapper(standardAnalyzer, createPerFieldAnalyzers(request.getFieldAnalyzers()));
        }

        try {

            Analyzer analyzer = perFieldAnalyzer != null ? perFieldAnalyzer : standardAnalyzer;
            IndexWriterConfig config = new IndexWriterConfig(analyzer).setIndexDeletionPolicy(NoDeletionPolicy.INSTANCE);
            
            IndexWriter indexWriter = new IndexWriter(
                    FSDirectory.open(Paths.get(IndexConstants.LUCENE_INDEX_ROOT_DIRECTORY + indexName)),
                    config
            );

            return indexWriter;
        } catch (IOException e) {
            LOG.error("Error while trying to create an index writer for index " + indexName, e);
            throw new RuntimeException(e);
        }
    }

    private Map<String, Analyzer> createPerFieldAnalyzers(Map<String, String> configAnalyzers) {

        Map<String, Analyzer> analyzers = new HashMap<>();

        for (Map.Entry<String, String> entry : configAnalyzers.entrySet()) {
            final String fieldName = entry.getKey();

            Object analyzer = ReflectionUtils.createClassForName(entry.getValue());
            if (analyzer instanceof Analyzer) {
                analyzers.put(fieldName, (Analyzer) analyzer);
            }
        }

        return analyzers;
    }
}
