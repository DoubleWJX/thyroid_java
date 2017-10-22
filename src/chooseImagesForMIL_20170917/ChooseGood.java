package chooseImagesForMIL_20170917;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tools.MyFileHelper;
/*
 * 本类根据之前处理好的良恶性txt文件，从中读取文件夹为良性数据并且病灶小图判断为良性或者未知的数据
 */
public class ChooseGood {
	public static String strPath = "D:/02 帅哥/06 多示例学习 甲状腺/11 我的数据集/00 测试集 - 良性数据/data";
	public static String strResultTxt = strPath + "/data.txt";

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		mainmain();
	}

	public static void mainmain(){
		File file = new File(strResultTxt);
		if(file.exists()) file.delete();
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			FileWriter fw = new FileWriter(file);
			String strXieru = "";
			
			ArrayList<String> listData = MyFileHelper.readFileTolist(Configuration.strDataTxt);
			Map<String, Integer> map = new HashMap<>();
			int m = 0;
			for(int i = 0; i != listData.size(); ++i){
				String str = listData.get(i);
				String strs[] = str.split("\t");
				String strImageSmall = strs[1];
				String strImageOrigin = strs[2];
				String strType = strs[5];
				if(strImageSmall.contains("良性") && (strType.equals("良性") || strType.equals("未知"))){
					if(map.containsKey(strImageOrigin)){
						
					}else{
						++m;
						map.put(strImageOrigin, 1);
						System.out.println(m + "\t" + strImageSmall);
						MyFileHelper.copyFile(strImageOrigin, strPath + "/" + m + ".jpg");
						strXieru += m + "\t" + m + ".jpg" + "\t" + strImageOrigin + "\t" + strType + "\r\n";
						
						if(m % 100 == 0){
							fw.write(strXieru);
							strXieru = "";
						}
					}
				}
			}
			
			fw.write(strXieru);
			fw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
