package org.srlab.usask.iedit.reputationprocessor;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListReader;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class ReputationFromWebpage {
	public static void main(String[] args) {
		
//		Map<String, String> idOwnerId = new HashMap<String, String>();
//		
//		PostOwnersInfo objPostOwner = new PostOwnersInfo();
//		idOwnerId = objPostOwner.findPostOwner();
//		
//		System.out.println(idOwnerId.size());
		
		FindReputation obj = new FindReputation();
		obj.calculateReputation(); //idOwnerId
	}
}

class FindReputation{
	
	ICsvListReader listReader = null;
	ICsvListWriter csvWriter = null;
	
	public void calculateReputation() { //Map<String, String> checkPostOwner
		
		try {
			File file;
			Scanner sc;
			
			String location = "E:/MyResearchProjects/SOUserReputationDatabase/Data/";
			
 			listReader = new CsvListReader(new FileReader("F:/MyResearchProjects/SORejectedEdits/TOSEM/Results/ReputationInconsistentVsConsistent/Question_All_Data_With_Inconsistency.csv"), CsvPreference.STANDARD_PREFERENCE);
			listReader.getHeader(true);
			final CellProcessor[] processors = getProcessors();
			List<Object> userList;
			
			csvWriter = new CsvListWriter(new FileWriter("F:/MyResearchProjects/SORejectedEdits/TOSEM/Results/ReputationInconsistentVsConsistent/Reputation_Question_Inconsistency.csv"), CsvPreference.STANDARD_PREFERENCE);
			csvWriter.write("Id","UserId","Reputation","New","Low-Reputed","Established","Trusted");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			
			
			while((userList = listReader.read(processors))!= null) {
			
				String id = "";
				String date = "";
				String userID = "inactive";
				Date postEditDate = null;
				Date currentDay = null;
				
				int reputation = 1;
				
				try {
					id = userList.get(0).toString().trim();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					date = userList.get(4).toString().trim();
					String[] dateTime = date.split(" ");
					String editDate = dateTime[0];
					postEditDate = dateFormat.parse(editDate);
//					System.out.println(postEditDate);
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					userID = userList.get(9).toString().trim();
//					System.out.println(userID + " " + checkPostOwner.get(id));
					System.out.println(userID);
					
					if(userID != null && Integer.parseInt(userID)>0) {
						
						file = new File(location+userID+".txt");
						sc = new Scanner(file);
						
						while(sc.hasNext()) {
							String line = sc.nextLine();
							String[] dateReputation = line.split(",");
							String day = dateReputation[0]+"-"+dateReputation[1]+"-"+dateReputation[2];
							currentDay = dateFormat.parse(day);
//							System.out.println(currentDay);
							
							if (postEditDate.compareTo(currentDay)>=0) {
								reputation = reputation + Integer.parseInt(dateReputation[3]);
							}
							else {
								break;
							}
						}						
					}	
					
				} catch (Exception e) {
					e.printStackTrace();
				}

				
				if(reputation > 0 && reputation < 10 && !userID.equals(" ") ) {
					csvWriter.write(id,userID,reputation,1,0,0,0);
//					if(checkPostOwner.containsKey(id)) {
//						if(checkPostOwner.get(id).equals(userID)){
//							csvWriter.write(id,userID,reputation,1,0,0,0,1);
//						}else {
//							csvWriter.write(id,userID,reputation,1,0,0,0,0);
//						}
//					}
					
				}
				if(reputation >= 10 && reputation < 1000 && !userID.equals(" ")) {
					csvWriter.write(id,userID,reputation,0,1,0,0);
//					if(checkPostOwner.containsKey(id)) {
//						if(checkPostOwner.get(id).equals(userID)){
//							csvWriter.write(id,userID,reputation,0,1,0,0,1);
//						}else {
//							csvWriter.write(id,userID,reputation,0,1,0,0,0);
//						}
//					}
				}
				if(reputation >= 1000 && reputation < 20000 && !userID.equals(" ") ) {
					csvWriter.write(id,userID,reputation,0,0,1,0);
//					if(checkPostOwner.containsKey(id)) {
//						if(checkPostOwner.get(id).equals(userID)){
//							csvWriter.write(id,userID,reputation,0,0,1,0,1);
//						}else {
//							csvWriter.write(id,userID,reputation,0,0,1,0,0);
//						}
//					}
				}
				if(reputation >= 20000 && !userID.equals(" ")) {
					csvWriter.write(id,userID,reputation,0,0,0,1);
//					if(checkPostOwner.containsKey(id)) {
//						if(checkPostOwner.get(id).equals(userID)){
//							csvWriter.write(id,userID,reputation,0,0,0,1,1);
//						}else {
//							csvWriter.write(id,userID,reputation,0,0,0,1,0);
//						}
//					}
				}
				
			}
			csvWriter.close();
			System.out.println("Finished!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		finally {
	    	try {
	    		if( listReader != null ) {
	                listReader.close();
	        }	
			} catch (Exception e2) {
				
			}
	    }
		
	}
	
private static CellProcessor[] getProcessors() {
        
	    final CellProcessor[] processors = new CellProcessor[] {
	    		new Optional(), // post Id not null
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
//	            new Optional(),
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

class PostOwnersInfo{
	
	ICsvListReader listReader = null;
	ICsvListWriter csvWriter = null;
	Map<String, String> idOwnerId = new HashMap<String, String>();
	
	public Map<String, String> findPostOwner() {
		
		try {
		
			
 			listReader = new CsvListReader(new FileReader("E:/Projects/SORejectedEdits/TOSEM/Results/ReputationInconsistentVsConsistent/PostOwners.csv"), CsvPreference.STANDARD_PREFERENCE);
			listReader.getHeader(true);
			final CellProcessor[] processors = getProcessors();
			List<Object> userList;
			
			while((userList = listReader.read(processors))!= null) {
			
				String id = "";
				String ownerId = "";
				
				try {
					id = userList.get(0).toString().trim();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					ownerId = userList.get(2).toString().trim();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if(!ownerId.isEmpty()) {
					idOwnerId.put(id,ownerId);
				}
				
			}
				
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		finally {
	    	try {
	    		if( listReader != null ) {
	                listReader.close();
	        }	
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
		
		return idOwnerId;
		
	}
	
private static CellProcessor[] getProcessors() {
        
	    final CellProcessor[] processors = new CellProcessor[] {
	    		new Optional(), 
	            new Optional(),
	            new Optional()
	    };
	    
	    return processors;
	}
}