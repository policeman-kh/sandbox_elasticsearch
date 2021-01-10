package sandbox.es.index;

import java.util.Map;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import sandbox.es.search.ActionListenerAdaptor;

@Slf4j
@AllArgsConstructor
@RestController
public class IndexController {
    private final RestHighLevelClient client;

    @GetMapping("index")
    public Mono<IndexResponse> index() {
        return Mono.defer(() -> ActionListenerAdaptor.<IndexResponse>execute(
                listener -> client.indexAsync(indexRequest(), RequestOptions.DEFAULT, listener)))
                   .doOnSuccess(res -> log.info("Created index. res={}", res));
    }

    private static IndexRequest indexRequest() {
        final IndexRequest request = new IndexRequest("test");
        request.source(Map.of("field", "field", "value", "value"));
        return request;
    }
}
