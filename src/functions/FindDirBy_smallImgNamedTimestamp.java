package functions;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class FindDirBy_smallImgNamedTimestamp {
	
	private final static String dbNameAfter = "data-after.db"; // 经过处理后的数据库文件
	
	private final static String strTargetImage = "D:\\甲状腺标注\\甲状腺标注数据（处理后）20170714\\其他事务\\查找图片的路径\\12res";
	
	public static void main(String[] args) {
		mainmain();
	}
	
	public static void mainmain() {
		
		/*
		 * 根据小图的名字（数据库的主键），找到文件夹
		 * 
		 */
		
		long starttime = System.currentTimeMillis();
				
		// 实际操作
		findImages();
		
		
		long endtime = System.currentTimeMillis();
		
		System.out.println("\n总计耗时： " + (endtime - starttime)/(1.0 * 1000 * 60) + " 分钟");
	}
	public static void findImages(){
		
		ArrayList<String> listImage = getListImageIdsByDir(strTargetImage);
		
		Connection c = null;
	    Statement stmt = null;
	    
	    try {
	      Class.forName("org.sqlite.JDBC");
	      
	      System.out.println(Configuration.dataDir_target + dbNameAfter);
	      c = DriverManager.getConnection("jdbc:sqlite:" + Configuration.dataDir_target + dbNameAfter);
	      c.setAutoCommit(false);
	      stmt = c.createStatement();
	      
	      for(int il = 0; il < listImage.size(); il++){
	    	  String strImagePath = listImage.get(il);
	    	  
	    	  String strId = strImagePath.substring(0, strImagePath.indexOf("_"));
	    	  
	    	  ResultSet rs = stmt.executeQuery( "select * from regions where id = '" + strId + "';" );

		      while ( rs.next() ) {

		    	  String id = rs.getString("id");
		    	  String path_origion = rs.getString("path_origion");
		    	  path_origion = path_origion.replaceAll("/", "\\\\");
		    	  path_origion = path_origion.substring(0,  path_origion.lastIndexOf("\\"));
		    	  
		    	  System.out.println(id + "\t" + path_origion);
		      }
		    	  
		      rs.close();
		  }
	      
	      stmt.close();
	      c.close();
	      
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	}
	
	private static ArrayList<String> getListImageIdsByDir(String strPath){
		// 根据路径获取要查找的图片名
		ArrayList<String> listImage = new ArrayList<>();
		
		FilenameFilter ff= new FilenameFilter() {
			@Override
			public boolean accept(File arg0, String arg1) {
				// TODO Auto-generated method stub
				return true;
			}
		};
		
		File fMulu = new File(strPath);
		File[] fList = fMulu.listFiles(ff);
		for(int j = 0; j < fList.length; j++){
			String dbPath = fList[j].getName().trim();
			System.out.println("\t" + dbPath);
			listImage.add(dbPath);
		}
		
		return listImage;
	}
}