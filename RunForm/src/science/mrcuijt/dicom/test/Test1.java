package science.mrcuijt.dicom.test;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import science.mrcuijt.dicom.util.ByteUtil;

public class Test1 {

	/*
	 * Java开发者必须牢记：
	 * 
	 * 	    在Java中字符仅以一种形式存在，那就是Unicode（不选择任何特定的编码，直接使用他们在字符集中的编号，这是统一的唯一方法）。
	 * 
	 * 由于java采用unicode编码，char 在java中占2个字节。2个字节（16位）来表示一个字符。
	 * 
	 * 	  JVM的折中约定使得一个字符分为两部分：JVM内部和OS的文件系统。
	 * 
	 * 在JVM内部，统一使用Unicode表示，当这个字符被从JVM内部移到外部（即保存为文件系统中的一个文件的内容时），就进行了编码转换，使用了具体的编码方案。
	 * 
	 * 因此可以说所有的编码转换只发生在边界的地方，JVM和OS的交界处，也就是各种输入/输出流（或者Reader，Writer类）起作用的地方。
	 * 
	 */
	
	/**
	 * 入口函数值
	 *
	 * 开发时间：2018-12-20 上午10:15:11
	 * @author：mrcuijt
	 * @param：args-传入的参数
	 * @return void
	 * @param args
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {

//		demo1();
		
//		demo2();
		
		demo3();
		
		ByteBuffer b = ByteBuffer.allocate(4);
		b.order(ByteOrder.LITTLE_ENDIAN);
		b.putInt(6309872);
		b.array();
		System.out.println(concatBytes(b.array()));
		
		String str = "你好";
		System.out.println(str);
		System.out.println(concatBytes(str.getBytes())); // default chaset UTF-8
		System.out.println(concatBytes(str.getBytes("UTF-8"))); // default chaset UTF-8
//		ByteBuffer b2 = ByteBuffer.wrap(new byte[]{-16,71,96,0});
		ByteBuffer b2 = ByteBuffer.allocate(4);
		b2.order(ByteOrder.BIG_ENDIAN);
		b2.put(new byte[]{-16,71,96,0});
		System.out.println(concatBytes(b2.array()));
		b2.order(ByteOrder.LITTLE_ENDIAN);
		System.out.println(concatBytes(b2.array()));
		
		System.out.println(new String(new byte[]{52,52,-53,-22},"GBK"));
		System.out.println(new String(new byte[]{52,52,-27,-78,-127}));
		System.out.println(ByteUtil.concatBytes(new String(new byte[]{-53,-22},"GBK").getBytes()));
//		try {
//			String str = "数学家";
//			System.out.println(str);
//			String temp2 = new String(str.getBytes("UTF-8"), "ISO-8859-1");
//			System.out.println(temp2);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
	}

	private static void demo4x() {
		String str = "数学";
		try {
			System.out.println(str);
			System.out.println("=======================");
			String temp1 = new String(str.getBytes("GBK"), "ISO-8859-1");
			System.out.println(temp1);
			temp1 = new String(temp1.getBytes("ISO-8859-1"), "GBK");
			System.out.println(temp1);
			System.out.println("=======================");
//			temp1 = new String(str.getBytes("GBK"), "ISO-8859-1");
//			temp1 = new String(temp1.getBytes("ISO-8859-1"), "UTF-8");
//			System.out.println(temp1);
			
			String temp2 = new String(str.getBytes("UTF-8"), "ISO-8859-1");
			System.out.println(temp2);
			temp2 = new String(temp2.getBytes("ISO-8859-1"), "UTF-8");
			System.out.println(temp2);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private static void demo3x() {
		try {
			String str2 = "姜丽娜";
			String temp = new String(str2.getBytes("UTF-8"),"ISO-8859-1");
			System.out.println(temp);
			System.out.println(new String(temp.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		try {
			String str3 = "姜丽娜";
			String temp = new String(str3.getBytes("GBK"), "ISO-8859-1");
			System.out.println("GBK\t" + temp);
			temp = new String(str3.getBytes("UTF-8"), "ISO-8859-1");
			System.out.println("UTF-8\t" + temp);
			System.out.println(new String(temp.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private static void demo3() {
		String gb2312 = "GB2312";
		String utf8 = "UTF-8";
		String iso_8859_1 = "ISO-8859-1";
		try {
			String str2 = "姜丽娜";
			System.out.println(concatBytes(str2.getBytes(iso_8859_1)));// 636363
			System.out.println(concatBytes(str2.getBytes(gb2312)));    // -67-86-64-10-60-56
			System.out.println(concatBytes(str2.getBytes(utf8)));      // -27-89-100-28-72-67-27-88-100
			String temp = new String(str2.getBytes(gb2312), iso_8859_1);
			System.out.println(temp); // ½ªÀöÄÈ
			System.out.println(concatBytes(temp.getBytes(iso_8859_1)));// -67-86-64-10-60-56
			System.out.println(concatBytes(temp.getBytes(gb2312)));    // 636363636363
			System.out.println(concatBytes(temp.getBytes(utf8)));      // -62-67-62-86-61-128-61-74-61-124-61-120
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private static void demo2() {
		try {
			String str2 = "姜丽娜";
			String temp = new String(str2.getBytes("UTF-8"),"ISO-8859-1");
			System.out.println(temp);
			System.out.println(new String(temp.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println("=========================");
		try {
			String str3 = "姜丽娜";
			System.out.println(str3);
			
			String temp = new String(str3.getBytes("GBK"), "ISO-8859-1");
			System.out.println("GBK\t" + temp);
			System.out.println("=========================");
			System.out.println(temp);
			temp = new String(temp.getBytes("UTF-8"));
			System.out.println("UTF-8\t" + temp);
			
//			System.out.println(new String(temp.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private static void demo1() {
		String str = "½ªÀöÄÈ";
		try {
			System.out.println(new String(str.getBytes("ISO-8859-1"),"GB2312"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String str2 = "姜丽娜";
		try {
			String temp = new String(str2.getBytes("GB2312"),"ISO-8859-1");
			System.out.println(temp);
			System.out.println(new String(temp.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public static String concatBytes(byte[] bytes) {
		StringBuilder strb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			strb.append(bytes[i]);
			strb.append(",");
		}
		return strb.toString();
	}
}
