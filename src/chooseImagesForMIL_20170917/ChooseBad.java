package chooseImagesForMIL_20170917;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tools.MyFileHelper;

public class ChooseBad {
	
	/*
	 * 此类用来选出已经指定的恶性文件夹中恶性的图片
	 * 
	 */
	
	public static String strDir = "D:/02 帅哥/06 多示例学习 甲状腺/11 我的数据集";
	public static String strDir_e = strDir + "/00 训练集 - 恶性数据";
	public static String strDir_e_to = "F:/甲状腺数据/AllTogether/bad（确定是bad）";
	public static String strResultTxt_e_to = strDir_e_to + "/dic_bad.txt"; // 记录一下原始路径和最新名字
	
	public static Map<String, Integer> mapSuffix = new HashMap<>();
	
	public static List<String> listDirs = new ArrayList<>(); // 存放所有包含图片的文件夹
	
	public static Map<String, String> mapImageType = new HashMap<>(); // 存放每个图片的良恶性，每个图片这个的key值需要包含路径和名字

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		init();
		mainmain();
	}
	private static void init(){
		mapSuffix.clear();
		mapSuffix.put(".jpg", 1);
		mapSuffix.put(".bmp", 1);
		
		File file = new File(strDir_e_to);
		if(!file.exists())
			file.mkdirs();
		
		file = new File(strResultTxt_e_to);
		if(!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	private static void mainmain(){
		listDirs.clear();
		listDirs.add(strDir_e);
		
		getImageType();
		
		getAllDirs(strDir_e); // 先获取所有图片所在文件夹
		
		getAllBadImages();
	}
	
	private static void getAllBadImages(){
		String strXieru = "";
		int iXu = 1;
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
	                    	// System.out.println("is directory");
	                    } else {
	                    	String strFileName = file2.getName();
	                    	String strSuffix = strFileName.substring(strFileName.lastIndexOf(".")).toLowerCase();
	                    	if(mapSuffix.containsKey(strSuffix)){
	                			System.out.println(file2.getAbsolutePath());
	                    		System.out.println(strFileName);
	                    		String strXiegangs[] = file2.getAbsolutePath().split("\\\\");
	                			String strKey = strXiegangs[strXiegangs.length - 2] + "-" + strXiegangs[strXiegangs.length - 1];
	            				
	            				if(mapImageType.containsKey(strKey)){
	            					String strV = mapImageType.get(strKey).split("@#@#@")[1];
	            					String strImageType = mapImageType.get(strKey).split("@#@#@")[0].trim();
		            				System.out.println(strV + "\t" + strImageType);
		            				
		            				if(strV.equals("1")){ // 只出现了一次，可以复制
		            					
		            					if(strImageType.equals("恶性")){
		            						String toPath = strDir_e_to + "/" + iXu + strSuffix;
			            					strXieru += iXu + "\t" + file2.getAbsolutePath() + "\t" + toPath + "\r\n";
					                        MyFileHelper.copyFile(file2.getAbsolutePath(), toPath);
			            					
					                        
					                        ++iXu;
		            					}		            					
		            				}
	            				}
	            				
	                    	}
	                    }
	                }
	            }
	        } else {
	            System.out.println("文件不存在!");
	        }
		}
		File fileR = new File(strResultTxt_e_to);
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
	
	public static void getImageType(){
		ArrayList<String> listData = MyFileHelper.readFileTolist(Configuration.strDataTxt);
		for(int i = 0; i < listData.size(); ++i){
			String str = listData.get(i);
			String strs[] = str.split("\t");
			String strImgOrigionPath = strs[2].trim();
			String strImgType = strs[5].trim();
			System.out.println(i + "\t" + strImgOrigionPath + "\t" + strImgType);
			
			String strXiegangs[] = strImgOrigionPath.split("/");
			String strKey = strXiegangs[strXiegangs.length - 2] + "-" + strXiegangs[strXiegangs.length - 1];
			if(mapImageType.containsKey(strKey)){
				System.err.println("key 重复");
				System.out.println(strKey);
				String strV = mapImageType.get(strKey).split("@#@#@")[1];
				
				mapImageType.put(strKey, strImgType + "@#@#@" + 2); // 如果重复就记录2次，这样之后判断如果是两次就不要了
			}else{
				mapImageType.put(strKey, strImgType + "@#@#@" + 1);
			}
		}
	}
}
