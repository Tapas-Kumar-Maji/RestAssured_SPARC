package test_components;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.jayway.jsonpath.JsonPath;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
 
@Epic("API Testing")
@Feature("Example Tests")
public class ExampleTest extends BaseTest {
	// Delete this comment
    private WireMockServer wireMockServer;
    
    @Mock
    private SomeService someService;
    
    @BeforeClass
    public void setup() {
        MockitoAnnotations.openMocks(this);
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8080);
    }
    
    @Override
	@AfterClass
    public void tearDown() {
        wireMockServer.stop();
    }
    
    @Test(enabled = true)
    @Description("Test demonstrating Mockito and AssertJ")
    @Severity(SeverityLevel.CRITICAL)
    public void testMockingAndAssertions() {
        // Mock setup
        when(someService.getData()).thenReturn("test data");
        
        // Test execution
        String result = someService.getData();
        
        // AssertJ assertions
        assertThat(result)
            .isNotNull()
            .isEqualTo("test data")
            .hasSize(9);
            
        // Verify mock interaction
        verify(someService).getData();
    }
    
    @Test(enabled = true)
    @Description("Test demonstrating WireMock for API mocking")
    public void testApiMocking() {
        // Setup mock response
        stubFor(get(urlEqualTo("/api/test"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"message\": \"Hello World\"}")));
                
        // Make the actual API call
        given()
            .when()
            .get("/api/test")
            .then()
            .statusCode(200)
            .contentType("application/json")
            .body("message", equalTo("Hello World"));
        
        // Verify mock was called
        verify(getRequestedFor(urlEqualTo("/api/test")));
    }
    
    @Test(enabled = true)
    @Description("Test demonstrating Awaitility for async testing")
    public void testAsyncOperation() {
        // Setup async operation
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "async result";
        });
        
        // Wait for result with Awaitility
        await().atMost(2, TimeUnit.SECONDS)
               .until(() -> future.isDone());
               
        assertThat(future.join()).isEqualTo("async result");
    }
    
    @Test(enabled = true)
    @Description("Test demonstrating JsonPath for JSON parsing")
    public void testJsonPath() {
        String json = "{\"store\":{\"book\":[{\"title\":\"Book1\",\"price\":10.99},{\"title\":\"Book2\",\"price\":20.99}]}}";
        
        // Extract data using JsonPath
        List<String> titles = JsonPath.read(json, "$.store.book[*].title");
        List<Double> prices = JsonPath.read(json, "$.store.book[*].price");
        
        // AssertJ assertions
        assertThat(titles)
            .hasSize(2)
            .containsExactly("Book1", "Book2");
            
        assertThat(prices)
            .hasSize(2)
            .containsExactly(10.99, 20.99);
    }
}

// Example service interface for mocking
interface SomeService {
    String getData();
} 