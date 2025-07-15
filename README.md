# SPARC API Test Project

This project contains automated tests for the SPARC API using TestNG, RestAssured, and other testing frameworks.

## Prerequisites

- Java 21 or higher
- Maven 3.8 or higher
- Git

## Project Structure

```
├── src/
│   ├── main/java/          # Source code
│   └── test/java/          # Test code
│       └── test_components/
│           ├── authentication_service/
│           ├── BaseTest.java
│           └── ExampleTest.java
├── test-suites/            # TestNG suite configurations
│   ├── DummyApiTestng.xml
│   ├── ExampleTest.xml
│   └── GoogleMapsApiTesting.xml
├── pom.xml                 # Maven configuration
└── README.md              # This file
```

## Running Tests

### Running All Tests
```bash
mvn clean test
```

### Running Specific Test Suite
```bash
# Run Dummy API tests
mvn clean test -Dsurefire.suiteXmlFiles=test-suites/DummyApiTestng.xml
# Run Google Maps API tests
mvn clean test -Dsurefire.suiteXmlFiles=test-suites/GoogleMapsApiTesting.xml
# Run Example Test
mvn clean test -Dsurefire.suiteXmlFiles=test-suites/ExampleTest.xml
# Run Dummy API tests
mvn clean test -Dsurefire.parallel=none -Dsurefire.threadCount=5
```

### Generating Reports
```bash
# Generate Allure report
mvn allure:report

# Serve Allure report locally
# // Delete this comment
mvn allure:serve
```

## Test Frameworks and Tools

- **TestNG**: Test framework
- **RestAssured**: API testing
- **ExtentReports**: HTML Report 
- **Slack**: Slack framework 
- **Mockito**: Mocking framework
- **AssertJ**: Fluent assertions
- **WireMock**: HTTP service mocking
- **Awaitility**: Async testing
- **JsonPath**: JSON parsing
- **Allure**: Test reporting

## Test Categories

1. **Authentication Service Tests**
   - Login functionality
   - Token validation
   - Session management

2. **Dummy API Tests**
   - Basic API endpoints
   - Response validation
   - Error handling

3. **Google Maps API Tests**
   - Location services
   - Geocoding
   - Distance calculations

## Best Practices

1. **Test Organization**
   - Use descriptive test names
   - Group related tests in test suites
   - Follow the Page Object Model for UI tests

2. **Code Quality**
   - Write clean, maintainable code
   - Use proper logging
   - Follow SOLID principles

3. **Testing Standards**
   - Each test should be independent
   - Use proper setup and teardown
   - Include meaningful assertions

## Troubleshooting

### Common Issues

1. **Java Version Mismatch**
   - Ensure Java 21 is installed
   - Update JAVA_HOME environment variable

2. **Test Failures**
   - Check test logs in `target/surefire-reports`
   - Verify API endpoints are accessible
   - Check test data validity

3. **Report Generation Issues**
   - Clear `target` directory
   - Run `mvn clean` before generating reports

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 