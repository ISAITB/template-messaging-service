# Introduction

The **template-messaging-service** is a [Maven Archetype](https://maven.apache.org/guides/introduction/introduction-to-archetypes.html) 
acting as a supporting resource for the GITB test bed software and specifications. It can be used as template from which
to build a fully working GITB-compliant messaging service to be used through the GITB test bed for sending and receiving
messages in an arbitrary manner.

The GITB specifications are the result of the
[CEN Global eBusiness Interoperability Test bed (GITB) Workshop Agreement](http://www.cen.eu/work/areas/ict/ebusiness/pages/ws-gitb.aspx).
Evolutive maintenance of the GITB specifications and software is now performed by the European Commission's DIGIT [ISA Unit](http://ec.europa.eu/isa/isa2)
and specifically [ISA Action 2016.25](https://ec.europa.eu/isa2/actions/platform-test-it-systems-services-and-products_en).
For more information please check the [Interoperability Test Bed's site](https://joinup.ec.europa.eu/solution/interoperability-test-bed/about)
on Joinup. 

# Build instructions

This Archetype is developed in Java and is built using Maven 3+, To build issue the following:

```
mvn clean install
```  

To also sign the resulting artefacts use the `sign-artifacts` profile as follows (you can optionally specify the 
key to use for signing through the system property `gpg.keyname`).

```
mvn clean install -P sign-artifacts
``` 


Using the Archetype to generate a new project is through [Maven's Archetype Plugin](https://maven.apache.org/archetype/maven-archetype-plugin/index.html).
To use it issue (replacing `VERSION` with the version you want to use):

```
mvn archetype:generate -DarchetypeGroupId=eu.europa.ec.itb -DarchetypeArtifactId=template-messaging-service -DarchetypeVersion=VERSION
```
