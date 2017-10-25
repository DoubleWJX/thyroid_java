package prepareDataForBing;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileLockInterruptionException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import chooseImagesForMIL_20170917.GetAllImagesTogether;
import functions.Configuration;
import ij.IJ;
import ij.ImagePlus;
import tools.MyFileHelper;
import tools.MyImageHelper;

public class Main {
	
	/*
	 * 在运行本类之前，先运行GetData类保证得到了图片及其位置，即得到类似于：F:\甲状腺数据\AllTogether\协和标注（去除多示例的数据） 的文件
	 * 本类为 Mingming Chen的c++代码 BING 准备类似于Pascal voc 2007的数据
	 * 包含图片，训练集和测试集
	 * 
	 */
	
	public static int nnTrain = 1000;
	public static int nnTest = 400;
	public static int nTrainGood = nnTrain; // 训练集中良性图片的个数
	public static int nTrainBad = nnTrain; // 训练集中恶性图片的个数
	
	public static int nTestGood = nnTest; // 训练集中良性图片的个数
	public static int nTestBad = nnTest; // 训练集中恶性图片的个数
	
	public static String vocName = "MyVOC2007_20171025_" + nnTrain + "_" + nnTest;
	
	public static String dirMyVOC = "F:/甲状腺数据/AllTogether/" + vocName; // 处理之后的数据放到此处
	public static String dirJPEGImages = dirMyVOC + "/" + "JPEGImages";
	public static String dirAnnotations= dirMyVOC + "/" + "Annotations";
	public static String dirImageSets= dirMyVOC + "/" + "ImageSets";
	public static String dirMain= dirImageSets + "/" + "Main";
	public static String imgSuffix = ".jpg"; // 这个应该是voc要求的格式吧
	
	public static String dirData = "F:/甲状腺数据/AllTogether/协和标注（去除多示例的数据）（crop）";
	public static int imgHeight = 512; // 高度压缩为imgHeight个像素
	
	public static boolean writeTestImageLocaiton = true;
	
	public static Map<String, Integer> mapSuffix = new HashMap<>();
	
	public static String strTxtTrain = dirMain + "/" + "train.txt";
	public static String strTxtTest= dirMain + "/" + "test.txt";

