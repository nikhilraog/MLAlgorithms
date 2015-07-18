/*** Author :Nikhil G Rao
The University of Texas at Dallas
 *****/


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.Random;

import javax.imageio.ImageIO;


public class KMeans {
	public static void main(String [] args){
		if (args.length < 3){
			System.out.println("Usage: Kmeans <input-image> <k> <output-image>");
			return;
		}
		try{
			BufferedImage originalImage = ImageIO.read(new File(args[0]));
			int k=Integer.parseInt(args[1]);
			BufferedImage kmeansJpg = kmeans_helper(originalImage,k);
			ImageIO.write(kmeansJpg, "jpg", new File(args[2])); 

		}catch(IOException e){
			System.out.println(e.getMessage());
		}	
	}

	private static BufferedImage kmeans_helper(BufferedImage originalImage, int k){
		int w=originalImage.getWidth();
		int h=originalImage.getHeight();
		BufferedImage kmeansImage = new BufferedImage(w,h,originalImage.getType());
		Graphics2D g = kmeansImage.createGraphics();
		g.drawImage(originalImage, 0, 0, w,h , null);
		// Read rgb values from the image
		int[] rgb=new int[w*h];
		int count=0;
		for(int i=0;i<w;i++){
			for(int j=0;j<h;j++){
				rgb[count++]=kmeansImage.getRGB(i,j);
			}
		}
		// Call kmeans algorithm: update the rgb values
		kmeans(rgb,k);

		// Write the new rgb values to the image
		count=0;
		for(int i=0;i<w;i++){
			for(int j=0;j<h;j++){
				kmeansImage.setRGB(i,j,rgb[count++]);
			}
		}
		return kmeansImage;
	}

	// Your k-means code goes here
	// Update the array rgb by assigning each entry in the rgb array to its cluster center
	private static void kmeans(int[] rgb, int k){

		int[] clustersCenter = new int[k]; // num of clusters
		int[] newClusterCenter = new int[k];
		int[] num_pointsinthisClutser = new int[k];
		Random rand = new Random();
		for(int i=0; i <k; i++)
		{

			/*int r = rand.nextInt(rgb.length);
			int g = rand.nextInt(rgb.length);
			int b = rand.nextInt(rgb.length);
			Color randomColor = new Color(r, g, b);*/
			//clustersCenter[i] = randomColor.getRGB();
			clustersCenter[i] = rand.nextInt(rgb.length);
			newClusterCenter[i] = 0;
			num_pointsinthisClutser[i] = 0;

		}
		double[] distanceVector = new double[k];
		int[] assignedCluster = new int[rgb.length];
		int count =0;
		int hit =0;
		do {
			count++;
			System.out.println("..."+count);
			for (int i = 0; i < rgb.length; i++) {
				Color point_i = new Color(rgb[i]);
				//System.out.println("color " + point_i);
				for (int j = 0; j < k; j++) {
					Color point_j = new Color(clustersCenter[j]);
					distanceVector[j] = calculateDistance(point_i, point_j);
				}
				double initailmin = distanceVector[0];
				assignedCluster[i] = 0;
				for (int j = 0; j < k; j++) {
					double min = distanceVector[j];
					if (min < initailmin) {
						initailmin = min;
						assignedCluster[i] = j; // assigining point i to jth cluster
					}
				}
				//get the cluster number that the point i belongs to 
				int cluster_num = assignedCluster[i];
				newClusterCenter[cluster_num] = newClusterCenter[cluster_num] + rgb[i];
				num_pointsinthisClutser[cluster_num] = num_pointsinthisClutser[cluster_num] + 1;

			}
			//int hit = 0;
			for (int j = 0; j < k; j++) {
				int newmean = 0;
				if (num_pointsinthisClutser[j] != 0) {
					newmean = newClusterCenter[j] / num_pointsinthisClutser[j];
					if (newmean == clustersCenter[j])
						hit++;

					else
						clustersCenter[j] = newmean;

				}
			}
			if (hit == k) {
				break;
			}
		} while (count < 100);
		for(int i=0;i<rgb.length;i++)
		{
			int l = assignedCluster[i];
			rgb[i] = clustersCenter[l];
		}
	}

	public static  double calculateDistance(Color point_i, Color point_j) {

		double red_dist = Math.abs(point_i.getRed() - point_j.getRed());
		double green_dist = Math.abs(point_i.getGreen() - point_j.getGreen());
		double blue_dist = Math.abs(point_i.getBlue() - point_j.getBlue());
		return Math.sqrt(red_dist*red_dist + green_dist*green_dist + blue_dist*blue_dist);

	}

}