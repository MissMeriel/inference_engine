# Steps to run

## 1. Download dependencies:
- commons-lang3-3.9/
- com.google.guava_1.6.0.jar
- commons-math3-3.6.1/
- java-cup-11a.jar
- org.apache.commons.lang3


## 2. Compile everything:
To compile parser: 
```

```

To compile engine on its own:
```
javac -classpath .:./commons-lang3-3.9/*:./com.google.guava_1.6.0.jar:commons-math3-3.6.1/*:./java-cup-11a.jar inference_engine/*.java
```
To compile assistant scripts:

```
javac 
javac Probability_To_Csv.java
```

## 3. Run scripts
```
cd src/
./generate_bag_probabilities.sh      # drone invariants
./generate_driving_probabilities.sh  # driving invariants
```

## FAQ
To run engine on a particular trace:
```
java -classpath .:./commons-lang3-3.9/*:./com.google.guava_1.6.0.jar:commons-math3-3.6.1/*:./java-cup-11a-runtime.jar inference_engine.Driver example.csv  example.config
```

FOR HELP:
```
java -classpath .:./commons-lang3-3.9/* Driver -h
```
