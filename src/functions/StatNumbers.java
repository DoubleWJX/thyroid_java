package functions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class StatNumbers {

	/*
	 * 统计每个特征的图片的数量
	 * 
	 */
	
	private final static String dataLETxtName = "data-良恶性.txt";
	
	private final static String dataPath = Configuration.dataDir_target + "甲状腺数据-" + Configuration.strDate + "/";
	private final static String dicTxtName = "dic.txt";
	private final static int nAtLeast = 5000;
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		getStat();
	}

	public static void getStat(){
		ArrayList<String> list = getFeatures();

		Map<String, ArrayList<String>> map = getFeatureVals();
		Map<String, ArrayList<Integer>> mapNumber = getFeatureValNumbers();
		
		int numbers[] = new int[list.size()];
		for(int i = 0; i < numbers.length; i++){
		    System.out.print(list.get(i) + "\t");
			System.out.println(numbers[i]);
		}
		
		File file = new File(Configuration.dataDir_target + dataLETxtName);
		int nLast = 0;
		try {
			BufferedReader br= new BufferedReader(new FileReader(file));
			String str;
			
			while((str = br.readLine()) != null){
				String strs[] = str.trim().split("\t");
				if(strs[5].trim().equals("未知")){
					continue;
				}
				/*
				if(strs[5].trim().equals("良性")){
					continue;
				}
				*/
				
				String strFeature = strs[4];
				if(!strFeature.endsWith("0")){
					nLast++;
				}
				
				System.out.println(strFeature);
				String strFeatures[] = strFeature.split("");
				for(int j = 0; j < strFeatures.length; j++){
					String feature = list.get(j);
					int val = Integer.parseInt(strFeatures[j]);
					// System.out.println(j + "\t" + feature + "\t" + val);
					if(val != 0){
						numbers[j]++;
						
						ArrayList<Integer> featureNumer = mapNumber.get(feature);
						int number = featureNumer.get(val - 1) + 1;
						featureNumer.set(val - 1, number);
						mapNumber.put(feature, featureNumer);
					}
					
				}
			}
			
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i = 0; i < numbers.length; i++){
		    System.out.print(list.get(i) + "\t");
			System.out.println(numbers[i]);
		}

	    System.out.println("特征\t各个特征上的取值");
		for(int i = 0; i < numbers.length; i++){
		    System.out.print("\t" + list.get(i) + "\t");
			
			ArrayList<String> list1 = map.get(list.get(i));
			for(int j = 0; j < list1.size(); j++){
				System.out.print(list1.get(j) + "\t");
			}
			System.out.println();
			
			
			System.out.print("已标注\t" + numbers[i] + "\t");
			ArrayList<Integer> list2 = mapNumber.get(list.get(i));
			for(int j = 0; j < list2.size(); j++){
				System.out.print(list2.get(j) + "\t");
			}
			System.out.println();
			
			System.out.print("还需要\t" + (nAtLeast - numbers[i]) + "\t");
			for(int j = 0; j < list2.size(); j++){
				if(list2.get(j) >= nAtLeast){
					System.out.print("-\t");
				}else{
					System.out.print(nAtLeast - list2.get(j) + "\t");
				}
				
			}
			System.out.println();
		}
		
		System.out.println(nLast);
	}
	
	public static ArrayList<String> getFeatures(){
		File file = new File(dataPath + dicTxtName);
		
		ArrayList<String> list = new ArrayList<>();
		
		try {
			BufferedReader br= new BufferedReader(new FileReader(file));
			String str;
			while((str = br.readLine()) != null){
				String strs[] = str.split("\t");
				System.out.println(strs[0].trim());
				list.add(strs[0].trim());
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}
	
	public static Map<String, ArrayList<String>> getFeatureVals(){
		File file = new File(dataPath + dicTxtName);
		
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		
		try {
			BufferedReader br= new BufferedReader(new FileReader(file));
			String str;
			while((str = br.readLine()) != null){
				String strs[] = str.split("\t");
				System.out.println(strs[0].trim());
				
				ArrayList<String> list = new ArrayList<>();

				String vals[] = strs[1].split(";");
				
				for(int j = 0; j < vals.length; j++){
					list.add(vals[j].trim());
				}
				
				map.put(strs[0].trim(), list);
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return map;
	}
	
	
	public static Map<String, ArrayList<Integer>> getFeatureValNumbers(){
		File file = new File(dataPath + dicTxtName);
		
		HashMap<String, ArrayList<Integer>> map = new HashMap<String, ArrayList<Integer>>();
		
		try {
			BufferedReader br= new BufferedReader(new FileReader(file));
			String str;
			while((str = br.readLine()) != null){
				String strs[] = str.split("\t");
				System.out.println(strs[0].trim());
				
				ArrayList<Integer> list = new ArrayList<>();

				String vals[] = strs[1].split(";");
				
				for(int j = 0; j < vals.length; j++){
					list.add(0);
				}
				
				map.put(strs[0].trim(), list);
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return map;
	}
}
