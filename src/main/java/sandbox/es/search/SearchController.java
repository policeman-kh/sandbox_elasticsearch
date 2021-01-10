package sandbox.es.search;

import java.io.IOException;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
public class SearchController {
    private final RestHighLevelClient client;

    @GetMapping("search")
    public Mono<String> search(@RequestParam String text) throws IOException {
        return Mono.just(client.search(searchRequest(text), RequestOptions.DEFAULT))
                   .map(SearchResponse::getHits)
                   .map(hits -> hits.getHits()[0].toString());
    }

    @GetMapping("searchAsync")
    public Mono<String> searchAsync(@RequestParam String text) {
        return Mono.defer(() -> ActionListenerAdaptor.<SearchResponse>execute(
                listener -> client.searchAsync(searchRequest(text), RequestOptions.DEFAULT, listener)))
                   .map(SearchResponse::getHits)
                   .map(hits -> hits.getHits()[0].toString());
    }

    private static SearchRequest searchRequest(String text) {
        final SearchRequest request = new SearchRequest();
        request.source(SearchSourceBuilder.searchSource()
                                          //.timeout(TimeValue.timeValueNanos(1L))
                                          .query(QueryBuilders.matchQuery("field", text)));
        return request;
    }
}
