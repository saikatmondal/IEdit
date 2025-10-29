package org.srlab.usask.iedit.machinelearning;

import java.io.FileReader;

import java.io.PrintWriter;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;

import org.supercsv.io.ICsvListReader;

import org.supercsv.prefs.CsvPreference;

public class DataForBagOfWords {

	public static void main(String[] args) {
		ExtractDataForForBagOfWords.dataExtractor();
	}
}

class ExtractDataForForBagOfWords {

	public static void dataExtractor() {
		ICsvListReader editReader = null;
//		ICsvListWriter csvWriter = null;
		try {
			
//			csvWriter = new CsvListWriter(new FileWriter("E:/Projects/SORejectedEdits/AUSE/Results/ManuallyAnalyzedDataset/Question-Rollback-PreEdit-For-BagOfWords.csv"),CsvPreference.STANDARD_PREFERENCE);
			editReader = new CsvListReader(new FileReader("E:/Projects/SORejectedEdits/AUSE/Results/Machine Learning/TrainTestSplit/test-dataset-v1.csv"),CsvPreference.STANDARD_PREFERENCE);
			PrintWriter myFileWriter = new PrintWriter("E:/Projects/SORejectedEdits/AUSE/Results/Machine Learning/TrainTestSplit/test-post-edit-text.txt","UTF-8");
			
//			csvWriter.write("postId","space","preText");
//			csvWriter.write("postId","postText");
			
			editReader.getHeader(true);
			
			final CellProcessor[] processors = getProcessors();
			List<Object> editList;
			int totalCounter = 0;
			
			while((editList = editReader.read(processors)) != null) {
				
				String postId = "";
				
				Document preEditDoc = null;
				Document postEditDoc = null;
				
				Elements preText = null;
				Elements postText = null;
				
				String preEditText = "";
				String postEditText = "";
				
				String singleLineString = "";
								
				try {
					postId = editList.get(0).toString().trim();
					System.out.println(postId);
	    								
				} catch (Exception e) {
					e.printStackTrace();
				}
				
//				try {
//					preEditDoc = Jsoup.parse(editList.get(7).toString());
//	    			preText = preEditDoc.select("p");
//					preEditText = preText.text().toString();
//					
//					singleLineString = preEditText.replaceAll("[\r\n]+", " ");
//					
//					if(singleLineString.equals("") || singleLineString.isEmpty()) {
//						singleLineString = "I have only code.";
//						System.out.println(singleLineString);
//					}					
//					myFileWriter.write(postId+" "+singleLineString+"\n");
//
//            		            							
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
				
				try {
					preEditDoc = Jsoup.parse(editList.get(8).toString());
	    			preText = preEditDoc.select("p");
					preEditText = preText.text().toString();
					
					singleLineString = preEditText.replaceAll("[\r\n]+", " ");
					
					if(singleLineString.equals("") || singleLineString.isEmpty()) {
						singleLineString = "I have only code.";
						System.out.println(singleLineString);
					}					
					myFileWriter.write(postId+" "+singleLineString+"\n");
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
//				csvWriter.write(postId," ",preEditText);
								
			}
			
			if(myFileWriter!= null) {
				myFileWriter.close();
			}
			
//			csvWriter.close();
			System.out.println("Total:"+totalCounter);
			System.out.println("Feature Extraction Successful !!!!");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
			
		finally {
			try {
				if(editReader != null) {
					editReader.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}		
	}
	private static CellProcessor[] getProcessors() {
		final CellProcessor[] processor = new CellProcessor[] {
			new Optional(),
			new Optional(),
			new Optional(),
			new Optional(),
			new Optional(),
			new Optional(),
			new Optional(),
			new Optional(),
			new Optional(),
			new Optional(),
			new Optional(),
			new Optional(),
			new Optional(),
			new Optional(),
			new Optional()
			
		};
		return processor;
	}
}