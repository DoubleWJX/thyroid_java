package myFunctions;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import tools.MyFileHelper;
import tools.MyImageHelper;

public class a_biaozhujiejie {
	/*
	 * 本类用于将师姐标注的结节的位置标在图片上
	 */

	public static String dirPath = "F:/甲状腺数据/AllTogether"; // 图片的原始路径
	public static String txtPath = dirPath + "/wangjianxiong.txt"; // 标注结果文件，里面包含了每张图片的结节位置
	
	public static String imgType = "bad";
	public static String dirGoodFrom = dirPath + "/" + imgType;
	public static String dirGoodTo = dirPath + "/" + imgType+ "_to";
	
	public static String strSplit = "/home/dell/biggame/Wangjianxiong_20101012"; // 标注结果文件貌似是一个字符串，这个是分割字符，分割后能够得到每个图像的结果
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		init();
		mainmain();
	}
	private static void init(){
		File fDirTo = new File(dirGoodTo);
		if(!fDirTo.exists())
			fDirTo.mkdirs();
	}

	public static void mainmain(){
		List<String> list = MyFileHelper.readFileTolist(txtPath);
		System.out.println(list.size());
		int n = 0;
		for(int i = 0; i < list.size();){
			String strImgPath = list.get(i).trim();
			if(strImgPath.contains(strSplit)){
				if(strImgPath.contains(imgType) && !strImgPath.contains("_det")){
					System.out.println(strImgPath);
					++n;
					
					// 接下来应该有4行，分别是左上角和右下角
					List<ArrayList<Float>> listPostions = new ArrayList<>();
					int np = 0;
					for(int j = i + 1; j < list.size(); ++j){
						String strLocation = list.get(j).trim();
						i = j;
						if(strLocation.contains(strSplit)){
							break;
						}
						System.out.println("position = " + strLocation);
						String strPosition = strLocation.substring(strLocation.indexOf("array([") + "array([".length());
						strPosition = strPosition.substring(0, strPosition.indexOf("]"));
						
						if(strPosition.trim().equals("")){
							// 说明是空串
							break;
						}
						
						String strPositions[] = strPosition.split(",");
						for(int ip = 0; ip < strPositions.length; ++ip){
							float f = Float.parseFloat(strPositions[ip]);
							
							// System.out.println(f);
							if(listPostions.size() == ip){
								ArrayList<Float> listOnePosition = new ArrayList<>();
								listPostions.add(listOnePosition);
							}
							listPostions.get(ip).add(f);
						}
						++np;
					}
					if(np == 0){
						System.err.println("没有找到");
					}else if(np == 4){
						String imgName = strImgPath.substring(strImgPath.lastIndexOf("/") + 1);
						String imgPathLocal = dirGoodFrom + "/" + imgName;
						String imgPathLocalTo = dirGoodTo + "/" + imgName;
						System.out.println("imgPathLocal = " + imgPathLocal);
						System.out.println("imgPathLocalTo = " + imgPathLocalTo);
						for(int ip = 0; ip < listPostions.size(); ++ip){
							ArrayList<Float> listOnePosition = listPostions.get(ip);
							for(int ip1 = 0; ip1 < listOnePosition.size(); ++ip1){
								System.out.print(listOnePosition.get(ip1) + " ");
							}
							System.out.println();
						}
						
						addJuxingkuang(imgPathLocal, imgPathLocalTo, listPostions);
					}else{
						System.err.println("坐标的数量不是4");
						System.exit(-1);
					}					
					
				}else{
					++i;
				}
			}else{
				++i;
			}
		}
		System.out.println("n = " + n);
	}
	
	private static void addJuxingkuang(String imgFrom, String imgTo, List<ArrayList<Float>> listPostions){
		try {
			File file = new File(imgFrom);
			int width = MyImageHelper.getImgWidth(file);
			int height = MyImageHelper.getImgHeight(file);
			InputStream in = new FileInputStream(imgFrom);
			BufferedImage image = ImageIO.read(file);
	        Graphics g = image.getGraphics();
	        g.setColor(Color.RED);//画笔颜色
	        
	        for(int ip = 0; ip < listPostions.size(); ++ip){
				ArrayList<Float> listOnePosition = listPostions.get(ip);
				Float fx1 = listOnePosition.get(0); Float fy1 = listOnePosition.get(1);
				Float fx2 = listOnePosition.get(2); Float fy2 = listOnePosition.get(3);
				
				int x = (int) (fx1 * width), y = (int) (fy1 * height);
				int w = (int) (width * (fx2 - fx1)), h = (int) (height * (fy2 - fy1));
		        
		        g.drawRect(x, y, w, h);//矩形框(原点x坐标，原点y坐标，矩形的长，矩形的宽)
			}
	        //g.dispose();
	        FileOutputStream out = new FileOutputStream(imgTo);//输出图片的地址
	        ImageIO.write(image, "jpeg", out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//图片路径
    }
}
