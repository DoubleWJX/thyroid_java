package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.security.auth.login.FailedLoginException;

public class MyFileHelper {
	
	public static String getHouzhuiByFileName(String strName){
		String houzhui = strName.substring(strName.lastIndexOf("."));
		return houzhui;
	}
	
	public static boolean copyFile(String pathFrom, String pathTo){
		
		String strFolderTo = pathTo.substring(0, pathTo.lastIndexOf("/"));
		File fileFolderTo = new File(strFolderTo);
		if(!fileFolderTo.exists()){
			fileFolderTo.mkdirs();
		}
		
		File ft = new File(pathTo);
		File ff = new File(pathFrom);
		if(ft.exists()){
			ft.delete();
		}
		
		
		// 复制文件  
        int byteread = 0; // 读取的字节数  
        InputStream in = null;  
        OutputStream out = null;  
  
        try {  
            in = new FileInputStream(ff);  
            out = new FileOutputStream(ft);  
            byte[] buffer = new byte[1024];  
  
            while ((byteread = in.read(buffer)) != -1) {  
                out.write(buffer, 0, byteread);  
            }  
            return true;  
        } catch (FileNotFoundException e) {  
            return false;  
        } catch (IOException e) {  
            return false;  
        } finally {  
            try {  
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
	}
	
	public static ArrayList<String> readFileTolist(String path){
		ArrayList<String> list = new ArrayList<>();
		
		File file = new File(path);
		String str;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			while((str = br.readLine()) != null){
				list.add(str);
			}
			
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}
}
