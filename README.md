# PoC Preservation Service using XML ERS 

This repository holds a proof-of-concept preservation service using the evidence records syntax from RFC6283.
The service follows specifications from ETSI 119 512 annexe F.2. It only preserves so-called *digest lists*.
The service does not perform augmentation of provided evidences and does not validate XML ERS evidences.
However, timestamp renewals are performed internally.

The service uses a PostgreSQL DB whose schema is maintained using liquibase. Other relational DBMS could be used with minor modifications (see ```application.properties``` in the ``server`` module).
To store trees the model used is Adjacency List. Meaning that a node holds a foreign key to its parent.

## Structure

*Note: append ``lt-pres-ers-`` before each name of the list below.*

* **core:** This module contains everything that is persistence-related, e.g. the JPA entities, the liquibase changelog, repositories. A scheduled job performs the tree building (including timestamp renewals), it is located in the ``scheduler`` package.
* **model:** This module contains intermediary representations to transfert between the *core* and *server* modules.
* **server:** Contains the API delegates for the REST endpoints. It also holds the ``.properties`` file for the service's configuration.
* **utils:** This module contains utility function such as a comparator for sorting in binary ascending order. It also has a useful ``PreservePO`` request body generator, which can be easily used by hand using the test cases.
* **test:** This module is not used, most of the tests are present in their respective modules. When integration testing is done the ``TestContainers`` library is used to run a temporary PostgreSQL instance in a Docker container. 

## Installation

Java 17 or above and Maven 3.8.6 (use the wrapper) are required to build and execute the project. For the DBMS we recommend PostgreSQL 15 or above, the database's URL, port and login info should be adapted in the ```application.properties```. 
The entrypoint is the main function in the ``server`` module.
A self-contained JAR can be built using the maven target ``package``, do not forget to disable tests with `-DskipTests=true`. The jar will then be located in the server's `target` directory.
