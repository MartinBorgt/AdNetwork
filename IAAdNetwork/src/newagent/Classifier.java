package newagent;


/*
* weka library
*/
import weka.core.Instances;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.core.Instance;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;

public class Classifier {
	
	/*
	 * Filter - for preprocessing the data
	 * Classifier/Clusterer - built on the processed data
	 * Evaluating - how good is the classifier/clusterer?
	 * Attribute selection - removing irrelevant attributes from your data
	 */
	
	Instances impTrainingDataset;
	Instances ucsTrainingDataset;
	
	String impFilename = "/arff/Impdata.arff";
	String ucsFilename = "/arff/UCSdata.arff";
	
	FileWriter writer;
	
	// creating classifier object for imp
	NaiveBayes impNB = new NaiveBayes();
	
	// creating classifier object for ucs
	NaiveBayes ucsNB = new NaiveBayes();
	
	public Classifier(){
		 // load data
		try {
			DataSource impSource = new DataSource(impFilename);
			DataSource ucsSource = new DataSource(ucsFilename);
			
			// training dataset
			impTrainingDataset = impSource.getDataSet();
			ucsTrainingDataset = ucsSource.getDataSet();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// setting the index that store class
		if(impTrainingDataset.classIndex() == -1){
			impTrainingDataset.setClassIndex(impTrainingDataset.numAttributes() - 1);
		} else if(ucsTrainingDataset.classIndex() == -1){
			ucsTrainingDataset.setClassIndex(ucsTrainingDataset.numAttributes() - 1);
		}

		try {
			// train the classifier
			impNB.buildClassifier(impTrainingDataset);
			ucsNB.buildClassifier(ucsTrainingDataset);
			
			System.out.println(impNB.getCapabilities().toString());
			System.out.println(ucsNB.getCapabilities().toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	// 
	public void addImpInstance(Instance impData){
		/*
		 *     String dataSetFileName = "stackoverflowQuestion.arff";
    Instances data = MyUtilsForWekaInstanceHelper.getInstanceFromFile(dataSetFileName);
    System.out.println("Before adding");
    System.out.println(data);


    double[] instanceValue1 = new double[data.numAttributes()];
    instanceValue1[0] = 244;
    instanceValue1[1] = 59;
    instanceValue1[2] = 2;
    instanceValue1[3] = 880606923;

    DenseInstance denseInstance1 = new DenseInstance(1.0, instanceValue1);

    data.add(denseInstance1);
    
    
 //assuming we already have arff loaded in a variable called dataset
     Instance newInstance  = new Instance();
     for(int i = 0 ; i < dataset.numAttributes() ; i++)
     {

         newInstance.setValue(i , value);
         //i is the index of attribute
         //value is the value that you want to set
     }
     //add the new instance to the main dataset at the last position
     dataset.add(newInstance);
     //repeat as necessary
      * 
      * 
 Instances dataSet = ...
 ArffSaver saver = new ArffSaver();
 saver.setInstances(dataSet);
 saver.setFile(new File("./data/test.arff"));
 saver.setDestination(new File("./data/test.arff"));   // **not** necessary in 3.5.4 and later
 saver.writeBatch();
		 */
		
		try {
			//true will append the new instance
			writer = new FileWriter(impFilename,true);
			
			//appends the string to the file
			writer.write("244 59  2   880606923\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getImpClass(){
		String impClass = "";
		/*
		 * create instance
		 * 
		for (int i = 0; i < impDataset.numInstances(); i++) {
			try {
				int pred = (int) impNB.classifyInstance(impDataset.instance(i)); // add instance here
				System.out.print("ID: " + impDataset.instance(i).value(0));
				System.out.print(", actual: " + impDataset.classAttribute().value((int) impDataset.instance(i).classValue()));
				// it getst the 
				System.out.println(", predicted: " + impDataset.classAttribute().value((int) pred));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		 */
		return impClass;
	}
	
	public void addUcsInstance(Instances ucsDataset){
		try {
			//true will append the new instance
			writer = new FileWriter(ucsFilename,true);
			
			//appends the string to the file
			writer.write("244 59  2   880606923\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getUcsClass(){
		String impClass = "";

		for (int i = 0; i < ucsDataset.numInstances(); i++) {
			try {
				int pred = (int) ucsNB.classifyInstance(ucsDataset.instance(i));
				System.out.print("ID: " + ucsDataset.instance(i).value(0));
				System.out.print(", actual: " + ucsDataset.classAttribute().value((int) ucsDataset.instance(i).classValue()));
				System.out.println(", predicted: " + ucsDataset.classAttribute().value((int) pred));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		 
		return impClass;
	}
}
