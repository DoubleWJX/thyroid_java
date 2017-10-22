package prepareDataForBing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import tools.MyImageHelper;

public class TestAddJuxingkuang {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String imgPath = "F:/甲状腺数据/AllTogether/MyVOC2007/JPEGImages/bad_1.jpg";
		String imgPathTo = "F:/甲状腺数据/AllTogether/MyVOC2007/JPEGImages/bad_1-r.jpg";
		List<ArrayList<Float>> listPostions = new ArrayList<>();

		addJuxingkuang(imgPath, imgPathTo, listPostions);
	}

	
	private static void addJuxingkuang(String imgFrom, String imgTo, List<ArrayList<Float>> listPostions){
		try {
			File file = new File(imgFrom);
			int width = 1;
			int height = 1;
			InputStream in = new FileInputStream(imgFrom);
			BufferedImage image = ImageIO.read(file);
	        Graphics g = image.getGraphics();
	        g.setColor(Color.green);//画笔颜色
	        
	        for(int ip = 0; ip < 1; ++ip){
				float fx1 = (float) 140.0; float fy1 = (float) 78.0;
				float fx2 = (float) 216.0; Float fy2 = (float) 126.0;
				
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
