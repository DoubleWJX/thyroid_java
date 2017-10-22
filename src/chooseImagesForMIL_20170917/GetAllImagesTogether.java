package chooseImagesForMIL_20170917;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import functions.Configuration;
import tools.MyFileHelper;
import tools.MyImageHelper;

public class GetAllImagesTogether {
	/*
	 * 本类用来把所有文件夹下面的图片合并在一起
	 */
	
	public static String strDir = "D:/02 帅哥/06 多示例学习 甲状腺/11 我的数据集";
	public static String strDir_e = strDir + "/00 训练集 - 恶性数据";
	public static String strDir_l = strDir + "/00 训练集 - 良性数据/data - 文件夹和结果为良性或未知 - 去除颜色";
	
	public static String strDir_to = "F:/甲状腺数据/AllTogether";
	public static String strDir_e_to = strDir_to + "/bad";
	public static String strDir_l_to = strDir_to + "/good";
	public static String strResultTxt_l = strDir_to + "/dic_good.txt";
	public static String strResultTxt_e = strDir_to + "/dic_bad.txt";

	public static Map<String, Integer> mapSuffix = new HashMap<>();
	
	public static List<String> listDirs = new ArrayList<>();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		mainmain();
	}

	public static void mainmain(){
		
		initData();
		
		// 恶性数据
		listDirs.clear();
		listDirs.add(strDir_e);
		getAllDirs(strDir_e);
		getAllImages(listDirs, strDir_e_to, strResultTxt_e);
		
		// 良性数据
		listDirs.clear();
		listDirs.add(strDir_l);
		getAllDirs(strDir_l);
		getAllImages(listDirs, strDir_l_to, strResultTxt_l);
	}
	public static void initData(){
		mapSuffix.put(".jpg", 1);
		mapSuffix.put(".bmp", 1);
		
		File file = new File(strDir_e_to);
		if(!file.exists())
			file.mkdirs();
		file = new File(strDir_l_to);
		if(!file.exists())
			file.mkdirs();
		
		file = new File(strResultTxt_l);
		if(!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		file = new File(strResultTxt_e);
		if(!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public static void getAllImages(List<String> listDirs, String toDirs, String strResultTxt){
		
		String strXieru = "";
		int iXu = 0;
		for(int i = 0; i < listDirs.size(); ++i){
			String strDir = listDirs.get(i);
			System.out.println("当前文件夹：" + strDir);
			File file = new File(strDir);
	        if (file.exists()) {
	            File[] files = file.listFiles();
	            if (files.length == 0) {
	                System.out.println("文件夹是空的!");
	            } else {
	            	for (File file2 : files) {
	                    if (file2.isDirectory()){
	                    	
	                    } else {
	                    	String strFileName = file2.getName();
	                    	String strSuffix = strFileName.substring(strFileName.lastIndexOf(".")).toLowerCase();
	                    	if(mapSuffix.containsKey(strSuffix)){
	                    		iXu++;
	                    		String toPath = toDirs + "/" + iXu + strSuffix;
		                        System.out.println("文件: " + file2.getAbsolutePath() + "\t" + toPath);
		                        strXieru += iXu + "\t" + file2.getAbsolutePath() + "\t" + toPath + "\r\n";
		                        MyFileHelper.copyFile(file2.getAbsolutePath(), toPath);
	                    	}
	                    }
	                }
	            }
	        } else {
	            System.out.println("文件不存在!");
	        }
		}
		File fileR = new File(strResultTxt);
		FileWriter fw = null;
		try {
			fw = new FileWriter(fileR, false);
			
			fw.write(strXieru);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(fw != null)
				try {
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
	}
	
	public static void getAllDirs(String strDir){
		File file = new File(strDir);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files.length == 0) {
                System.out.println("文件夹是空的!");
                return;
            } else {
            	for (File file2 : files) {
                    if (file2.isDirectory() && !file2.getName().endsWith("_crop")) {
                        System.out.println("文件夹: " + file2.getAbsolutePath());
                        listDirs.add(file2.getAbsolutePath());
                        getAllDirs(file2.getAbsolutePath());
                    } else {
                        // System.out.println("文件:" + file2.getAbsolutePath());
                    }
                }
            }
        } else {
            System.out.println("文件不存在!");
        }
	}
}
