package org.srlab.usask.iedit.customdatareader;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListReader;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class CustomDataReader {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		readForRandomSample();

	}
	
private static void readForRandomSample() throws Exception {
	
	ICsvListReader idReader = null;
	ICsvListReader listReader = null;
	ICsvListWriter csvWriter = null;
	
    List<String> idList = new ArrayList<String>();
    
    try {
    	
    	idReader = new CsvListReader(new FileReader("E:/Projects/SORejectedEdits/AUSE/Results/Machine Learning/TrainTestSplit/IdInconsistent.csv"), CsvPreference.STANDARD_PREFERENCE);
    	listReader = new CsvListReader(new FileReader("E:/Projects/SORejectedEdits/AUSE/Results/Machine Learning/TrainTestSplit/Rollback_Edits.csv"), CsvPreference.STANDARD_PREFERENCE);
        csvWriter = new CsvListWriter(new FileWriter("E:/Projects/SORejectedEdits/AUSE/Results/Machine Learning/TrainTestSplit/test-dataset-inconsistent.csv"),CsvPreference.STANDARD_PREFERENCE);
        
		csvWriter.write("postId","guiId","currentRevisionNo","prevRevisionNo","rollBackRevisionNo","editStatus","prevEditComment","preEditText","postEditText","rollbackUserId","rollbackUserName","rollbackDateTime","prevEditUserId","prevEditUserName","prevEditDateTime");
		idReader.getHeader(true);
		final CellProcessor[] idProcessors = getIdProcessors();               
        List<Object> questionIdList;
		
        while( (questionIdList = idReader.read(idProcessors)) != null ) {
       	 
            
        	String id="";                          		    // int     	
    
        	try {
        		id=questionIdList.get(0).toString().trim();
			}catch (Exception e) {
				// TODO: handle exception
			}
        	
        	idList.add(id.trim());
        	        	       	
        }
		
//        System.out.println(idList.size());
        
        listReader.getHeader(true); // skip the header (can't be used with CsvListReader)
        final CellProcessor[] processors = getProcessors();               
        List<Object> questionList;
        
        while( (questionList = listReader.read(processors)) != null ) {
        	 
        	String id="";
        	String column2 = "";
        	String column3 = "";
        	String column4 = "";
        	String column5 = "";
        	String column6 = "";
        	String column7 = "";
        	String column8 = "";
        	String column9 = "";
        	String column10 = "";
        	String column11 = "";
        	String column12 = "";
        	String column13 = "";
        	String column14 = "";
        	String column15 = "";
        	
        	try {
        		id=questionList.get(0).toString().trim();
			}catch (Exception e) {
				e.printStackTrace();
			}
        	try {
        		column2=questionList.get(1).toString().trim();
			}catch (Exception e) {
				e.printStackTrace();
			}
        	try {
        		column3=questionList.get(2).toString().trim();
			}catch (Exception e) {
				e.printStackTrace();
			}
        	try {
        		column4=questionList.get(3).toString().trim();
			}catch (Exception e) {
				e.printStackTrace();
			}
        	try {
        		column5=questionList.get(4).toString().trim();
			}catch (Exception e) {
				e.printStackTrace();
			}
        	try {
        		column6=questionList.get(5).toString().trim();
			}catch (Exception e) {
				e.printStackTrace();
			}
        	try {
        		column7=questionList.get(6).toString().trim();
			}catch (Exception e) {
				e.printStackTrace();
			}
        	try {
        		column8=questionList.get(7).toString().trim();
			}catch (Exception e) {
				e.printStackTrace();
			}
        	try {
        		column9=questionList.get(8).toString().trim();
			}catch (Exception e) {
				e.printStackTrace();
			}
        	try {
        		column10=questionList.get(9).toString().trim();
			}catch (Exception e) {
				e.printStackTrace();
			}
        	try {
        		column11=questionList.get(10).toString().trim();
			}catch (Exception e) {
				e.printStackTrace();
			}
        	try {
        		column12=questionList.get(11).toString().trim();
			}catch (Exception e) {
				e.printStackTrace();
			}
        	try {
        		column13=questionList.get(12).toString().trim();
			}catch (Exception e) {
				e.printStackTrace();
			}
        	try {
        		column14=questionList.get(13).toString().trim();
			}catch (Exception e) {
				e.printStackTrace();
			}
        	try {
        		column15=questionList.get(14).toString().trim();
			}catch (Exception e) {
				e.printStackTrace();
			}
        	        		
        	if(idList.contains(id)){
        		System.out.println(id);
        		
        		try {
        			csvWriter.write(id,
            				       column2,
            				       column3,
            				       column4,
            				       column5,
            				       column6,
            				       column7,
            				       column8,
            				       column9,
            				       column10,
            				       column11,
            				       column12,
            				       column13,
            				       column14,
            				       column15);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
        	}
        	
        	       	
        }
                
        csvWriter.close();
        System.out.println("Data Write in File Finished Successfully!!");
       
    }
    finally {
    	
        if( listReader != null ) {
                listReader.close();
                idReader.close();
        }
    }
}

private static CellProcessor[] getIdProcessors() {
	         
    final CellProcessor[] processors = new CellProcessor[] {
    		new Optional() // post Id not null           
    };
    
    return processors;
}

private static CellProcessor[] getProcessors() {
    
    final CellProcessor[] processors = new CellProcessor[] {
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
    
    return processors;
}

}