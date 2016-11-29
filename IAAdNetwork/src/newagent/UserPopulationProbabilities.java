package newagent;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import tau.tac.adx.report.adn.MarketSegment;

public class UserPopulationProbabilities {

	//Age, Gender, Income, Probability
	final static String[][] allProbabilities = new String[][] {{"18-24","M","0-30","526"},
			{"25-34","M","0-30","371"},
			{"35-44","M","0-30","263"},
			{"45-54","M","0-30","290"},
			{"55-64","M","0-30","284"},
			{"65+","M","0-30","461"},
			{"18-24","F","0-30","546"},
			{"25-34","F","0-30","460"},
			{"35-44","F","0-30","403"},
			{"45-54","F","0-30","457"},
			{"55-64","F","0-30","450"},
			{"65+","F","0-30","827"},
			{"18-24","M","30-60","71"},
			{"25-34","M","30-60","322"},
			{"35-44","M","30-60","283"},
			{"45-54","M","30-60","280"},
			{"55-64","M","30-60","245"},
			{"65+","M","30-60","235"},
			{"18-24","F","30-60","52"},
			{"25-34","F","30-60","264"},
			{"35-44","F","30-60","255"},
			{"45-54","F","30-60","275"},
			{"55-64","F","30-60","228"},
			{"65+","F","30-60","164"},
			{"18-24","M","60-100","11"},
			{"25-34","M","60-100","140"},
			{"35-44","M","60-100","185"},
			{"45-54","M","60-100","197"},
			{"55-64","M","60-100","157"},
			{"65+","M","60-100","103"},
			{"18-24","F","60-100","6"},
			{"25-34","F","60-100","75"},
			{"35-44","F","60-100","104"},
			{"45-54","F","60-100","122"},
			{"55-64","F","60-100","109"},
			{"65+","F","60-100","53"},
			{"18-24","M","100+","5"},
			{"25-34","M","100+","51"},
			{"35-44","M","100+","125"},
			{"45-54","M","100+","163"},
			{"55-64","M","100+","121"},
			{"65+","M","100+","67"},
			{"18-24","F","100+","3"},
			{"25-34","F","100+","21"},
			{"35-44","F","100+","47"},
			{"45-54","F","100+","57"},
			{"55-64","F","100+","48"},
			{"65+","F","100+","18"}};
	public UserPopulationProbabilities() {
	}
	
	public static int getProbabilityString(List<String> targetSegment){
		List<String[]> probabilities =  new LinkedList<String[]>(Arrays.asList(allProbabilities));
		for(String seg : targetSegment){
			//We remove all instances that are not what we are looking for
			if(seg.toString().equals("FEMALE")){
				for (Iterator<String[]> iterator = probabilities.iterator(); iterator.hasNext();) {
				    String[] probability = iterator.next();
				    if (!"F".equals(probability[1])) {
				        iterator.remove();
				    }
				}
			}
			
			if(seg.toString().equals("MALE")){
				for (Iterator<String[]> iterator = probabilities.iterator(); iterator.hasNext();) {
				    String[] probability = iterator.next();
				    if (!"M".equals(probability[1])) {
				        iterator.remove();
				    }
				}
			}
			if(seg.toString().equals("LOW_INCOME")){
				for (Iterator<String[]> iterator = probabilities.iterator(); iterator.hasNext();) {
				    String[] probability = iterator.next();
				    if (!("0-30".equals(probability[2])||"30-60".equals(probability[2]))) {
				        iterator.remove();
				    }
				}
			}
			if(seg.toString().equals("HIGH_INCOME")){
				for (Iterator<String[]> iterator = probabilities.iterator(); iterator.hasNext();) {
				    String[] probability = iterator.next();
				    if (("0-30".equals(probability[2])||"30-60".equals(probability[2]))) {
				        iterator.remove();
				    }
				}
			}
			if(seg.toString().equals("YOUNG")){
				for (Iterator<String[]> iterator = probabilities.iterator(); iterator.hasNext();) {
				    String[] probability = iterator.next();
				    if (!("18-24".equals(probability[0])||"25-34".equals(probability[0])||"35-44".equals(probability[0]))) {
				        iterator.remove();
				    }
				}
			}
			if(seg.toString().equals("OLD")){
				for (Iterator<String[]> iterator = probabilities.iterator(); iterator.hasNext();) {
				    String[] probability = iterator.next();
				    if (("18-24".equals(probability[0])||"25-34".equals(probability[0])||"35-44".equals(probability[0]))) {
				        iterator.remove();
				    }
				}
			}
		}
		
		//Then we add up the probabilities and return
		int returnvalue = 0;
		for (String[] probability : probabilities ) {
		    returnvalue += Integer.parseInt(probability[3]);
		}
		return returnvalue;
	}
	
	public static int getProbability(Set<MarketSegment> targetSegment){
		List<String[]> probabilities =  new LinkedList<String[]>(Arrays.asList(allProbabilities));
		for(MarketSegment seg : targetSegment){
			//We remove all instances that are not what we are looking for
			if(seg.toString().equals("FEMALE")){
				for (Iterator<String[]> iterator = probabilities.iterator(); iterator.hasNext();) {
				    String[] probability = iterator.next();
				    if (probability[1]!="F") {
				        iterator.remove();
				    }
				}
			}
			
			if(seg.toString().equals("MALE")){
				for (Iterator<String[]> iterator = probabilities.iterator(); iterator.hasNext();) {
				    String[] probability = iterator.next();
				    if (probability[1]!="M") {
				        iterator.remove();
				    }
				}
			}
			if(seg.toString().equals("LOW_INCOME")){
				for (Iterator<String[]> iterator = probabilities.iterator(); iterator.hasNext();) {
				    String[] probability = iterator.next();
				    if (!(probability[2]=="0-30"||probability[2]=="30-60")) {
				        iterator.remove();
				    }
				}
			}
			if(seg.toString().equals("HIGH_INCOME")){
				for (Iterator<String[]> iterator = probabilities.iterator(); iterator.hasNext();) {
				    String[] probability = iterator.next();
				    if ((probability[2]=="0-30"||probability[2]=="30-60")) {
				        iterator.remove();
				    }
				}
			}
			if(seg.toString().equals("YOUNG")){
				for (Iterator<String[]> iterator = probabilities.iterator(); iterator.hasNext();) {
				    String[] probability = iterator.next();
				    if (!(probability[0]=="18-24"||probability[0]=="25-34"||probability[0]=="35-44")) {
				        iterator.remove();
				    }
				}
			}
			if(seg.toString().equals("OLD")){
				for (Iterator<String[]> iterator = probabilities.iterator(); iterator.hasNext();) {
				    String[] probability = iterator.next();
				    if ((probability[0]=="18-24"||probability[0]=="25-34"||probability[0]=="35-44")) {
				        iterator.remove();
				    }
				}
			}
		}
		
		//Then we add up the probabilities and return
		int returnvalue = 0;
		for (String[] probability : probabilities ) {
		    returnvalue += Integer.parseInt(probability[3]);
		}
		return returnvalue;
	}
}
