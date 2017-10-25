package prepareDataForBing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tools.MyFileHelper;
import tools.MyImageHelper;

public class GetData {
	/*
	 * 该类用来把之前协和标注的数据存为voc的格式（也要注意能够方便的存为其他格式，比如后面万一用selective search去训练位置）
	 * 
	 * 该类得到的数据分为训练集和测试集，训练集用来训练位置，测试集评价预测的位置
	 * 
	 * 达到以下一些目的
	 * 1、能够修改训练集的数量，方便后期写文章的时候可以比较一下训练集数量对结果的影响
	 * 2、能够把测试集的标注文件里面的位置信息去掉，因为对于BING的开源c++代码我感觉预测出来的位置与标注的位置信息好像挺像的，但是不一样，不知道是不是算法本身很强大能够达到那样的效果
	 * 3、获取的数据应该去掉后面多示例训练的数据
	 */
	
	public static String origionDataTxt; //存放最开始协和标注数据整理后的数据，里面应该存放原图，小图，位置，特征以及良恶性，具体可以参照 D:\甲状腺标注\甲状腺标注数据（处理后）20170714文件夹里面，data-良恶性.txt
	public static String dirGoodTrain = "D:/02 帅哥/06 多示例学习 甲状腺/11 我的数据集/00 训练集 - 良性数据/data - 文件夹和结果为良性或未知 - 20170917"; //存放良性图片的文件夹，应该保证里面有一个data。txt文件
	public static String dirBadTrain = "D:/02 帅哥/06 多示例学习 甲状腺/11 我的数据集/00 训练集 - 恶性数据"; // 存放恶性文件夹的图片
	
	public static String dirTo = "F:/甲状腺数据/AllTogether/协和标注（去除多示例的数据）";
	public static String dirTo_imgs_good = dirTo + "/" + "imgs_good";
	public static String dirTo_locations_good = dirTo + "/" + "locations_good";
	public static String dirTo_imgs_bad = dirTo + "/" + "imgs_bad";
	public static String dirTo_locations_bad = dirTo + "/" + "locations_bad";
	
	public static String dicGood = dirTo + "/" + "dic_good.txt";
	public static String dicBad = dirTo + "/" + "dic_bad.txt";
	
