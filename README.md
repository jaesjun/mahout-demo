Mahout Demo
==========

Welcom to mahout demo.

![Preview](https://docs.google.com/viewer?pid=explorer&srcid=0B30jOdovR1_MUTF6cEJWTWNVaU0&docid=f3426880473807155edef6fbc9dea340|4243b4b22ac6766dcd2a5c5f9b97c6c5&a=bi&pagenumber=1&w=800)<br>
![Preview](https://docs.google.com/viewer?pid=explorer&srcid=0B30jOdovR1_MZlFMdnNGRGU0VUE&docid=2ac555c07c69cf2b65d3f15d8d1f9192%7C9dcf8c6b550d8a2e5b2965e86071f35f&a=bi&pagenumber=1&w=800)

This project is developed to show how mahout works.
By default, it uses GroupLens user, movie and preference data(http://grouplens.org/datasets/movielens/).

Any other user, item and preference data can be loaded as long as each file follows CSV format.
The first line of data file is column header which can be displayed as a header in user, item table.


Build with maven
==========
mvn clean install

Execution
==========
java -jar mahout-demo-0.0.1-SNAPSHOT-jar-with-dependencies.jar
