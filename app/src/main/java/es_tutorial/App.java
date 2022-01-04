package es_tutorial;

import java.io.IOException;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

public class App {
    static RestHighLevelClient client;

    public static String addIndex(String indexName, String jsonString) throws IOException {
        IndexRequest request = new IndexRequest(indexName);
        request.source(jsonString, XContentType.JSON);
        return client.index(request, RequestOptions.DEFAULT).getId();
    }

    public static SearchResponse search(String query) throws IOException {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.queryStringQuery(query));
        SearchRequest searchRequest = new SearchRequest();

        searchRequest.source(builder);
        SearchResponse response = client.search(searchRequest,
                RequestOptions.DEFAULT);
        return response;
    }

    public static void printSearchResults(String query) throws IOException {
        System.out.println("Wynik wyszukania \"" + query + "\":");
        SearchHit[] searchhits = search(query).getHits().getHits();
        for (SearchHit hit : searchhits) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    public static void main(String[] args) throws IOException {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder().connectedTo("20.126.141.116:9200")
                .build();
        client = RestClients.create(clientConfiguration).rest();

        addIndex("people",
                "{\"first_name\": \"Jan\", \"last_name\": \"Kowalski\", \"age\": 20, \"nationality\": \"Polish\"}");
        addIndex("people",
                "{\"first_name\": \"John\", \"last_name\": \"Smith\", \"age\": 30, \"nationality\": \"American\"}");

        printSearchResults("Kowalski");
        printSearchResults("American");
        printSearchResults("20");

        client.close();
    }
}
