# Comparing Solitary and Sociable Tests

## Key Differences
### Test setup
- Solitary tests are very straightforward to set up. 
 Using the jUnit MockitoExtension and annotations, we can simply create the test unit.
 Dependencies will be mocked, so we don't need to instantiate them.

- Sociable tests require some more setup. 
 We want to avoid mocking the dependencies, so we will have to instantiate them.
 We don't want to start up an application context, so we will still have to mock some parts that require the application context.
 Sociable tests typically mock everything beyond our direct control, like a database, filesystem, external API's, messaging systems, etc.

### Test complexity
- Solitary tests are straightforward to write.
- Sociable tests are a little more complex due to the required setup.
- Individual tests are similar in complexity. However, it may feel a little more difficult
 to write a sociable test because you have to think about the bigger picture.
- Solitary tests only test code contained within a single class file. Solitary tests have a bigger unit,
 so the code is not contained within a single class

### Test focus
#### Solitary test
- Solitary tests are focused on a single class, all dependencies on other classes are mocked.
- This implies that we are not testing the full behavior of the flow, but only that part within the class being tested.
- By doing this, our test will automatically become more coupled to the technical implementation.

#### Sociable test
- A sociable tests focuses on the behavior of a flow.
- It might not be the entire flow (from the application entry point), we can also test partial flows
- By using the actual dependencies, we immediately verify that the components within the flow integrate correctly.

### Test readability
- In small projects, both sociable and solitary tests are very readable.
- When we have more complex flows, sociable tests tend to be more readable.
- If we have multiple dependencies in a flow, solitary tests will require some mocking, making the test harder to read.
- Sociable tests have less mocking, making it easy to see what's being tested.

### Test maintainability
- In the very beginning, every unit test is both sociable and solitary when the class has no external dependencies.
- Once we add dependencies to other classes, we have to decide to go solitary or sociable.
- Sociable tests are often more maintainable because they accept dependencies.
- Solitary tests don't accept dependencies, so they require setting up the mocks for mimicking the external behavior.
- When refactoring, we often introduce new dependencies by extracting parts of the flow to another, dedicated, class.
- While doing this, we will have to add mocks and configure them to make our solitary test pass again.
- Sociable tests only require updating the setup of the test unit (fix compilation errors). 
 Sometimes we will need to mock an additional call to an external system or infrastructure.
- This implies that a sociable test is more likely to provide us the confidence that our refactoring did not break anything.