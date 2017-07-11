package functions;

import java.io.File;
import java.util.ArrayList;

import tools.MyFileHelper;

public class CopyImagesIntoFolderByFeatureValue {

	private final static String dataPath = "D:/甲状腺标注/甲状腺标注数据20170525/甲状腺数据-20170525/";
	private final static String dataResultPath = "D:/甲状腺标注/甲状腺标注数据20170525/甲状腺数据-按特征分开-20170525/";
	private final static String dataResultPath_Good = "D:/甲状腺标注/甲状腺标注数据20170525/甲状腺数据-按特征分开-20170525/良性/";
	private final static String dataResultPath_Bad = "D:/甲状腺标注/甲状腺标注数据20170525/甲状腺数据-按特征分开-20170525/恶性/";
	private final static String dataTxtName = "data.txt";
	private final static String dicTxtName = "dic.txt";
	
	
	public static void main(String[] args) {
		mainmain();
	}
	
	public static void mainmain() {
		// TODO Auto-generated method stub
		long starttime = System.currentTimeMillis();
		/*
		 * 本类用于按照特征和取值把图片放进相应的文件夹
		 */
		
		initDatas();
		
		copyImages();
		
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

	public static void copyImages(){
		// 读取特征
		ArrayList<String> listFetures = MyFileHelper.readFileTolist(dataPath + dicTxtName);
		
		
		// 读取图片的数据
		ArrayList<String> list = MyFileHelper.readFileTolist(dataPath + dataTxtName);
		String str;
		String strs[];

		for(int i = 0; i < list.size(); i++){
			
			str = list.get(i);
			strs = str.split("\t");
			
			System.out.println(str);
			
			String strFeature = strs[4].trim();
			String strFeatures[] = strFeature.split("");
			
			if(strFeatures.length != listFetures.size()){
				System.exit(1);
				System.err.println("长度不一样");
			}
			
			for(int ifea = 0; ifea < strFeatures.length; ifea++){
				int nFea = Integer.parseInt(strFeatures[ifea]);
				
				String strFeature_Value = listFetures.get(ifea);
				String strFeature_Values[] = strFeature_Value.split("\t");
				String strFeatureName = strFeature_Values[0].trim();
				String strFeatureValue = strFeature_Values[1].trim();
				String strFeatureValues[] = strFeatureValue.split(";");
				//System.out.println(strFeatureValue + "\t" + strFeatureValues.length + "\t" + (nFea - 1));
				
				if(nFea == 0){
					
				}else{
					
					
					String strFeatureValue_name = strFeatureValues[nFea - 1].trim();
					
					// System.out.println(list.size() + "\t" + strs[0] + "\t" + strs[1] + "\t" + strs[5]);
					if(strs[5].trim().equals("良性")){
						MyFileHelper.copyFile(dataPath + strs[1], dataResultPath_Good + strFeatureName + "/" + strFeatureValue_name  + "/" + strs[1]);
					}
					if(strs[5].trim().equals("恶性")){
						MyFileHelper.copyFile(dataPath + strs[1], dataResultPath_Bad + strFeatureName + "/" + strFeatureValue_name  + "/" + strs[1]);
					}
					
				}
			}
			
			
		}
		
	}
}