	public static String strTxtData= dirMain + "/" + "data.txt";
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		mainmain();
	}
	
	public static void mainmain(){
		init();
		
		// 先从标注结果文件中读取所有图像的数据，并将相应的图像和数据保存在MyVOC2007中
		// getData();
		
		// 下面挑选训练集和测试集
		getTrainAndTest(dirData + "/imgs_bad", dirData + "/locations_bad", nTrainBad, nTestBad);
		getTrainAndTest(dirData + "/imgs_good", dirData + "/locations_good", nTrainGood, nTestGood);
		
		// 添加其他文件，比如class.txt
		MyFileHelper.writeStrToFile("thyroid\r\n", dirMain + "/" + "class.txt", false); // 添加class文件
	}
	
	
	private static void getTrainAndTest(String strDirImgs, String strDirLocations, int nTrain, int nTest){
		
		List<String> listImage = MyFileHelper.getAllFiles(strDirImgs); // 从这个文件夹获取所有图片
		if(nTrain > listImage.size()){
			System.err.println("nTrain > listImage.size()");
			System.exit(0);
		}
		String strXieruData = "";
		// 打乱，然后选择前面的图片，就当做随机选择了nTrain张图像
		Collections.shuffle(listImage);
		String strXieruTrain = "", strXieruTest = "";
		int nTestTmp = 0;
		int iTrue = 0;
		for(int i = 0; i < listImage.size(); ++i){
			String imgName = listImage.get(i);
			System.out.println(strDirImgs + "\t" + listImage.size() + "\t" + i + "\t" + imgName);
			String imgOnlyName = imgName.substring(0, imgName.lastIndexOf("."));
			String suffix = imgName.substring(imgName.lastIndexOf("."));
			
			String locationTxt = strDirLocations + "/" + imgOnlyName + ".txt";
			String strPositions[] = MyFileHelper.readFileTolist(locationTxt).get(0).trim().split(",");
			int xmin = Integer.parseInt(strPositions[0]);
			int ymin = Integer.parseInt(strPositions[1]);
			int xmax = Integer.parseInt(strPositions[2]);
			int ymax = Integer.parseInt(strPositions[3]);
			
			/*
			 * 复制图片，并将图片进行压缩
			 */			
			Image srcImg = null;
			try {
				srcImg = ImageIO.read(new FileInputStream(strDirImgs + "/" + imgName));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} //取源图

			double scale = imgHeight * 1.0 / srcImg.getHeight(null);
			int imgWidth = (int) (scale * srcImg.getWidth(null));
			
			
			xmin = (int) (xmin * scale); ymin = (int) (ymin * scale); xmax = (int) (xmax * scale); ymax = (int) (ymax * scale);
			xmin = xmin < 1 ? 1 : xmin;
			ymin = ymin < 1 ? 1 : ymin;
			xmax = xmax > imgWidth ? imgWidth : xmax;
			ymax = ymax > imgHeight ? imgHeight : ymax;
			
			if(xmin >= xmax || ymin >= ymax) {
				System.err.println("xmin >= xmax || ymin >= ymax");
				System.exit(0);
			}
			
			
			if(iTrue < nTrain){ // 对于训练集的图像
				strXieruTrain += imgOnlyName + "\r\n";
			} else { // 对于测试集的图像
				if(nTestTmp >= nTest) break;
				++nTestTmp;
				
				if(writeTestImageLocaiton == false) { // 表示不写入测试集的图像location
					xmin = 0; ymin = 0; xmax = 0; ymax = 0;
				}		
				strXieruTest += imgOnlyName + "\r\n";
			}
			
			Image smallImg = srcImg.getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH);
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(dirJPEGImages + "/" + imgName);
			} catch (FileNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}//输出图片的地址
			try {
				ImageIO.write(MyImageHelper.toBufferedImage(smallImg), "jpeg", out);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
					
			/*
			 * 写标注文件
			 */
			strXieruData += imgWidth + "\t" + imgHeight + "\t" + xmin + "\t" + ymin + "\t" + xmax + "\t" + ymax + "\r\n";
			String strFileContent = getTxtContent(imgName, imgWidth, imgHeight,
					xmin, ymin, xmax, ymax);
			String strFileName = dirAnnotations + "/" + imgOnlyName + ".yml";
			File file = new File(strFileName);
			FileWriter fWriter = null;
			try {
				fWriter = new FileWriter(file, false);
				fWriter.write(strFileContent);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				try {
					fWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			++iTrue;
		}
		
		MyFileHelper.writeStrToFile(strXieruTrain, strTxtTrain, true);
		MyFileHelper.writeStrToFile(strXieruTest, strTxtTest, true);
		MyFileHelper.writeStrToFile(strXieruData, strTxtData, true);
	}
	
	
	private static void init(){
		mapSuffix.clear();
		mapSuffix.put(".jpg", 1);
		mapSuffix.put(".bmp", 1);
		
		//处理结果文件夹，如果存在就删除，然后新建
		File fDirTo = new File(dirMyVOC);
		if(fDirTo.exists()){
			System.err.println(dirMyVOC + " already exists !!!");
			System.exit(0);
		}
		fDirTo.mkdirs();		
		fDirTo = new File(dirJPEGImages);
		fDirTo.mkdirs();
		fDirTo = new File(dirAnnotations);
		fDirTo.mkdirs();
		fDirTo = new File(dirMain);
		fDirTo.mkdirs();		
		

		File fileTrain = new File(strTxtTrain);
		File fileTest = new File(strTxtTest);
		if(fileTrain.exists())
			fileTrain.delete();
		if(fileTest.exists())
			fileTest.delete();
		File fileData = new File(strTxtData);
		if(fileData.exists())
			fileData.delete();
	}
	
	public static String getTxtContent(String fileName, int width, int height, int x1, int y1, int x2, int y2){
		
		String string = "%YAML:1.0" + "\r\n"
				+ "\r\n"
				+ "annotation:" + "\r\n"
				+ "  folder: " + vocName + "\r\n"
				+ "  filename: \"" + fileName + "\"\r\n"
				+ "  source: {database: The VOC2007 Database, annotation: PASCAL VOC2007, image: flickr," + "\r\n"
				+ "    flickrid: '341012865'}" + "\r\n"
				+ "  owner: {flickrid: Fried Camels, name: Jinky the Fruit Bat}" + "\r\n"
				+ "  size: {width: '" + width + "', height: '" + height + "', depth: '3'}" + "\r\n"
				+ "  segmented: '0'" + "\r\n"
				+ "  object:" + "\r\n";
				
		// 补充位置信息		
		string += "" 
				+ "    - bndbox: {xmin: '" + x1 + "', ymin: '" + y1 + "', xmax: '" + x2 + "', ymax: '" + y2 + "'}" + "\r\n"
				+ "      name: " + "thyroid" + "\r\n"
				+ "      pose: Left" + "\r\n"
				+ "      truncated: '1'" + "\r\n"
				+ "      difficult: '0'" + "\r\n";
		
		return string;
	}
}
