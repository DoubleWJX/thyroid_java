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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import chooseImagesForMIL_20170917.GetAllImagesTogether;
import functions.Configuration;
import tools.MyFileHelper;
import tools.MyImageHelper;

public class MainOld {
	
	/*
	 * 本类为 Mingming Chen的c++代码 BING 准备类似于Pascal voc 2007的数据
	 * 包含图片，训练集和测试集
	 * 
	 */
	
	public static String dirPath = "F:/甲状腺数据/AllTogether"; // 图片的原始路径
	public static String txtPath = dirPath + "/wangjianxiong.txt"; // 标注结果文件，里面包含了每张图片的结节位置
	
	public static String imgType = "bad"; // 表示现在处理恶性图片
	public static String dirFrom = dirPath + "/" + imgType;
	
	public static String strSplit = "/home/dell/biggame/Wangjianxiong_20101012"; // 标注结果文件貌似是一个字符串，这个是分割字符，分割后能够得到每个图像的结果
	
	public static String vocName = "MyVOC2007";
	public static String dirMyVOC = "F:/甲状腺数据/AllTogether/" + vocName; // 处理之后的数据放到此处
	public static String dirJPEGImages = dirMyVOC + "/" + "JPEGImages";
	public static String dirAnnotations= dirMyVOC + "/" + "Annotations";
	public static String dirImageSets= dirMyVOC + "/" + "ImageSets";
	public static String dirMain= dirImageSets + "/" + "Main";
	public static String imgSuffix = ".jpg"; // 这个应该是voc要求的格式吧
	
