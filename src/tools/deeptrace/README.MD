# How to run the this execution

1.  Compiling whole project

```
# ant clean
# ant
```
2.  Compiling test file

```
# javac test/Test.java
```
Where **test/Test.java** is the relative directory path and testing java file
3.  Execute bytecode file to get executing results

```
# rrrun -classpath=./classes -tool=PE test.single.Test
```
where **test.single.Test** is the class name with pacakge index