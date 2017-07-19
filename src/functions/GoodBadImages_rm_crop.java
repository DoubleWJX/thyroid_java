package functions;

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

public class GoodBadImages_rm_crop {
	/*
	 * 整理数据给深度学习
	 * 
	 * 把原始图片、切出来的图片以及相关有用的数据整理出来
	 * 
	 * 和GoodBadImages.java不一样的是，本类会去掉那些小图的原图有问题的图
	 * 
	 * 具体来讲，就是如果小图的原图所属的文件夹含有:region_crop，就说明这个原图是被裁剪过的，就去掉
	 * 
	 */
	
	private final static String dbName = "data-after.db";
	// private final static String dataPath = "D:/甲状腺标注/甲状腺标注数据20170525/甲状腺数据-20170525/";
	private final static String dataPath = Configuration.dataDir_target + "甲状腺数据-" + Configuration.strDate + "/";
	
	private final static String dataTxtName = "data-crop.txt";
	private final static String dataLETxtName = "data-良恶性-crop.txt";
	private final static String dataLENumberTxtName = "data-良恶性-数量-crop.txt";
	private final static String dicTxtName = "dic.txt";
	private final static String dicTxtName_old = "new_edition.txt";
	private static int n = 0;
	private static int nL = 0;
	private static int nE = 0;
	private static int nW = 0;
	
	private static int nn_crop = 0;

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
		
		if(fd.exists()){
			fd.delete();
		}
		
		if(!fd.exists())
		    fd.mkdirs();
		
		File fdT = new File(dataPath + dataTxtName);
		File fdTLE = new File(Configuration.dataDir_target + dataLETxtName);
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
	    	  
	    	  n++;
	    	  
	    	  String id = rs.getString("id");
	    	  String path = rs.getString("path");
	    	  String pathOrigion = rs.getString("path_origion");
	    	  String diagnose = rs.getString("diagnose");
	    	  String cropfactor = rs.getString("cropfactor");
	    	  String risk = rs.getString("risk");
	    	  String tirads = rs.getString("tirads");
	    	  
	    	  System.out.println(n + "\t" + id + "\t" + path + "\t" + pathOrigion + "\t" + cropfactor + "\t" + diagnose);
	    	  
	    	  //如果path的最后一个目录含有crop和region就说明这个图的原图已经是被裁剪过的小图
	    	  String strDirLast = path.trim();
	    	  strDirLast = strDirLast.substring(0, strDirLast.lastIndexOf("/"));
	    	  strDirLast = strDirLast.substring(strDirLast.lastIndexOf("/") + 1).trim();
	    	  
	    	  System.out.println(strDirLast);
	    	  
	    	  if(strDirLast.startsWith("region") && strDirLast.endsWith("_crop")){
	    		  System.out.println("region_crop");
	    		  nn_crop ++;
	    		  continue;
	    	  }

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
			fWriter.write("总数" + "\t" + "良性" + "\t" + "恶性" + "\t" + "未知" + "\t" + "crop_number" + "\r\n");
			fWriter.write(n + "\t" + nL + "\t" + nE + "\t" + nW + "\t" + nn_crop + "\r\n");
			fWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
