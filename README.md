# Comparing the expressiveness of Declare Process Models

> The Declare process modelling language has been established within the research community for modelling so-called flexible processes. Declare follows the declarative modelling paradigm and therefore guarantees flexible process execution. For several reasons declarative process models turned out to be hard to read and to comprehend. Thus, it is also hard to decide whether two process models are equal with respect to their expressiveness, whether one model is completely covered by another one and whether two models share a common modelling space. [...] We follow an automaton-based approach by transforming Declare process models into finite state automatons and apply automata theory for interpreting the expressiveness of the Declare process models to be compared. 

*Excerpt from the paper "Comparing the expressiveness of Declare Process Models" (currently under review)*

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
@article{schuetzenmeier-2022-cdm,
    title = "Comparing the expressiveness of Declare Process Models",
    author = "Sch\"{u}tzenmeier, Nicolai and K\"{a}ppel, Martin and 
        Ackermann, Lars and Petter, Sebastian and Jablonski, Stefan",
    journal = "Software and Systems Modeling (POEM 2021 Special Section)",
    year = "Currently under review",
    publisher = "Springer"
}
```

## Contact
- [nicolai.schuetzenmeier@uni-bayreuth.de](mailto:Nicolai.Schuetzenmeier@uni-bayreuth.de)
- [martin.kaeppel@uni-bayreuth.de](mailto:martin.kaeppel@uni-bayreuth.de)
- [lars.ackermann@uni-bayreuth.de](mailto:Lars.Ackermann@uni-bayreuth.de)
- [sebastian.petter@uni-bayreuth.de](mailto:sebastian.petter@uni-bayreuth.de)
- [stefan.jablonski@uni-bayreuth.de](mailto:stefan.jablonski@uni-bayreuth.de)