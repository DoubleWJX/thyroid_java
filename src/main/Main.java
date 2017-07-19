package main;


import functions.*;

public class Main {

	public static void main(String[] args) {
		long starttime = System.currentTimeMillis();
		
		// 合并数据库
		// 合并之前请整理一下协和标注的数据，确保路径对头
		a0_MergeDb.mainmain();

		// 找出正确的数据（所谓正确就是能找到小图和大图的数据，其他的没检查）
		b0_CheckDB.mainmain();

		
		// 找出良恶性图
		GoodBadImages.mainmain();
		
		// 找出良恶性图并分开
		GoodBadImages_Fenkai.mainmain();
		
		// 按照特征分开
		CopyImagesIntoFolderByFeatureValue.mainmain();
		
		// 
		GoodBadImages_rm_crop.mainmain();
		
		Images_Labels_TXT.mainmain();
		
		
		long endtime = System.currentTimeMillis();
		
		System.out.println("\n总计耗时： " + (endtime - starttime)/(1.0 * 1000 * 60) + " 分钟");
	}
}
