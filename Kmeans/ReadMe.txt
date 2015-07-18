Steps to compile and run Kmeans:

1. download the file KMeans.java
2. Compile the java program using 
	javac KMeans.java
3. Execute the program using the following command:
  java KMeans <Input image file along its path> <K value> <output image file with location>
 Example:
 C:\Users\nikhil\Desktop\Rao_ngr140030\KmeansTest\Koala.jpg  20 C:\Users\nikhil\Desktop\Rao_ngr140030\KmeansTest\Koala_20.jpg

steps to run the LibSVMtoCSV : converts LibSVM format file to CSV and then to Arff format:
1. javac LibSVM_csv.java
2. java LibSVM_csv <input.new> <Intermeditefile.csv> <inputInARFF.arff>
Example :
C:\Users\nikhil\Desktop\SVM\Testing\validation.new C:\Users\nikhil\Desktop\SVM\Testing\validation.csv C:\Users\nikhil\Desktop\SVM\validation.arff
and then run the LibSVM using command prompt
LibSVM.JAR svm-train -t Kernel traningFile
C:\UsersNikhill\DocumentsML\libsvm-3.20\windows>svm-train -t 0 training.new