	public static int imgHeight = 512; // 高度压缩为256个像素
	
	
	public static int nTrainGood = 300; // 训练集中良性图片的个数
	public static int nTrainBad = 700; // 训练集中恶性图片的个数
	public static String strTrainTxtName = "thyroid_train.txt";
	public static String strTestTxtName = "thyroid_test.txt";
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		mainmain();
	}
	
	public static void mainmain(){
		// init();
		
		// 先从标注结果文件中读取所有图像的数据，并将相应的图像和数据保存在MyVOC2007中
		// getData();
		
		// 下面挑选训练集和测试集
		
		getTrainAndTest();
	}
	
	private static void getData(){
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
						String imgPathLocal = dirFrom + "/" + imgName;
						String imgOnlyName = imgName.substring(0, imgName.lastIndexOf("."));
						String imgPathLocalTo = dirJPEGImages + "/" + imgType + "_" + imgOnlyName  + imgSuffix;
						System.out.println("imgPathLocal = " + imgPathLocal);
						System.out.println("imgPathLocalTo = " + imgPathLocalTo);
						
												
						for(int ip = 0; ip < listPostions.size(); ++ip){
							ArrayList<Float> listOnePosition = listPostions.get(ip);
							for(int ip1 = 0; ip1 < listOnePosition.size(); ++ip1){
								System.out.print(listOnePosition.get(ip1) + " ");
							}
							System.out.println();
						}
						
						// 复制文件
//						MyFileHelper.copyFile(imgPathLocal, imgPathLocalTo);
//						File fileImg = new File(imgPathLocal);
//						String strFileContent = getTxtContent(imgOnlyName + imgSuffix,
//						MyImageHelper.getImgWidth(fileImg) , MyImageHelper.getImgHeight(fileImg), listPostions);

						Image srcImg = null;
						try {
							srcImg = ImageIO.read(new FileInputStream(imgPathLocal));
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}//取源图
						int imgWidth = imgHeight * srcImg.getWidth(null) / srcImg.getHeight(null);
						Image smallImg = srcImg.getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH);
						FileOutputStream out = null;
						try {
							out = new FileOutputStream(imgPathLocalTo);
						} catch (FileNotFoundException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}//输出图片的地址
						try {
							ImageIO.write(toBufferedImage(smallImg), "jpeg", out);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						
//						 写标注文件
						String strFileContent = getTxtContent(imgType + "_" + imgOnlyName + imgSuffix, imgWidth, imgHeight, listPostions);
						String strFileName = dirAnnotations + "/" + imgType + "_" + imgOnlyName + ".yml";
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
						
						
//						System.exit(0);
						
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
	
	public static String getTxtContent(String fileName, int width, int height, List<ArrayList<Float>> listPostions){
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
		for(int ip = 0; ip < listPostions.size(); ++ip){
			ArrayList<Float> listOnePosition = listPostions.get(ip);
			int x1 = Math.round(listOnePosition.get(0) * width); int y1 = Math.round(listOnePosition.get(1) * height);
			int x2 = Math.round(listOnePosition.get(2) * width); int y2 = Math.round(listOnePosition.get(3) * height);
			
			string += "" 
					+ "    - bndbox: {xmin: '" + x1 + "', ymin: '" + y1 + "', xmax: '" + x2 + "', ymax: '" + y2 + "'}" + "\r\n"
					+ "      name: " + "thyroid" + "\r\n"
					+ "      pose: Left" + "\r\n"
					+ "      truncated: '1'" + "\r\n"
					+ "      difficult: '0'" + "\r\n";
		}
		
		return string;
	}

	
	private static void getTrainAndTest(){
		
		List<String> listImage = getAllFiles(dirJPEGImages); // 从这个文件夹获取所有图片
		List<String> listImageGood = new ArrayList<>();
		List<String> listImageBad = new ArrayList<>();
		
		for(int i = 0; i < listImage.size(); ++i){
			String imgName = listImage.get(i);
			if(imgName.startsWith("good")){
				listImageGood.add(imgName);
			}else if(imgName.startsWith("bad")){
				listImageBad.add(imgName);
			} else{
				System.out.println(imgName + " is not right!!!");
				System.exit(0);
			}
		}
		
		// 随机生成训练集和测试集
		Map<String, Integer> map = new HashMap<>();
		Collections.shuffle(listImageGood); Collections.shuffle(listImageBad);
		
		for(int i = 0; i < listImageGood.size() && i < nTrainGood; ++i){
			map.put(listImageGood.get(i), 1);
		}
		for(int i = 0; i < listImageBad.size() && i < nTrainBad; ++i){
			map.put(listImageBad.get(i), 1);
		}
		
		String strXieru_Train = "";
		String strXieru_Test = "";
		String strXieru_Train_final = "", strXieru_Test_final = "";
		int nTrain = 0;
		for(int i = 0; i < listImage.size(); ++i){
			String imgName = listImage.get(i);
			strXieru_Train += imgName.substring(0, imgName.lastIndexOf("."));
			strXieru_Test += imgName.substring(0, imgName.lastIndexOf("."));
			if(map.containsKey(imgName)){
				strXieru_Train_final += imgName.substring(0, imgName.lastIndexOf(".")) + "\r\n";
				strXieru_Train += " " + "1";
				strXieru_Test += " " + "-1";
				++nTrain;
			}else{
				strXieru_Test_final += imgName.substring(0, imgName.lastIndexOf(".")) + "\r\n";
				strXieru_Train += " " + "-1";
				strXieru_Test += " " + "1";
			}
			strXieru_Train += "\r\n";
			strXieru_Test += "\r\n";
		}
		
		File file1 = new File(dirMain + "/" + strTrainTxtName);
		File file2 = new File(dirMain + "/" + strTestTxtName);
		
		File file1_final = new File(dirMain + "/" + "train.txt");
		File file2_final = new File(dirMain + "/" + "test.txt");
		File file_class = new File(dirMain + "/" + "class.txt");
		
		FileWriter fWriterTrain = null, fWriterTest = null, fWriterTrain_final = null, fWriterTest_final = null, fWriter_class = null;
		try {
			fWriterTrain = new FileWriter(file1);
			fWriterTest = new FileWriter(file2);
			fWriterTrain_final = new FileWriter(file1_final);
			fWriterTest_final = new FileWriter(file2_final);
			fWriter_class = new FileWriter(file_class);
			
			fWriterTrain.write(strXieru_Train);
			fWriterTest.write(strXieru_Test);
			
			fWriterTrain_final.write(strXieru_Train_final);
			fWriterTest_final.write(strXieru_Test_final);
			
			fWriter_class.write("thyroid\r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				fWriterTrain.close();
				fWriterTest.close();
				
				fWriterTrain_final.close();
				fWriterTest_final.close();
				
				fWriter_class.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("**** nTrain = " + nTrain);
	}
	private static List<String> getAllFiles(String strDir){
		List<String> listImage = new ArrayList<>();
		FilenameFilter ff= new FilenameFilter() {
			@Override
			public boolean accept(File arg0, String arg1) {
				// TODO Auto-generated method stub
				if(arg1.endsWith(imgSuffix))
					return true;
				return false;
			}
		};
		File fMulu = new File(strDir);
		File[] fList = fMulu.listFiles(ff);
		for(int j = 0; j < fList.length; j++){
			String imgName = fList[j].getName();
			listImage.add(imgName);
		}
		
		return listImage;
	}
	
	private static void init(){
		
		// 处理结果文件夹，如果存在就删除，然后新建
		File fDirTo = new File(dirMyVOC);
		if(!fDirTo.exists()){
			fDirTo.mkdirs();			
			fDirTo = new File(dirJPEGImages);
			fDirTo.mkdirs();
			fDirTo = new File(dirAnnotations);
			fDirTo.mkdirs();
			fDirTo = new File(dirMain);
			fDirTo.mkdirs();
		}
		
	}
	
	public static BufferedImage toBufferedImage(Image image) {  
        if (image instanceof BufferedImage) {  
            return (BufferedImage)image;  
         }  
        
        // This code ensures that all the pixels in the image are loaded  
         image = new ImageIcon(image).getImage();  
        
        // Determine if the image has transparent pixels; for this method's  
        // implementation, see e661 Determining If an Image Has Transparent Pixels  
        //boolean hasAlpha = hasAlpha(image);  
        
        // Create a buffered image with a format that's compatible with the screen  
         BufferedImage bimage = null;  
         GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();  
        try {  
            // Determine the type of transparency of the new buffered image  
            int transparency = Transparency.OPAQUE;  
           /* if (hasAlpha) { 
             transparency = Transparency.BITMASK; 
             }*/  
        
            // Create the buffered image  
             GraphicsDevice gs = ge.getDefaultScreenDevice();  
             GraphicsConfiguration gc = gs.getDefaultConfiguration();  
             bimage = gc.createCompatibleImage(  
             image.getWidth(null), image.getHeight(null), transparency);  
         } catch (HeadlessException e) {  
            // The system does not have a screen  
         }  
        
        if (bimage == null) {  
            // Create a buffered image using the default color model  
            int type = BufferedImage.TYPE_INT_RGB;  
            //int type = BufferedImage.TYPE_3BYTE_BGR;//by wang  
            /*if (hasAlpha) { 
             type = BufferedImage.TYPE_INT_ARGB; 
             }*/  
             bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);  
         }  
        
        // Copy image to buffered image  
         Graphics g = bimage.createGraphics();  
        
        // Paint the image onto the buffered image  
         g.drawImage(image, 0, 0, null);  
         g.dispose();  
        
        return bimage;  
    } 
}
