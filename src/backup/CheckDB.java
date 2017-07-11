package backup;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckDB {
	
	private final static String dirCommon = "D:/甲状腺标注/甲状腺标注数据20170217/";
	
	private static int allImage = 0;
	private static int rightImage = 0;
	
	private final static String dbName = "data.db"; // 合并后的数据库文件
	
	private final static String dbNameAfter = "data-after.db"; // 经过处理后的数据库文件
	
	public static void main(String[] args) {
		
		/*
		 * 把合并后的数据库文件中 没有问题的（原始图像和小图都存在的）的数据添加到处理后的数据库文件中
		 * 
		 */
		
		long starttime = System.currentTimeMillis();
		
		// 新建结果数据库
		
		String dbPath = dirCommon + dbNameAfter;
		File fDb = new File(dbPath);
		if(fDb.exists()){
			fDb.delete();
		}		
		createTable(dbPath);
		
		
		// 实际操作
		findImages();
		
		System.out.println("合计" + "\t" + allImage + "\t" + rightImage);
		
		long endtime = System.currentTimeMillis();
		
		System.out.println("\n总计耗时： " + (endtime - starttime)/(1.0 * 1000 * 60) + " 分钟");
	}
	public static void findImages(){
		Connection c = null;
	    Statement stmt = null;
	    
	    Connection cto = null;
	    Statement stmtto = null;
	    
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:" + dirCommon + dbName);
	      c.setAutoCommit(false);

	      cto = DriverManager.getConnection("jdbc:sqlite:" + dirCommon + dbNameAfter);
	      stmtto = cto.createStatement();
	      
	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "select * from regions;" );
	      String sql = "";
	      while ( rs.next() ) {
	    	  allImage++;
	    	  rightImage++;
	    	  String id = rs.getString("regionid");
	    	  String path = rs.getString("path");
	    	  String pathNew = path;
	    	  
	    	  String diagnose = rs.getString("diagnose");
	    	  String caseid = rs.getString("caseid");
	    	  String cropfactor = rs.getString("cropfactor");
	    	  String risk = rs.getString("risk");
	    	  String tirads = rs.getString("tirads");
	         
	    	  File file = new File(pathNew);
	    	  if(!pathNew.endsWith(".jpg") || !file.exists()){
	    		  System.out.println("\t\t小图不存在："+path);
	    		  rightImage--;
	    		  //break;
	    	  }else {
	    		  // System.out.println(pathNew);
	    		  String pathOrigion = pathNew.substring(0, pathNew.lastIndexOf("/"));
		    	  if(!pathOrigion.endsWith("_crop")){
		    		  System.out.println("\t\t大图不存在："+path);
		    	  }
		    	  else{
		    		  // System.out.println(pathOrigion);
		    		  if(pathNew.indexOf("_crop/") != pathNew.lastIndexOf("_crop/")){
		    			  System.out.println("-------可能有问题--------\t" + pathNew);
		    		  }
			    	  pathOrigion = pathNew.substring(0, pathNew.indexOf("_crop/"));
			         
			    	  File file2 = new File(pathOrigion);
			    	  if(!file2.exists()){
			    		  System.out.println("\t\t大图不存在：" + path);
			    	  }else{
			    		  sql += "insert into regions values ( " +
			                      "" + id + ", " +
			                      "'" + path + "', " +
			                      "'" + pathOrigion + "', " +
			                      "'" + diagnose + "', " +
			                      //"" + caseid + ", " +
			                      "'" + cropfactor + "', " +
			                      "'" + risk + "', " +
			                      "'" + tirads + "' " +
			                      " ); "; 
			    	  }
		    	  }
	    	  }
	      }
	      stmtto.executeUpdate(sql);
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
	
	
	public static void createTable(String dbPath){
	    System.out.println("新建数据库");
		Connection c = null;
	    Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	    	c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
	      stmt = c.createStatement();
	      String sql = "";
	      /*
	      sql = "CREATE TABLE artists " +
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
	      
	      */
	      
	      sql = "CREATE TABLE regions " +
                  "(id INT PRIMARY KEY, " +
                  " path varchar(50), " + 
                  " path_origion varchar(50), " + 
                  " diagnose varchar(29), " + 
                  //" caseid int, " + 
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
}