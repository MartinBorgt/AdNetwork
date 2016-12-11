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
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;

import tau.tac.adx.report.adn.MarketSegment;

import java.io.File;
import java.io.FileInputStream;
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
	String impFilename = null;
	String ucsFilename = null;
	FileWriter writer;
	
	// creating classifier object for imp
	LinearRegression impLR = new LinearRegression();
	
	// creating classifier object for ucs
	IBk ucsIbk = new IBk();
		
	public Classifier(SampleAdNetworkModified adNetwork){
		this.adNetwork = adNetwork;
		this.run();
	}
	
	public void run(){
		 // load data
		try {
//			impFilename = Res.class.getResource("impdataset.arff").toURI();
//			ucsFilename = Res.class.getResource("ucsdataset.arff").toURI();
			impFilename = "/home/hsn/git/AdNetwork/IAAdNetwork/data/impdataset.arff";
			ucsFilename = "/home/hsn/git/AdNetwork/IAAdNetwork/data/ucsdataset.arff";
			DataSource impDataSource = new DataSource(new FileInputStream(new File(impFilename)));
			DataSource ucsDataSource = new DataSource(new FileInputStream(new File(ucsFilename)));
			
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
			/*
			// train the classifier
			if(ucsTrainingDataset.size()  < 1 && impTrainingDataset.size() < 1){
				double totalPay = 5.00;
				double reachedImpression = 1000.00;
				int day = 5;
				double ucsBid = 0.05;
				double ucsLevel = 1;
				String bidClass = "good";
				
				this.addImpInstance(totalPay, reachedImpression);
				this.addUcsInstance(ucsLevel, ucsBid, day);
				
				// build classifier
				ucsIbk.buildClassifier(ucsTrainingDataset);
				impLR.buildClassifier(impTrainingDataset);
			} else {
				// build classifier
				ucsIbk.buildClassifier(ucsTrainingDataset);
				impLR.buildClassifier(impTrainingDataset);
			}
			*/
			// build classifier
			ucsIbk.buildClassifier(ucsTrainingDataset);
			impLR.buildClassifier(impTrainingDataset);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// add training data instance to dataset
	public void addImpTrainingdata(CampaignLogReport camp, double totalPay, double reachedImpression){	
		PredictImpressionCost predImpCost = new PredictImpressionCost(adNetwork);
		
		double totalPayment = totalPay; // total payment for campaign
		double reachedImp = reachedImpression; // total reach for campaign on final day
		double pi = totalPayment / reachedImp;
		
		int dayBiddingFor = camp.getDayStart();
		
		System.out.println("total payment: "+totalPayment+" reach: "+reachedImp+" PI: "+pi + " bidding day: " + dayBiddingFor);
		
		for(int i = dayBiddingFor; i <= camp.getDayEnd(); i++){
			for (Iterator<MarketSegment> it = camp.getTargetSegment().iterator(); it.hasNext();) {
				MarketSegment segment = it.next();
				double popSt = predImpCost.predictAdvancePriceIndex(segment, i);
	
				Instance data = new DenseInstance(3);
				data.setValue(impTrainingDataset.attribute(0), popSt); // segment
				data.setValue(impTrainingDataset.attribute(1), dayBiddingFor); // campaign final day
				data.setValue(impTrainingDataset.attribute(2), pi); // pi

				data.setDataset(impTrainingDataset);

				// adding instance to list
				impTrainingDataset.add(data);
				break;
			}
		}

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
	
	// add new instance with class still unknown
	public void addImpInstance(double popSt, int dayBiddingFor){
		
		Instance datai = new DenseInstance(3);
		
		datai.setValue(impTrainingDataset.attribute(0), popSt); // segment
		datai.setValue(impTrainingDataset.attribute(1), dayBiddingFor); // campaign final day
		
		datai.setDataset(impTrainingDataset);
			
		// adding instance to list
		impTrainingDataset.add(datai);

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
	
	// gets the last instance of dataset and predict the class value and call method update
	public Double getImpClass(){
		// train classifier
		this.run();
		double pred = 0.00;
		
		try {
			Instance data = impTrainingDataset.lastInstance();
			if(data != null){
				pred = impLR.classifyInstance(data);
				this.updateImpDataset(data, pred);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return pred;
	}
	
	// adding predicted class value to the last instance of dataset
	public void updateImpDataset(Instance i, double pi){
		System.out.println("Instance " + i + " pred : " + pi);
		i.setValue(ucsTrainingDataset.attribute(impTrainingDataset.classIndex()), pi);

		// writing to file
		ArffSaver saver = new ArffSaver();
 		saver.setInstances(impTrainingDataset);
 		try {
			saver.setFile(new File(impFilename));
			saver.writeBatch();
			 System.out.println("Data updated to imp file");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// add training dataset to ucs dataset
	public void addUcsTrainingData(double ucsLevel, double ucsBid, int day){
		// creating empty instance with three attribute
		Instance data = new DenseInstance(4);
		
		// need to figure out what attribute should ucs classifier have from current won campaign using adnetwork
		// Set instance's values for the attributes "length", "weight", and "position"
		data.setValue(ucsTrainingDataset.attribute(0), ucsLevel);
		data.setValue(ucsTrainingDataset.attribute(1), day);
		data.setValue(ucsTrainingDataset.attribute(2), ucsBid);

		if(ucsLevel >= 0.8){
			data.setValue(ucsTrainingDataset.attribute(3), 0);
		}else{
			data.setValue(ucsTrainingDataset.attribute(3), 1);
		}
		
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
	
	// adding instance to dataset with no class to end of dataset
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
	
	// predicts ucs class
	public String getUcsClass(){
		// train classifier
		this.run();
		double pred = 0.00;
		String ucsClass = "No class";
		
		try {
			Instance data = ucsTrainingDataset.lastInstance();
			if(data != null){
				pred = ucsIbk.classifyInstance(data);
				ucsClass = ucsTrainingDataset.classAttribute().value((int) pred);
				this.updateUcsDataset(data, pred);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ucsClass;
	}
	
	//updating ucs dataset with new data
	public void updateUcsDataset(Instance data, double bidClass){

		if(bidClass == 0){
			data.setValue(ucsTrainingDataset.attribute(3), 0);
		} else {
			data.setValue(ucsTrainingDataset.attribute(3), 1);
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
}