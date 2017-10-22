package prepareDataForBing;

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
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		mainmain();
	}
	
	public static void mainmain() {
		init();
		
		
	}
	
	public static void init() {
		// 进行一些初始化的工作
	}

}
