# UnitSocializer—Quick Start

## Required dependency
```
<dependency>
    <groupId>io.github.wouter-bauweraerts</groupId>
    <artifactId>unit-socializer-junit-mockito</artifactId>
    <version>1.0.0</version> 
    <scope>test</scope>
</dependency>
```

## Test Setup
- Annotate your class with **@SociableTest**
- Annotate the entrypoint for your test with **@TestSubject**
- Inject the classes you need for your test using the **@InjectTestInstance**
- Configure your test unit

## Unit configuration

### Mock configuration
- Manually mock the dependency and annotate with @Predefined (deprecated!)
- Configure mocking in the [UnitSocializer configuration file](src/test/resources/unit-socializer-config.yaml)
  - file called unit-socializer-config.yaml in the test resources directory
  - mocking on different levels (specific classes, packages and/or annotations) [see documentation](https://wouter-bauweraerts.github.io/UnitSocializer/modules/core/mock-configuration)
  - currently no defaults are configured in UnitSocializer

### Dependencies with specific values
UnitSocializer provides the `@Predefined` annotation to configure dependencies with specific values. 
Declare and instantiate an attribute with the desired value and annotate it with `@Predefined`.

### Using a specific implementation of an interface or abstract class
- Use `@Predefined` (similar to above)
- Use `@Resolve` within the `@TestSubject` annotation (recommended)
- Use classpath scanner (fallback)
   - If a dependency has an interface or abstract class as type
   - UnitSocializer will try to find a class that implements or extends the interface or abstract class
   - If no implementations (or multiple implementations) are found, an exception will be thrown
   - Classpath scanning is **slow**, use it as a fallback only

## UnitSocializer documentation
Check the [documentation pages](https://wouter-bauweraerts.github.io/UnitSocializer/) for more details