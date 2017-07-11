package main;


import functions.*;

public class Main {
	
    

	public static void main(String[] args) {
		
		
		// 合并数据库
		// 合并之前请整理一下协和标注的数据，确保路径对头
		a0_MergeDb.mainmain();
		
		try {
			Thread.sleep(60 * 10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 找出正确的数据（所谓正确就是能找到小图和大图的数据，其他的没检查）
		b0_CheckDB.mainmain();
		
		try {
			Thread.sleep(60 * 10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 找出良恶性图
		GoodBadImages.mainmain();
		
		try {
			Thread.sleep(60 * 10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 找出良恶性图并分开
		GoodBadImages_Fenkai.mainmain();
	}
}
