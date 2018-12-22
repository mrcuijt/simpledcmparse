/**
 * 
 * 
 * 创建时间：2018-12-20 上午11:29:55
 * @author：mrcuijt
 */
package science.mrcuijt.dicom.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Properties;

/**
 * 
 * 
 * 创建时间：2018-12-20 上午11:29:55
 * @author mrcuijt
 * 
 */
public class EncodingTest {

	/**
	 * 入口函数值
	 *
	 * 开发时间：2018-12-20 上午11:29:55
	 * @author：mrcuijt
	 * @param：args-传入的参数
	 * @return void
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(getJVMEnconding());
		getJVMParams();
	}
	
	// 获得jvm的默认编码
    public static String getJVMEnconding() {
       return System.getProperty("file.encoding");
     }
    
    //获取JVM属性
    public static void  getJVMParams() {
       try {
           Properties properties=System.getProperties();
           PrintWriter out=null;
           out = new PrintWriter(new File("a.txt"));
           properties.list(out);
           out.flush();
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       }
   }

}
