# sociable-pet-adoption
Sample application illustrating the difference between solitary and sociable unit tests

## Description
Simple Spring-Boot REST API. Idea inspired by the spring pet clinic sample application.
Built using Spring-Boot and Spring Modulith. Application divided into 5 modules
- common: contains some common code
- pets: contains CRUD operations on pets
- owners: contains CRUD operations on owners
- adoptions: contains logic to allow pets to be adopted using the system
- veterinary: contains logic to calculate the price of a veterinary checkup after adoption

## Branches
- main

### main
- Branch contains all code, some code may be commented out
- All tests are present, branch gives a full overview of the differences between testing types

## Test Data
Some of the test data is generated using [Instancio Fixture Builder](https://wouter-bauweraerts.github.io/instancio-fixture-builder/)
which is a layer on top of [Instancio](https://www.instancio.org/), a great tool to generate test data.