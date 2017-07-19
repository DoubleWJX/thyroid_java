package functions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import tools.MyFileHelper;
import tools.MyImageHelper;
import tools.MyImageOperator;

public class Images_Labels_TXT {

	// private final static String dataPath = "D:/甲状腺标注/甲状腺标注数据20170525/甲状腺数据-20170525/";
	private final static String dataPath = Configuration.dataDir_target + "甲状腺数据-" + Configuration.strDate + "/";
	// private final static String dataResultPath = "D:/甲状腺标注/甲状腺标注数据20170525/甲状腺数据-按特征分开-20170525/";
	private final static String dataResultPath = Configuration.dataDir_target + "甲状腺数据-按特征分开-" + Configuration.strDate + "/";
	//private final static String dataResult_image_path = "D:/甲状腺标注/甲状腺标注数据20170525/MyDataset/Images";
	private final static String dataResult_image_path = Configuration.dataDir_target + "MyDataset/Images";
	//private final static String dataResult_small_image_path = "D:/甲状腺标注/甲状腺标注数据20170525/MyDataset/Images_small";
	private final static String dataResult_small_image_path = Configuration.dataDir_target + "MyDataset/Images_small";
	//private final static String dataResult_crop_image_path = "D:/甲状腺标注/甲状腺标注数据20170525/MyDataset/Images_small_my_crop";
	//private final static String dataResult_crop_image_path = Configuration.dataDir_target + "MyDataset/Images_small_my_crop";
	//private final static String dataResult_label_path = "D:/甲状腺标注/甲状腺标注数据20170525/MyDataset/Labels";
	private final static String dataResult_label_path = Configuration.dataDir_target + "MyDataset/Labels";
	private final static String dataTxtName = "data-crop.txt";
	
	
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
		
		File file1 = new File(dataResult_image_path);
		file1.mkdirs();
		
		File file2 = new File(dataResult_label_path);
		file2.mkdirs();
		
		File file3 = new File(dataResult_small_image_path);
		file3.mkdirs();
		
		/*
		File file4 = new File(dataResult_crop_image_path);
		file4.mkdirs();
		*/
	}

	public static void copyImages(){
		
		// 读取图片的数据
		ArrayList<String> list = MyFileHelper.readFileTolist(dataPath + dataTxtName);
		String str;
		String strs[];

		for(int i = 0; i < list.size(); i++){
			
			str = list.get(i);
			strs = str.split("\t");
			
			System.out.println(str);
			
			str = list.get(i);
			strs = str.split("\t");
			
			String strPosition = strs[3].trim();
			
			// System.out.println(list.size() + "\t" + strs[0] + "\t" + strs[1] + "\t" + strs[5]);
			
			File fileImage = new File(dataPath + strs[2]);
			int width = MyImageHelper.getImgWidth(fileImage);
			int height = MyImageHelper.getImgHeight(fileImage);
			
			System.out.println("size = " + "\t" + width + "\t" + height);
			
			if(width <= 0 || height <= 0){
				System.out.println("continue");
				continue;
			}
			
			String strPositions[] = strPosition.split(",");
			
			double dxmin = Double.parseDouble(strPositions[0]);
			double dymin = Double.parseDouble(strPositions[1]);
			double dxmax = Double.parseDouble(strPositions[2]);
			double dymax = Double.parseDouble(strPositions[3]);
			
			if(width != 1024 && height != 768){
				
				// break;
			}
			
			int nxmin = (int) (width * dxmin);
			int nymin = (int) (height * dymin);
			int nxmax = (int) (width * dxmax);
			int nymax = (int) (height * dymax);
			
			nxmin = nxmin < 0 ? 0 : nxmin;
			nymin = nymin < 0 ? 0 : nymin;
			nxmax = nxmax > width ? width - 1 : nxmax;
			nymax = nymax > height ? height - 1 : nymax;
			
			strPosition = nxmin + "," + nymin + "," + nxmax + "," + nymax;
			System.out.println(strPosition);
			
			
			if(strs[5].trim().equals("良性")){
				String strName111 = strs[2];
				if(strName111.indexOf("_") == 0){
					strName111 = strName111.replaceFirst("_", "").trim();
				}else{
					System.exit(-1);
				}
				MyFileHelper.copyFile(dataPath + strs[2], dataResult_image_path + "/" + strName111);		
				
				String strName = strName111;
				strName = strName.substring(0,  strName.lastIndexOf("."));
				File file = new File(dataResult_label_path + "/" + strName + ".txt");
				try {
					FileWriter fWriter = new FileWriter(file);
					fWriter.write("0" + " " + strPosition.replace(",", " "));
					fWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				MyFileHelper.copyFile(dataPath + strs[1], dataResult_small_image_path + "/" + strs[1]);
				
				/*
				MyImageOperator operateImage = new MyImageOperator(nxmin, nymin, nxmax - nxmin, nymax - nymin); 
			    operateImage.srcpath = dataPath + strs[2]; 
			    operateImage.subpath = dataResult_crop_image_path + "/" + strs[1]; 
			    try { 
			      operateImage.cut(); 
			    } catch (IOException e) { 
			      e.printStackTrace(); 
			    }
			    */
			}
			if(strs[5].trim().equals("恶性")){
				String strName111 = strs[2];
				if(strName111.indexOf("_") == 0){
					strName111 = strName111.replaceFirst("_", "").trim();
				}else{
					System.exit(-1);
				}
				MyFileHelper.copyFile(dataPath + strs[2], dataResult_image_path + "/" + strName111);
				
				String strName = strName111;
				strName = strName.substring(0,  strName.lastIndexOf("."));
				File file = new File(dataResult_label_path + "/" + strName + ".txt");
				try {
					FileWriter fWriter = new FileWriter(file);
					fWriter.write("1" + " " + strPosition.replace(",", " "));
					fWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				MyFileHelper.copyFile(dataPath + strs[1], dataResult_small_image_path + "/" + strs[1]);
				
				/*
				MyImageOperator operateImage = new MyImageOperator(nxmin, nymin, nxmax - nxmin, nymax - nymin); 
			    operateImage.srcpath = dataPath + strs[2]; 
			    operateImage.subpath = dataResult_crop_image_path + "/" + strs[1]; 
			    try { 
			      operateImage.cut(); 
			    } catch (IOException e) { 
			      e.printStackTrace(); 
			    }
			    */
			}
			
		}
		
	}
}
