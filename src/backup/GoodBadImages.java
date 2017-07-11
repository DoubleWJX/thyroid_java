package backup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import tools.MyFileHelper;

public class GoodBadImages {
	
    private final static String dirCommon = "D:/甲状腺标注/甲状腺标注数据20170217/";
	private final static String dbName = "data-after.db";
	private final static String dataPath = "D:/甲状腺标注/甲状腺标注数据20170217/甲状腺数据-20170301/";
	
	private final static String dataTxtName = "data.txt";
	private final static String dataLETxtName = "data-良恶性.txt";
	private final static String dicTxtName = "dic.txt";
	private final static String dicTxtName_old = "new_edition.txt";
	private static int n = 0;
	private static int nL = 0;
	private static int nE = 0;
	private static int nW = 0;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long starttime = System.currentTimeMillis();
				
		// findImages(dirCommon + dbName);
		
		getData(dirCommon + dbName, dataPath);
		
		long endtime = System.currentTimeMillis();
		
		System.out.println("\n总计耗时： " + (endtime - starttime)/(1.0 * 1000 * 60) + " 分钟");
		System.out.println(n + "\t" + nL + "\t" + nE + "\t" + nW);
	}
	private static void getData(String dbPath, String dataPath){
		
		File fd = new File(dataPath);
		
		if(fd.exists()){
			fd.delete();
		}
		
		if(!fd.exists())
		    fd.mkdirs();
		
		File fdT = new File(dataPath + dataTxtName);
		File fdTLE = new File(dirCommon + dataLETxtName);
		if(fdT.exists()){
			fdT.delete();
		}
		if(fdTLE.exists()){
			fdTLE.delete();
		}
		try {
			fdT.createNewFile();
			fdTLE.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		getPictures(dbPath, dataPath);
		
		getDic(dirCommon + dicTxtName_old, dataPath + dicTxtName);
	}
	private static void getPictures(String dbPath, String dataPath){		
		Connection c = null;
	    Statement stmt = null;
	    File fdT = new File(dataPath + dataTxtName);
	    File fdTLE = new File(dirCommon + dataLETxtName);
	    FileWriter fwT = null;
	    FileWriter fwTLE = null;
	    try {
			fwT = new FileWriter(fdT, true);
			fwTLE = new FileWriter(fdTLE, true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
	      c.setAutoCommit(false);
	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "select * from regions;" );
	      String sql = "";
	      while ( rs.next() ) {
	    	  String id = rs.getString("id");
	    	  String path = rs.getString("path");
	    	  String pathOrigion = rs.getString("path_origion");
	    	  String diagnose = rs.getString("diagnose");
	    	  String cropfactor = rs.getString("cropfactor");
	    	  String risk = rs.getString("risk");
	    	  String tirads = rs.getString("tirads");
	         
	    	  File file = new File(path);
	    	  if(!path.endsWith(".jpg") || !file.exists()){
	    		  System.out.println("\t\t小图不存在："+path);
	    		  //break;
	    	  }else {
	    		  File fileOrigion = new File(pathOrigion);
	    		  if(!(pathOrigion.endsWith(".jpg") ||  pathOrigion.endsWith(".bmp") || 
	    				  pathOrigion.endsWith(".JPG") ||  pathOrigion.endsWith(".PNG"))
	    		      || !fileOrigion.exists()){
	    			  
		    		  System.out.println("\t\t大图不存在："+pathOrigion);
		    	  }
		    	  else{
		    		  n++;
			    	  System.out.println(n + "\t" + id + "\t" + path + "\t" + pathOrigion + "\t" + cropfactor + "\t" + diagnose);
			    	  String hz = MyFileHelper.getHouzhuiByFileName(path);
			    	  String hzO = MyFileHelper.getHouzhuiByFileName(pathOrigion);
			    	  String str = id + hz;
			    	  String strO = "_" + id + hzO;
			    	  
			    	  String diaStrs[] = diagnose.trim().split("");
			    	  if(diaStrs.length != 27){
			    		  System.out.println("不对啊");
			    		  System.exit(1);
			    	  }
			    	  boolean tag = false;
			    	  String strType = "未知";
			    	  nW++;
			    	  if(!diaStrs[0].equals("0")){
		    			  // System.out.println(strType);
			    		  if(!tag){
			    			  strType = "恶性";
			    			  nE++;
			    			  nW--;
				    		  tag = true;
			    		  }
			    	  }
			    	  
			    	  if(!diaStrs[1].equals("0") || diaStrs[3].equals("4") || diaStrs[3].equals("5") || diaStrs[25].equals("1")){
		    			  // System.out.println(strType);
			    		  if(!tag){
			    			  strType = "良性";
			    			  nL++;
			    			  nW--;
				    		  tag = true;
			    		  }else{
			    			  // System.out.println("重复");
			    			  strType = "未知";
					    	  // System.out.println(n + "\t" + id + "\t" + path + "\t" + pathOrigion + "\t" + cropfactor + "\t" + diagnose);
			    			  // System.exit(1);
			    		  }
			    	  }
			    	  if(!tag){
		    			  // System.out.println("都不是");
			    		  // System.out.println(n + "\t" + id + "\t" + path + "\t" + pathOrigion + "\t" + cropfactor + "\t" + diagnose);
			    	  }

			    	  MyFileHelper.copyFile(path, dataPath + str);
			    	  MyFileHelper.copyFile(pathOrigion, dataPath + strO);
			    	  
			    	  fwT.write(n + "\t" + str + "\t" + strO + "\t" + cropfactor + "\t" + diagnose + "\t" + strType + "\r\n");
			    	  fwTLE.write(n + "\t" + path + "\t" + pathOrigion + "\t" + cropfactor + "\t" + diagnose + "\t" + strType + "\r\n");
			    	  
		    	  }
	    	  }
	    	  if(n == 100000)
	    		  break;
	      }
	      rs.close();
	      stmt.close();
	      c.close();
	      
	      fwT.close();
	      fwTLE.close();
	      
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	}
	private static void getDic(String pathOld, String path){
		File f = new File(path);
		if(f.exists()){
			f.delete();
		}
		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File fo = new File(pathOld);
		BufferedReader bro = null;
		String xieru = "";
		try {
			bro = new BufferedReader(new FileReader(fo));
			String str = "";
			boolean tag = false;
			while((str = bro.readLine()) != null){
				System.out.println(str);
				if(str.indexOf(" ") != 0){
					if(xieru.equals("")){
						xieru += str.trim();
					}else{
						xieru += "\r\n" + str.trim();
					}
					xieru += "\t";
					tag = false;
				}else if(str.indexOf(" ") == 0){
					if(tag){
						xieru += ";";
					}
					tag = true;
					xieru += str.trim();
				}
			}
			bro.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileWriter fw = null;
		try {
			fw = new FileWriter(f, false);
			fw.write(xieru);
			fw.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static void findImages(String dbPath){
		int n = 0;
		Connection c = null;
	    Statement stmt = null;
	    
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
	      c.setAutoCommit(false);
	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "select * from regions;" );
	      String sql = "";
	      while ( rs.next() ) {
	    	  String id = rs.getString("id");
	    	  String path = rs.getString("path");
	    	  String pathOrigion = rs.getString("path_origion");
	    	  String diagnose = rs.getString("diagnose");
	    	  String cropfactor = rs.getString("cropfactor");
	    	  String risk = rs.getString("risk");
	    	  String tirads = rs.getString("tirads");
	         
	    	  File file = new File(path);
	    	  if(!path.endsWith(".jpg") || !file.exists()){
	    		  System.out.println("\t\t小图不存在："+path);
	    		  //break;
	    	  }else {
	    		  File fileOrigion = new File(pathOrigion);
	    		  if(!(pathOrigion.endsWith(".jpg") ||  pathOrigion.endsWith(".bmp") || 
	    				  pathOrigion.endsWith(".JPG") ||  pathOrigion.endsWith(".PNG"))
	    		      || !fileOrigion.exists()){
	    			  
		    		  System.out.println("\t\t大图不存在："+pathOrigion);
		    	  }
		    	  else{
		    		  n++;
			    	  System.out.println(n + "\t" + id + "\t" + path + "\t" + pathOrigion + "\t" + cropfactor);
		    	  }
	    	  }
	      }
	      rs.close();
	      stmt.close();
	      c.close();
	      
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	}
}
