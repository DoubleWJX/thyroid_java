package testDiagnosis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.omg.CORBA.PRIVATE_MEMBER;

import tools.MyFileHelper;

public class GoodBadImages {
	/*
	 * 整理数据给深度学习
	 * 
	 * 把原始图片、切出来的图片以及相关有用的数据整理出来
	 * 
	 */
	
	private final static String dbName = "data-after.db";
	private final static String dataPath = Configuration.dataDir_target + "甲状腺数据-" + Configuration.strDate + "/";
	private final static String dataPath_test = Configuration.dataDir_target + "test-" + Configuration.strDate + "/";
	
	private final static String dataTxtName = "data.txt";
	private final static String dataLETxtName = "data-良恶性.txt";
	private final static String dataLETxtName_test = "data-良恶性-test.txt";
	private final static String dataLENumberTxtName = "data-良恶性-数量.txt";
	private final static String dicTxtName = "dic.txt";
	private final static String dicTxtName_old = "new_edition.txt";
	private static int n = 0;
	private static int nL = 0;
	private static int nE = 0;
	private static int nW = 0;

	public static void main(String[] args) {
		mainmain();
	}
	
	public static void mainmain() {
		// TODO Auto-generated method stub
		long starttime = System.currentTimeMillis();
		
		getData(Configuration.dataDir_target + dbName, dataPath);
		
		long endtime = System.currentTimeMillis();
		
		System.out.println("\n总计耗时： " + (endtime - starttime)/(1.0 * 1000 * 60) + " 分钟");
		System.out.println(n + "\t" + nL + "\t" + nE + "\t" + nW);
	}
	private static void getData(String dbPath, String dataPath){
		
		File fd = new File(dataPath);
		File fd_test = new File(dataPath_test);
		
		if(fd.exists()){
			fd.delete();
		}
		if(fd_test.exists()){
			fd_test.delete();
		}
		
		if(!fd.exists())
		    fd.mkdirs();
		if(!fd_test.exists())
		    fd_test.mkdirs();
		
		File fdT = new File(dataPath + dataTxtName);
		File fdTLE = new File(Configuration.dataDir_target + dataLETxtName);
		File fdTLE_test = new File(Configuration.dataDir_target + dataLETxtName_test);
		if(fdT.exists()){
			fdT.delete();
		}
		if(fdTLE.exists()){
			fdTLE.delete();
		}
		if(fdTLE_test.exists()){
			fdTLE_test.delete();
		}
		try {
			fdT.createNewFile();
			fdTLE.createNewFile();
			fdTLE_test.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		 * 把图片拷贝到相应的文件夹
		 */
		getPictures(dbPath, dataPath);
		
		getDic(Configuration.dataDir_target + dicTxtName_old, dataPath + dicTxtName);
		
		getNumber(Configuration.dataDir_target + dataLENumberTxtName);
	}
	private static void getPictures(String dbPath, String dataPath){		
		Connection c = null;
	    Statement stmt = null;
	    File fdT = new File(dataPath + dataTxtName);
	    File fdTLE = new File(Configuration.dataDir_target + dataLETxtName);
	    File fdTLE_test = new File(Configuration.dataDir_target + dataLETxtName_test);
	    FileWriter fwT = null;
	    FileWriter fwTLE = null;
	    FileWriter fwTLE_test = null;
	    try {
			fwT = new FileWriter(fdT, true);
			fwTLE = new FileWriter(fdTLE, true);
			fwTLE_test = new FileWriter(fdTLE_test, true);
			fwTLE_test.write("id" + "\t" + "image" + "\t" + "label" + "\r\n");
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
	      
	      int nliange = 0;
	      while ( rs.next() ) {
	    	  
	    	  n++;
	    	  
	    	  String id = rs.getString("id");
	    	  String path = rs.getString("path");
	    	  String pathOrigion = rs.getString("path_origion");
	    	  String diagnose = rs.getString("diagnose");
	    	  String cropfactor = rs.getString("cropfactor");
	    	  String risk = rs.getString("risk");
	    	  String tirads = rs.getString("tirads");
	         
	    	  
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
	    			  nE--;
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

	    	  if(strType.equals("未知")){
	    		  continue;
	    	  }
	    	  MyFileHelper.copyFile(path, dataPath_test + str);

	    	  nliange++;
	    	  int tagLE = 1;
	    	  if(strType.equals("恶性")){
	    		  tagLE = 1;
	    	  }else if(strType.equals("良性")){
	    		  tagLE = 0;
	    	  }
	    	  

	    	  fwTLE_test.write(nliange + "\t" + str + "\t" + path + "\t" + risk + "\t" + tirads + "\t" + tagLE + "\r\n");
	      }
	      rs.close();
	      stmt.close();
	      c.close();
	      
	      fwT.close();
	      fwTLE.close();
	      fwTLE_test.close();
	      
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

	private static void getNumber(String dataPath){
		
		File file = new File(dataPath);
		
		if(file.exists()){
			file.delete();
		}
		
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			FileWriter fWriter = new FileWriter(file);
			fWriter.write("总数" + "\t" + "良性" + "\t" + "恶性" + "\t" + "未知" + "\r\n");
			fWriter.write(n + "\t" + nL + "\t" + nE + "\t" + nW + "\r\n");
			fWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
