package backup;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MergeDb {
	
	private final static String dirCommonOld = "D:/甲状腺标注/标注数据"; // 这个是原始标注的数据库文件里面存放的路径
	
	private final static String dirCommon = "D:/甲状腺标注/甲状腺标注数据20170217/"; // 这个是存放标注数据的本地路径
	private final static String dbCombine = "data.db"; // 这个是合并后的数据库文件
	
	private final static String [] personalDirs = {"高璐莹", "刘佳", "刘如玉", "王娟娟", "王莹"}; // 每个标数据的人的名字
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		/*
		 * 运行本类把每个人标注的数据合并到一个数据库文件中，并把其中的路径都修改为本地路径
		 * 
		 */
		
		long starttime = System.currentTimeMillis();
		
		/*
		 * 新建所需要的数据
		 * 1. 新建存放合并数据的数据库文件，并建立相关表格
		 */
		initDatas();
		
		/*
		 * 遍历每个标注的文件夹，找到其中db文件，读取其中的数据并合并
		 * 
		 */		
		FilenameFilter ff= new FilenameFilter() {
			@Override
			public boolean accept(File arg0, String arg1) {
				// TODO Auto-generated method stub
				if(arg1.endsWith(".db"))
					return true;
				return false;
			}
		};
		
		for(int i = 0; i < personalDirs.length; i++){
			String dbDir = dirCommon + personalDirs[i];
			System.out.println(dbDir);
			File fMulu = new File(dbDir);
			File[] fList = fMulu.listFiles(ff);
			for(int j = 0; j < fList.length; j++){
				String dbPath = fList[j].getAbsolutePath();
				System.out.println("\t" + dbPath);
				
				// 把当前数据库的数据读到新的数据库
				insertIntoDb(dbPath, dirCommon + dbCombine, i);
								
			}
		}
		
		long endtime = System.currentTimeMillis();
		
		System.out.println("\n总计耗时： " + (endtime - starttime)/(1.0 * 1000 * 60) + " 分钟");
	}
	
	public static void initDatas(){
		String dbPath = dirCommon + dbCombine;
		File fDb = new File(dbPath);
		if(fDb.exists()){
			fDb.delete();
		}
		// 新建一个db文件
		Connection c = null;
	    Statement stmt = null;
	    try {
	    	Class.forName("org.sqlite.JDBC");
	    	c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
	    	c.close();
	    } catch ( Exception e ) {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    	System.exit(0);
	    }
	    
	    createTable(dbPath);
	}
	
	public static void createTable(String dbPath){
	    System.out.println("新建数据库");
		Connection c = null;
	    Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	    	c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
	      stmt = c.createStatement();
	     
	      String sql = "CREATE TABLE artists " +
	                   "(id INT PRIMARY KEY, " +
	                   " artist varchar(8), " + 
	                   " albumcount int, " + 
	                   " caseinfo varchar(600) " + 
	                   " ); "; 
	      stmt.executeUpdate(sql);
	      
	      sql = "CREATE TABLE albums " +
                  "(albumid INT PRIMARY KEY, " +
                  " path varchar(50), " + 
                  " hisid int " + 
                  " ); "; 
	      stmt.executeUpdate(sql);
	      
	      sql = "CREATE TABLE regions " +
                  "(regionid INT PRIMARY KEY, " +
                  " path varchar(50), " + 
                  " diagnose varchar(29), " + 
                  " caseid int, " + 
                  " cropfactor varchar(50), " + 
                  " risk varchar(50), " + 
                  " tirads varchar(50) " + 
                  " ); "; 
	      stmt.executeUpdate(sql);
	      
	      
	      stmt.close();
	      c.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("新建数据库成功");
	}
	
	public static void insertIntoDb(String fromPath, String toPath, int index){
		String personalDir = personalDirs[index];
		
		Connection c = null;
	    Statement stmt = null;
	    
	    Connection cto = null;
	    Statement stmtto = null;
	    
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:" + fromPath);
	      cto = DriverManager.getConnection("jdbc:sqlite:" + toPath);
	      c.setAutoCommit(false);
	      stmt = c.createStatement();
	      stmtto = cto.createStatement();
	      ResultSet rs = null;
	      System.out.println("\t" + "\t" + "插入artists表");
	      // artist 表
	      rs = stmt.executeQuery( "select * from artists;" ); 
	      while ( rs.next() ) {
	    	  String id = rs.getString("id");
	    	  String artist = rs.getString("artist");
	    	  String albumcount = rs.getString("albumcount");
	    	  String caseinfo = rs.getString("caseinfo");
	    	  
	    	  // System.out.println(id + "\t" + artist + "\t" + albumcount + "\t" + caseinfo);

		      ResultSet rsto = stmtto.executeQuery( "select * from artists where id = " + id +";" );
	    	  if(rsto.next()){
		    	  
		    	  System.out.println("\t" + "\t" + "\t" + "artists 已经存在: " + id + "\t" + artist + "\t" + albumcount + "\t" + caseinfo);
	    		  
	    	  }else{
	    		  String sql = "insert into artists values ( " +
	                      "" + id + ", " +
	                      "'" + artist + "', " +
	                      "" + albumcount + ", " +
	                      "'" + caseinfo + "' " +
	                      " ); "; 
	    	      stmtto.executeUpdate(sql);
	    	  }
	    	  
	    	  rsto.close();
	    	  
	      }
	      rs.close();

	      System.out.println("\t" + "\t" + "插入albums表");
	      // albums 表
	      rs = stmt.executeQuery( "select * from albums;" ); 
	      while ( rs.next() ) {
	    	  String id = rs.getString("albumid");
	    	  String path = rs.getString("path");
	    	  String hisid = rs.getString("hisid");
	    	  
	    	  path = dirCommon + personalDir + path.replace(dirCommonOld, "");

		      ResultSet rsto = stmtto.executeQuery( "select * from albums where albumid = " + id +";" );
	    	  if(rsto.next()){
		    	  
		    	  System.out.println("\t" + "\t" + "\t" + "albums 已经存在: " + id + "\t" + path + "\t" + hisid);
	    		  
	    	  }else{
	    		  String sql = "insert into albums values ( " +
	                      "" + id + ", " +
	                      "'" + path + "', " +
	                      "" + hisid + " " +
	                      " ); "; 
	    	      stmtto.executeUpdate(sql);
	    	  }
	    	  
	    	  rsto.close();
	    	  
	      }
	      rs.close();

	      System.out.println("\t" + "\t" + "插入regions表");
	      // regions 表
	      rs = stmt.executeQuery( "select * from regions;" ); 
	      while ( rs.next() ) {
	    	  String id = rs.getString("regionid");
	    	  String path = rs.getString("path");
	    	  String diagnose = rs.getString("diagnose");
	    	  String caseid = rs.getString("caseid");
	    	  String cropfactor = rs.getString("cropfactor");
	    	  String risk = rs.getString("risk");
	    	  String tirads = rs.getString("tirads");
	    	  
	    	  path = dirCommon + personalDir + path.replace(dirCommonOld, "");

		      ResultSet rsto = stmtto.executeQuery( "select * from regions where regionid = " + id +";" );
	    	  if(rsto.next()){
		    	  
		    	  System.out.println("\t" + "\t" + "\t" + "regions 已经存在: " + id + "\t" + path + "\t" + diagnose + "\t" + caseid + "\t" + cropfactor);
	    		  
	    	  }else{
	    		  String sql = "insert into regions values ( " +
	                      "" + id + ", " +
	                      "'" + path + "', " +
	                      "'" + diagnose + "', " +
	                      "" + caseid + ", " +
	                      "'" + cropfactor + "', " +
	                      "'" + risk + "', " +
	                      "'" + tirads + "' " +
	                      " ); "; 
	    	      stmtto.executeUpdate(sql);
	    	  }
	    	  
	    	  rsto.close();
	    	  
	      }
	      rs.close();
	      
	      stmt.close();
	      c.close();
	      stmtto.close();
	      cto.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	}

}
