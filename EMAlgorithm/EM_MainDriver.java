import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;


public class EM_MainDriver {

	static ArrayList<Double> datapoints = new ArrayList<Double>();
	static int k;
	static ArrayList<Double> means = new ArrayList<Double>(); 
	static ArrayList<Double> variance = new ArrayList<Double>(); 
	static ArrayList<Double> sum_bi = new ArrayList<Double>();
	static ArrayList<Double> weights = new ArrayList<Double>();
	static ArrayList<Double> oldmeans = new ArrayList<Double>();
	static double probability_xj_bk [][];
	static double posterior_probability_bk_xi [][];
	static double unconditional_prob [];
	public static void main(String[] args) throws Exception {
		
		System.out.println("Usage : please input <EM> <K - number of cluster> <Initialization: 1 or 2> <EM type : 1 or 2 > <Variance Uniform initailization : 1-Nonuniform variance intialization or 2- Uniform variance of value = 1.0 >");
		String datafile = args[0];
		int kpoints = Integer.parseInt(args[1]); // Number of clusters kind of as in k-means
		int variancetype = Integer.parseInt(args[2]);
		int emtype = Integer.parseInt(args[3]);
		int var_uniform = Integer.parseInt(args[4]);
		datapoints = readFile(datafile,kpoints);
		k = kpoints;

		System.out.println(datapoints.size());

		if(emtype == 1 && variancetype == 1){
			findMeansVariance(var_uniform);
			findPosteriorEM(10000,1);
		}

		else if(emtype == 1 && variancetype == 2){
			System.out.println("2 1 <univar or not>: variance type 2  Em type 1 ");
			findMeansVariance_method2(var_uniform);
			findPosteriorEM(10000,1);
		}

		else if(emtype == 2 && variancetype == 1){
			findMeansVariance(var_uniform);
			findPosteriorEM(10000,2);

		}
		else if (emtype == 2 && variancetype == 2){
			findMeansVariance_method2(var_uniform);
			findPosteriorEM(10000,2);
		}
	}

	private static void findMeansVariance_method2(int variancetype) {
		Random random = new Random();

		System.out.println("Initailization 2");
		Double[] sum = new Double[k];
		int num [] = new int[k];

		for(int j=0;j<datapoints.size();j++){
			int point_num= random.nextInt(k); // get a random number to select the cluster k		
			sum[point_num] = datapoints.get(j);
			num[point_num] = num[point_num]+1;
		}
		for(int i = 0 ; i<k;i++){
			means.add(i, sum[i]/num[i]);
			oldmeans.add(i, 0.0);
			weights.add(i, 1.0/k);
			if(variancetype == 1){
				double a = 0.0;
				for(int j = 0 ; j<datapoints.size();j++){
					a = a+ weights.get(i)*Math.pow(datapoints.get(j)-means.get(i),2);
				}
				variance.add(i, a/datapoints.size());
			}else{
				variance.add(i, 1.0);
			}
		}

		for(int i=0;i<k;i++){
			for(int j=0;j<datapoints.size();j++)// P(x_i | b)  = 1/sqrt(2*Pi*var_i) *(- exp (x_i - mean_B_i)^2 /2*var_i))
			{	

				probability_xj_bk[j][i] = 1.0/(Math.sqrt(2*Math.PI*variance.get(i)))*Math.exp(-1.0*(datapoints.get(j)-means.get(i))*(datapoints.get(j)-means.get(i))/(2*variance.get(i)));
				unconditional_prob[j] = unconditional_prob[j] + weights.get(i)*probability_xj_bk[j][i];
				//System.out.println("data point "+datapoints.get(j)+ " Pr(xi|b) =  probabilty it belongs to k "+(i+1) + " is "+probability_xj_bk[j][i]);

			}

		}



	}

