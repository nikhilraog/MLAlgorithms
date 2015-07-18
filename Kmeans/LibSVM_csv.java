import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;


public class LibSVM_csv {

	public static void main(String[] args) throws Exception {
		
		DataOutputStream out1 = new DataOutputStream(new FileOutputStream(args[1]));
		 
		BufferedReader reader1 = new BufferedReader(new FileReader(args[0]));
		
		String l = reader1.readLine();
		String[] headers = l.split(" ");
		for(int j=0;j<headers.length-1;j++){
			out1.writeBytes("attr"+j+",");
		}
		out1.writeBytes("class"+"\n");
		reader1.close();
		
		BufferedReader reader = new BufferedReader(new FileReader(args[0]));
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			
				String[] values = line.split(" ");
				
				for(int i = 1; i<values.length;i++){
					String val = values[i];
					String[] temp = val.split(":");
					out1.writeBytes(temp[1]+",");
					
				}
				out1.writeBytes(values[0]+"\n");
				
		}
		
			
		
		CSVLoader loader = new CSVLoader();
		
	    loader.setSource(new File(args[1]));
	    
	    
	    Instances data = loader.getDataSet();
	 
	    // save ARFF
	    ArffSaver saver = new ArffSaver();
	    saver.setInstances(data);
	    saver.setFile(new File(args[2]));
	    //saver.setDestination(new File(args[2]));
	    saver.writeBatch();
		
		
	}
}