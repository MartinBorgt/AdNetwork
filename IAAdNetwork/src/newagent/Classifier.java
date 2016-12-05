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
import java.io.File;
import java.io.FileNotFoundException;

public class Classifier {
	
	Instances impDataset;
	Instances ucsDataset;
	
	
	public Classifier(){
		 // load data
		try {
			DataSource impSource = new DataSource("/arff/Impdata.arff");
			DataSource ucsSource = new DataSource("/arff/Impdata.arff");
			
			impSource = new DataSource("/arff/Impdata.arff");
			ucsSource = new DataSource("/arff/ucsdata.arff");
			
			impDataset = impSource.getDataSet();
			ucsDataset = ucsSource.getDataSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// setting the index that store class
		if(impDataset.classIndex() == -1){
			impDataset.setClassIndex(impDataset.numAttributes() - 1);
		} else if(ucsDataset.classIndex() == -1){
			ucsDataset.setClassIndex(ucsDataset.numAttributes() - 1);
		}
		
		// creating classifier object for imp
		NaiveBayes impNB = new NaiveBayes();
		
		// creating classifier object for ucs
		NaiveBayes ucsNB = new NaiveBayes();

		try {
			impNB.buildClassifier(impDataset);
			ucsNB.buildClassifier(ucsDataset);
			
			System.out.println(impNB.getCapabilities().toString());
			System.out.println(ucsNB.getCapabilities().toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void setImpressionClassfier(){
		
	}
	
	public void getImpressionClassifier(){
		
	}
	
	public void setUCSClassfier(){
		
	}
	
	public void getUCSClassifier(){
		
	}
}
