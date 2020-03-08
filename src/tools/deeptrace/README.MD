# How to run the this execution

1.  Compiling whole project

```
# cd Roadrunner
# ant clean
# ant
```
2.  Compiling and execute test file

```
# javac test/Test.java
# java -cp ./build/jar/test.jar test.sync.Main
```
Where **test/Test.java** is the relative directory path and testing java file
3.  Execute bytecode file to get executing results

```
# rrrun -classpath=./classes -tool=PE test.Test
or 
# rrrun -classpath=./build/jar/test.jar -tool=DT test.Test
```
where **test.single.Test** is the class name with pacakge index