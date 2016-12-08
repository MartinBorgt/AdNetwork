package newagent;


/*
* weka library
*/
import weka.core.Instances;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.lazy.IBk;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.DenseInstance;
import weka.core.Instance;

import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

import tau.tac.adx.report.adn.MarketSegment;

import java.io.File;
import java.io.FileWriter;

public class Classifier {
	
	/*
	 * Filter - for preprocessing the data
	 * Classifier/Clusterer - built on the processed data
	 * Evaluating - how good is the classifier/clusterer?
	 * Attribute selection - removing irrelevant attributes from your data
	 * 
	 * implement in opportunity message class and send bit class
	 */
	
	SampleAdNetworkModified adNetwork;
	Instances impTrainingDataset;
	Instances ucsTrainingDataset;
	
	String impFilename = "/Users/Premlimbu/Desktop/git/AdNetwork/IAAdNetwork/data/impdataset.arff";
	String ucsFilename = "/Users/Premlimbu/Desktop/git/AdNetwork/IAAdNetwork/data/ucsdataset.arff";
	
	FileWriter writer;
	
	// creating classifier object for imp
	LinearRegression impLR = new LinearRegression();
	
	// creating classifier object for ucs
	IBk ucsIbk = new IBk();
	
	public Classifier(SampleAdNetworkModified adNetwork){
		this.adNetwork = adNetwork;
		
		 // load data
		try {
			DataSource impDataSource = new DataSource(impFilename);
			DataSource ucsDataSource = new DataSource(ucsFilename);
			
			// training dataset
			impTrainingDataset = impDataSource.getDataSet();
			ucsTrainingDataset = ucsDataSource.getDataSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		 // Make the last attribute be the class
		if(impTrainingDataset.classIndex() == -1){
			impTrainingDataset.setClassIndex(impTrainingDataset.numAttributes() - 1);
		}
		
		if(ucsTrainingDataset.classIndex() == -1){
			ucsTrainingDataset.setClassIndex(ucsTrainingDataset.numAttributes() - 1);
		}
		
		try {
			
			// train the classifier
			if(ucsTrainingDataset.size()  < 1 && impTrainingDataset.size() < 1){
				double totalPay = 5.00;
				double reachedImpression = 1000.00;
				int day = 5;
				double ucsBid = 0.05;
				double ucsLevel = 1;
				String bidClass = "good";
				
				this.addImpInstance(totalPay, reachedImpression);
				//this.addUcsInstance(ucsLevel, ucsBid, day, bidClass);
				this.addUcsInstance(ucsLevel, ucsBid, day);
				
				ucsIbk.buildClassifier(ucsTrainingDataset);
				impLR.buildClassifier(impTrainingDataset);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	// Note - need to be called day before campaign finishes
	public void addImpInstance(double totalPay, double reachedImpression){	
		// creating empty instance with three attribute
		// Adv. price-index (segment popularity), segment, at day t
		Instance data = new DenseInstance(3);
		
		double totalPayment = totalPay; // total payment for campaign
		double reachedImp = reachedImpression; // total reach for campaign on final day
		
		double pi = totalPayment / reachedImp;
		
		PredictImpressionCost predCost = new PredictImpressionCost(adNetwork);
		CampaignData currCampaign = adNetwork.getCurrCampaign();
		
		int dayBiddingFor = adNetwork.getDay() + 1;
		
		for (Iterator<MarketSegment> it = currCampaign.targetSegment.iterator(); it.hasNext();) {
			MarketSegment marketSegment = it.next();
			double popSt = predCost.predictAdvancePriceIndex(marketSegment, dayBiddingFor);
			
			data.setValue(impTrainingDataset.attribute(0), popSt); // segment
			data.setValue(impTrainingDataset.attribute(1), dayBiddingFor); // campaign final day
			data.setValue(impTrainingDataset.attribute(2), pi); // pi
		}
		
		/*
		Random rand = new Random();
		double popSt = rand.nextDouble();
		int dayBiddingFor = rand.nextInt();
		
		data.setValue(impTrainingDataset.attribute(0), popSt); // segment
		data.setValue(impTrainingDataset.attribute(1), dayBiddingFor); // campaign final day
		data.setValue(impTrainingDataset.attribute(2), pi); // pi
		*/
		
		// Set instance's dataset to be the dataset "race" 
		data.setDataset(impTrainingDataset);; //
		
		
		// adding instance to list
		impTrainingDataset.add(data);
		
		// saving to arff file
		ArffSaver saver = new ArffSaver();
 		saver.setInstances(impTrainingDataset);
 		try {
			saver.setFile(new File(impFilename));
			saver.writeBatch();
			System.out.println("Data saved to imp file");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public Double getImpClass(){
		double pred = 0.00;
		
		try {
			Instance data = impTrainingDataset.lastInstance();
			if(data != null){
				pred = impLR.classifyInstance(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return pred;
	}
	
	// Note goes in campaign opportunity class
	public void addUcsInstance(double ucsLevel, double ucsBid, int day){
		// creating empty instance with three attribute
		Instance data = new DenseInstance(4);
		
		// need to figure out what attribute should ucs classifier have from current won campaign using adnetwork
		// Set instance's values for the attributes "length", "weight", and "position"
		data.setValue(ucsTrainingDataset.attribute(0), ucsLevel);  
		data.setValue(ucsTrainingDataset.attribute(1), day);
		data.setValue(ucsTrainingDataset.attribute(2), ucsBid);
		
		// Set instance's dataset to be the dataset "race" 
		data.setDataset(ucsTrainingDataset);
		
		// adding instance to list
		ucsTrainingDataset.add(data);
		
		// writing to file
		ArffSaver saver = new ArffSaver();
 		saver.setInstances(ucsTrainingDataset);
 		try {
			saver.setFile(new File(ucsFilename));
			saver.writeBatch();
			 System.out.println("Data saved to ucs file");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Note goes in campaign opportunity class
	public void addUcsInstance(Instance data, String bidClass){
		
		String className = bidClass;
		
		switch(className) {
			case "too_low":
				data.setValue(ucsTrainingDataset.attribute(3), 0);
				break;
			case "low":
				data.setValue(ucsTrainingDataset.attribute(3), 1);
				break;
			case "good":
				data.setValue(ucsTrainingDataset.attribute(3), 2);
				break;
			case "high":
				data.setValue(ucsTrainingDataset.attribute(3), 3);
				break;
			default:
				data.setValue(ucsTrainingDataset.attribute(3), 4);
				break;
		}
		
		// writing to file
		ArffSaver saver = new ArffSaver();
 		saver.setInstances(ucsTrainingDataset);
 		try {
			saver.setFile(new File(ucsFilename));
			saver.writeBatch();
			 System.out.println("Data saved to ucs file");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getUcsClass(){
		
		int pred = 0;
		String ucsClass = "No class";
		
		try {
			Instance data = ucsTrainingDataset.lastInstance();
			if(data != null){
				pred = (int) ucsIbk.classifyInstance(data);
				ucsClass = ucsTrainingDataset.classAttribute().value(pred);
				this.addUcsInstance(data, ucsClass);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ucsClass;
	}

	/*
	public static void main(String [] args){
		Classifier classify = new Classifier();
		
		Random rand = new Random();
		
		double totalPay = rand.nextDouble();
		double reachedImpression = rand.nextDouble();
		
		double ucsLevel = rand.nextDouble();
		double ucsBid = rand.nextDouble();
		int day = rand.nextInt();
		String c = "too_low";
		
		for(int i = 0; i <= 5; i++){
			classify.addImpInstance(totalPay, reachedImpression);
			//classify.addUcsInstance(ucsLevel, ucsBid, day , c);
			classify.addUcsInstance(ucsLevel, ucsBid, day);
		}
	}
	*/
}
