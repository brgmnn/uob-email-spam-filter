# Email Spam Filter #
<sup>2012</sup>

Classifies whether an email is spam or not using an Improved Bayesian
classifier. Also includes some optional email preprocessing. Classification
accuracy is of course very dependant on feature selection and training data
but I have achieved rates of 97% correctly classified emails with this
classifier.


### Dependencies ###

* Java and the JDK.
* GCC.
* GNU C Library (glibc).


### Compiling and Running ###

Compile with:

```
gcc launch.c -o launch-server
javac *.java
```

Classify an email with:

```
java filter path/to/email
```

It will take some seconds to classify an email the first time it is run,
however it should be very fast on subsequent runs.


### Optional preprocessing ###

* Header stripping.
* Header field extraction.
* Filtering words by regex.
* Filtering words by length (min and max).
* Replacing words by regex.
* Pair/triplet word adding (N grams).
* Case insensitivity.


### Client/Server code ###

For the assignment that I created this for I split the code in to a
client/server model for actually classifying emails. This was to allow a
larger set of training data to be used given the constraints of the
assignment.