	public static Map<String, String> mapGoodTrain = new HashMap<>(); // 存放良性数据的位置
	public static int nGoodTrain = 0;
	public static Map<String, String> mapBadTrain = new HashMap<>(); // 存放恶性数据的位置
	public static int nBadTrain = 0;
	public static List<String> listDirs = new ArrayList<>(); // 存放所有包含图片的文件夹
	public static Map<String, Integer> mapSuffix = new HashMap<>();
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		mainmain();
	}
	
	public static void mainmain() {
		init();
		
		/*
		 * 第一步：先把所有的图片拷贝到一起并记录下来
		 * 关于这一步，可以认为数据已经存在于：D:\甲状腺标注\甲状腺标注数据（处理后）20170714文件夹里面，data-良恶性.txt 里面存放了原图，小图，位置，特征以及良恶性
		 */
		
		origionDataTxt = "D:\\甲状腺标注\\甲状腺标注数据（处理后）20170714\\data-良恶性.txt";
		
		/*
		 * 第二步：把按照文件夹存放的良性图和恶性图的位置记录下来，方便在第三步进行筛选
		 */
		recordTrain();
				
		/*
		 * 第三步：把协和标注数据里面所有的数据（除了在按照文件夹得到的良性图和恶性图里面的数据）另存到其他地方
		 */
		getGoodBad();
	}
	
	public static void getGoodBad(){
		List<String> list = MyFileHelper.readFileTolist(origionDataTxt);
		Map<String, Integer> map = new HashMap<>();
		int nGood = 0, nBad = 0;
		int nGood1 = 0, nBad1 = 0;
		
		String strXieru_Good = "", strXieru_Bad = "";
		for(int i = 0; i < list.size(); ++i) {
			String str = list.get(i);
			String strs[] = str.split("\t");
			String imgPath = strs[2].trim();
			String imgType = strs[5].trim();
			String strPosition = strs[3].trim();
			System.out.println(imgPath + "\t" + imgType);
			
			File fileImage = new File(imgPath);
			int width = MyImageHelper.getImgWidth(fileImage);
			int height = MyImageHelper.getImgHeight(fileImage);
			
			if(width == -1 || height == -1) continue; // 说明图片出错
			
//			if(width != 1024 && height != 768){
//				
//				continue;
//			}
			
			String strPositions[] = strPosition.split(",");
			
			double dxmin = Double.parseDouble(strPositions[0]);
			double dymin = Double.parseDouble(strPositions[1]);
			double dxmax = Double.parseDouble(strPositions[2]);
			double dymax = Double.parseDouble(strPositions[3]);
			
			dxmin = dxmin < 0 ? 0 : dxmin; dymin = dymin < 0 ? 0 : dymin;
			dxmax = dxmax > 1 ? 1 : dxmax; dymax = dymax > 1 ? 1 : dymax;
			
			
						
			int nxmin = (int) (width * dxmin);
			int nymin = (int) (height * dymin);
			int nxmax = (int) (width * dxmax);
			int nymax = (int) (height * dymax);
			

			if(nxmin >= nxmax || nymin >= nymax) continue;
			
			
			nxmin = nxmin <= 0 ? 0 : nxmin;
			nymin = nymin <= 0 ? 0 : nymin;
			nxmax = nxmax >= width ? width - 1 : nxmax;
			nymax = nymax >= height ? height - 1 : nymax;
			
			strPosition = nxmin + "," + nymin + "," + nxmax + "," + nymax;
			
			if(imgType.equals("良性")){
				if(mapGoodTrain.containsKey(imgPath)) { // 在训练集里面，不写入数据
					nGood1++;
				} else{
					if(map.containsKey(imgPath)){
						
					}else {
						map.put(imgPath, 1);
						
						MyFileHelper.copyFile(imgPath, dirTo_imgs_good + "/good_" + nGood + ".jpg");
						File file = new File(dirTo_locations_good + "/good_" + nGood + ".txt");
						FileWriter fWriter;
						try {
							fWriter = new FileWriter(file);
							fWriter.write(strPosition.trim() + "\r\n");
							fWriter.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						strXieru_Good += nGood + "\t" + "good_" + nGood + ".jpg" + "\t" + imgPath + "\t" + strPosition + "\t" + imgType + "\r\n";
						
						nGood++;
					}
				}
			} else if(imgType.equals("恶性")) {
        		String strXiegangs[] = imgPath.split("/");
    			String strKey = strXiegangs[strXiegangs.length - 2] + "-" + strXiegangs[strXiegangs.length - 1];
				
    			if(mapBadTrain.containsKey(strKey)) { // 在训练集里面，不写入数据
    				nBad1++;
				} else{
					if(map.containsKey(imgPath)){
						
					}else {
						map.put(imgPath, 1);
						
						MyFileHelper.copyFile(imgPath, dirTo_imgs_bad + "/bad_" + nBad + ".jpg");
						File file = new File(dirTo_locations_bad + "/bad_" + nBad + ".txt");
						FileWriter fWriter;
						try {
							fWriter = new FileWriter(file);
							fWriter.write(strPosition.trim() + "\r\n");
							fWriter.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						strXieru_Bad += nBad + "\t" + "bad_" + nBad + ".jpg" + "\t" + imgPath + "\t" + strPosition + "\t" + imgType + "\r\n";
						
						nBad++;
					}
				}
			} else {
				
			}
		}
		
		File fGood = new File(dicGood), fBad = new File(dicBad);
		FileWriter fwGood, fwBad;
		try {
			fwGood = new FileWriter(fGood, false);
			fwGood.write(strXieru_Good);
			fwGood.close();
			
			fwBad = new FileWriter(fBad, false);
			fwBad.write(strXieru_Bad);
			fwBad.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(mapGoodTrain.size());
		System.out.println(nGoodTrain);
		System.out.println(nGood);
		System.out.println(nGood1);
		System.out.println(mapBadTrain.size());
		System.out.println(nBadTrain);
		System.out.println(nBad);
		System.out.println(nBad1);
	}
	
	public static void recordTrain(){ // 记录下后面要用的训练集合测试集
		
		recordTrainGood(dirGoodTrain);
		
		recordTrainBad(dirBadTrain);
	}
	
	public static void recordTrainGood(String strDir_l) {
		List<String> list = MyFileHelper.readFileTolist(strDir_l + "/" + "data.txt");
		for(int i = 0; i < list.size(); ++i) {
			String str = list.get(i);
			String strs[] = str.split("\t");
			String imgPath = strs[2].trim();
			mapGoodTrain.put(imgPath, "good");
			nGoodTrain++;
		}
	}
	
	public static void recordTrainBad(String strDir_e) {
		listDirs.clear();
		listDirs.add(strDir_e);
		getAllDirs(strDir_e); // 先获取所有图片所在文件夹
		getAllBadImagesFromBadTrain(listDirs);
	}
	
	private static void getAllBadImagesFromBadTrain(List<String> listDirs){
		
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
	            				
	                			mapBadTrain.put(strKey, "1");
	                			nBadTrain++;
	                    	}
	                    }
	                }
	            }
	        } else {
	            System.out.println("文件不存在!");
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
	
	public static void init() {
		// 进行一些初始化的工作
		mapSuffix.clear();
		mapSuffix.put(".jpg", 1);
		mapSuffix.put(".bmp", 1);
		
		File file = new File(dirTo);
		if(file.exists()){
			System.err.println(dirTo + " already exists !!!");
			System.exit(0);
		}
		
		file = new File(dirTo_imgs_good);
		file.mkdirs();
		file = new File(dirTo_locations_good);
		file.mkdirs();
		
		file = new File(dirTo_imgs_bad);
		file.mkdirs();
		file = new File(dirTo_locations_bad);
		file.mkdirs();
	}

}