	private static void findPosteriorEM(int num_iterations, int method_num) {
		int num_match = 0;
		int done = 0;
		do {

			//Pr(b|xi) = Pr(xi|b)*Pr(b) /Pr(xi)
			//Px(xi) = Pr(xi|b)*Pr(b) + Px(xi|a)*Pr(a) +...
			for(int i = 0;i<k;i++){

				means.add(i, 0.0);
				variance.add(i,0.0);
				sum_bi.add(i, 0.0);
			}
			//variance.clear();
			//sum_bi.clear();
			for (int i = 0; i < k; i++) {
				for (int j = 0; j < datapoints.size(); j++) {

					posterior_probability_bk_xi[j][i] = (1.0* weights.get(i)* probability_xj_bk[j][i]) / unconditional_prob[j];
					double b = sum_bi.get(i)+ posterior_probability_bk_xi[j][i];
					sum_bi.add(i, b);
					double a = means.get(i)+ (datapoints.get(j) * posterior_probability_bk_xi[j][i]);
					means.add(i, a);

				}

			}
			//Now update the new mean and variance for K points
			//updating mean here	
			for (int i = 0; i < k; i++) {
				weights.add(i, sum_bi.get(i) / datapoints.size());

				double a = means.get(i) / sum_bi.get(i);
				means.add(i, a);


			}
			//updating variance here 
			for (int i = 0; i < k; i++) {
				double a = variance.get(i);
				for (int j = 0; j < datapoints.size(); j++) {
					a = a+ posterior_probability_bk_xi[j][i]* Math.pow((datapoints.get(j) - means.get(i)),2);
				}
				variance.add(i, a / sum_bi.get(i));
			}
			for (int i = 0; i < datapoints.size(); i++) {
				unconditional_prob[i] = 0.0;
			}
			for (int i = 0; i < k; i++) {
				for (int j = 0; j < datapoints.size(); j++) {
					probability_xj_bk[j][i] = 1.0
							/ (Math.sqrt(2 * Math.PI * variance.get(i)))
							* Math.exp(-1.0
									* (datapoints.get(j) - means.get(i))
									* (datapoints.get(j) - means.get(i))
									/ (2 * variance.get(i)));
					unconditional_prob[j] = unconditional_prob[j]
							+ weights.get(i) * probability_xj_bk[j][i];
				}
			}
			for (int i = 0; i < k; i++) {
				if (Math.abs(oldmeans.get(i) - means.get(i)) < 0.001){
					done = 1;
					if(method_num == 2){
						System.out.println("method 2 EM ");
						num_match = num_match+1;
					}
				}
				oldmeans.add(i, means.get(i));
			}
			if (done == 1 && method_num!=2) {
				System.out.println("Method 1 EM");
				for (int i = 0; i < k; i++)
					System.out.println("The means for cluster sampled from "+ i + " is " + means.get(i));

				break;
				//System.out.println("The number of iteration taken are "+z);
			}
			else if (done == 1 && method_num == 2){

				for (int i = 0; i < k; i++)
					System.out.println("The means for cluster sampled from "+ i + " is " + means.get(i));

				break;

			}
			num_iterations--;
		} while (num_iterations>0 );
		double ll =0.0;
		for (int i = 0; i < k; i++){
			ll = ll+ means.get(i);
			System.out.println("---># of iterations :"+num_iterations+"The means for cluster sampled from "+ i + " is " + means.get(i) + "and variance: "+variance.get(i));
		}
		System.out.println("Log likely hood: "+ Math.log(ll));
	}

	private static void findMeansVariance(int variancetype) {
		Random random = new Random();

		for(int i =0;i<k;i++){
			int r = random.nextInt(datapoints.size());
			means.add(i, datapoints.get(r));
			oldmeans.add(i, datapoints.get(r));
			weights.add(i, 1.0/k);
			double diff = 0;
			if(variancetype == 1){
				for(int j =0;j<datapoints.size();j++){
					// diff = diff + (1.0/k)*Math.pow((datapoints.get(j)-means.get(i)), 2);
					diff = diff + weights.get(i)*Math.pow((datapoints.get(j)-means.get(i)), 2);
				}
				variance.add(i, diff/datapoints.size());
				//System.out.println("Point " + i+ " Random point num"+r+" Mean "+means.get(i)+ " variance "+variance.get(i));
			}
			else{
				System.out.println("here i'm ");
				variance.add(i, 1.0);
			}
		}

		for(int i=0;i<k;i++){
			for(int j=0;j<datapoints.size();j++)// P(x_i | b)  = 1/sqrt(2*Pi*var_i) *(- exp (x_i - mean_B_i)^2 /2*var_i))
			{	

				//given blue parameters (mean and variance) what is the probability that point x belongs to tat blue cluster
				//pr(x|b) -->
				probability_xj_bk[j][i] = 1.0/(Math.sqrt(2*Math.PI*variance.get(i)))*Math.exp(-1.0*(datapoints.get(j)-means.get(i))*(datapoints.get(j)-means.get(i))/(2*variance.get(i)));
				unconditional_prob[j] = unconditional_prob[j] + weights.get(i)*probability_xj_bk[j][i];
				//System.out.println("data point "+datapoints.get(j)+ " Pr(xi|b) =  probabilty it belongs to k "+(i+1) + " is "+probability_xj_bk[j][i]);
				//Pr(b|x) = Prior(B) * Pr(x|b) /Pr(X) 
				//Pr(X) = sum of probabilities of all the values that x can take on = pr(b)*pr(x|b)+pr(a)*pr(x|a) 
			}
			//break;
		}
	}

	private static ArrayList<Double> readFile(String datafile,int k) throws Exception {
		ArrayList<Double> datapoints = new ArrayList<Double>(); 
		BufferedReader br = new BufferedReader(new FileReader(datafile));
		for(String line = br.readLine();line !=null;line = br.readLine()){
			double d = Double.parseDouble(line);
			datapoints.add(d);
		}
		probability_xj_bk = new double [datapoints.size()][k];
		posterior_probability_bk_xi = new double [datapoints.size()][k];
		unconditional_prob = new double [datapoints.size()];
		return datapoints;
	}


}
