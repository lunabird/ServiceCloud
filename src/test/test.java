package test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;

public class test {
	

	public void copyFile(File sourceFile , File targetFile) throws IOException{
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try{
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
			byte[] b = new byte[1024*8];
			int len;
			while((len = inBuff.read(b))!=-1){
				outBuff.write(b, 0, len);
			}
			outBuff.close();
		}finally{
			if(inBuff!=null) inBuff.close();
			if(outBuff!=null) outBuff.close();
		}
		System.out.println("OK!");
	}
	
	
//		
//	}
//	
	public static void main(String[] args) throws IOException {
//		JOptionPane.showMessageDialog(null, "ok!");
//		File source = new File("D:\\fxhqfwzx.war");
//		File target = new File("D:\\1\\fxhqfwzx.war");
		test t = new test();
		String path = "D:\\Automation.txt";
//		t.copyFile(source, target);
		
	}
	
}
