package testDiagnosis;

public class main {
	
	/*
	 * 本类用来测试深度诊断程序
	 * 
	 * 本类首先提取出所有要诊断的图像以及他们的良恶性，把图像放在一个文件夹，良恶性标记以及与图像的对应放在另一个txt文件中
	 * 然后把这个文件夹交给深度学习的程序员，他们测试诊断之后会返回一个csv文件
	 * 
	 * 然后运行之前的统计脚本就能测试出
	 */

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		a0_MergeDb.mainmain();
		
		b0_CheckDB.mainmain();
		
		GoodBadImages.mainmain();
		
	}

}
