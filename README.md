# Comparing Imperative and Declarative Process Models

> Since the advent of so-called declarative process modeling languages in addition to the previously known imperative languages, the business process management community has been confronted with a lot of new possibilities and challenges regarding modeling  and interpreting business processes. In general, these declarative languages are better suited for flexible processes, i.e. processes which lead a lot of open decisions to the model executor and hence have got a relatively large amount of execution paths, whereas imperative languages are mainly used to formalize routine processes. Of course, the question arises whether a declarative or an imperative approach fits better for a specific application. In this paper we handle this issue and present a method based on automata theory, which supports the process modeler in making this decision. Furthermore, we present a comparison method which makes it possible to check given imperative and declarative process models for equality. We finally evaluate our approach by implementing a Java tool and calculating practical examples.

*Excerpt from the paper "Comparing Imperative and Declarative Process Models" (currently under review)*

## Usage

### Installation instructions

- Download and install Java 17
- Download and install Maven
- Either download the pre-configured JAR file (link will be added soon) or follow the steps below:
    1. Download the sources or clone this repository
    2. In folder with pom.xml run
        ```
        mvn install
        ```

### Usage of command-line interface
- Help:
    ```
    java -jar <generated jar file> --help
    ```
- List supported declare constraints:
    ```
    java -jar <generated jar file> --help
    ```
- **Run model comparison for two models:** 
    ```
    java -jar <generated jar file> [--dfa-output <PATH>] [--max-word-length <INTEGER>] <model file path 1> <model file path 2>
    ```
    Example:
    ```
    java -jar comparedeclaremodels-0.1.jar --max-word-length 2 --dfa-output .\tmp "examples\models\test1.txt" "examples\models\test4.txt"
    ```
    A **model file** needs to have the following structure:

    Line 1:
    ```
    Symbol, symbol, symbol, ...;
    ```
    Line 2 - end:
    ```
    <constraintTemplateName>(symbol1[, symbol2]);
    ```
    The model files need to contain at least one constraint each. Symbols are used to encode activity names. Only symbols defined in line 1 of the model files can be used in constraints. Example of a valid model file:
    ```
    a,b,c,d,e,f;
    init(a);
    init(b);
    precedence(a,b);
    ```

## Citation
If you use this tool in your paper, please cite the following paper:
```
@article{schuetzenmeier-2023-cdm,
    title = "Comparing Imperative and Declarative Process Models",
    author = "Sch\"{u}tzenmeier, Nicolai and Jablonski, Stefan and
        K\"{a}ppel, Martin and 
        Ackermann, Lars",
    year = "Currently under review"
}
```

## Contact
- [nicolai.schuetzenmeier@uni-bayreuth.de](mailto:Nicolai.Schuetzenmeier@uni-bayreuth.de)
- [martin.kaeppel@uni-bayreuth.de](mailto:martin.kaeppel@uni-bayreuth.de)
- [lars.ackermann@uni-bayreuth.de](mailto:Lars.Ackermann@uni-bayreuth.de)
- [sebastian.petter@uni-bayreuth.de](mailto:sebastian.petter@uni-bayreuth.de)
- [stefan.jablonski@uni-bayreuth.de](mailto:stefan.jablonski@uni-bayreuth.de)
