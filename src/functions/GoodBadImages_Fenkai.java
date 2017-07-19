package functions;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;

import tools.MyFileHelper;

public class GoodBadImages_Fenkai {

	private final static String dataPath = Configuration.dataDir_target + "甲状腺数据-" + Configuration.strDate + "/";
	
	private final static String dataResultPath = Configuration.dataDir_target + "甲状腺数据-按良恶性分开-" + Configuration.strDate + "/";
	private final static String dataResultPath_Good = Configuration.dataDir_target + "甲状腺数据-按良恶性分开-" + Configuration.strDate + "/良性/";
	private final static String dataResultPath_Bad = Configuration.dataDir_target + "甲状腺数据-按良恶性分开-" + Configuration.strDate + "/恶性/";
	private final static String dataTxtName = "data.txt";
	
	public static void main(String[] args) {
		mainmain();
	}
	
	public static void mainmain() {
		// TODO Auto-generated method stub
		long starttime = System.currentTimeMillis();
		/*
		 * 本类用于把良恶性的病灶图片分开，分别放在两个文件夹中
		 */
		
		initDatas();
		
		getGoodBadImages();
		
		long endtime = System.currentTimeMillis();
		
		System.out.println("\n总计耗时： " + (endtime - starttime)/(1.0 * 1000 * 60) + " 分钟");
	}
	
	public static void initDatas(){

		File file = new File(dataResultPath);
		if(file.exists()){
			file.delete();
		}
		
		File file1 = new File(dataResultPath_Good);
		file1.mkdirs();
		
		File file2 = new File(dataResultPath_Bad);
		file2.mkdirs();
		
	}

	public static void getGoodBadImages(){
		
		ArrayList<String> list = MyFileHelper.readFileTolist(dataPath + dataTxtName);
		String str;
		String strs[];
		for(int i = 0; i < list.size(); i++){
			str = list.get(i);
			strs = str.split("\t");
			
			System.out.println(list.size() + "\t" + strs[0] + "\t" + strs[1] + "\t" + strs[5]);
			if(strs[5].trim().equals("良性")){
				MyFileHelper.copyFile(dataPath + strs[1], dataResultPath_Good + strs[1]);
			}
			if(strs[5].trim().equals("恶性")){
				MyFileHelper.copyFile(dataPath + strs[1], dataResultPath_Bad + strs[1]);
			}
		}
		
	}
}
