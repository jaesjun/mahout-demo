Welcom to mahout demo.

This project is developed to show how mahout works.
By default, it uses GroupLens user, movie and preference data(http://grouplens.org/datasets/movielens/).

Any other user, item and preference data can be loaded as long as each file follows CSV format.
The first line of data file is column header which can be displayed as a header in user, item table.


1. Build with maven
mvn clean install

2. Execute.
java -jar mahout-demo-0.0.1-SNAPSHOT-jar-with-dependencies.jar
