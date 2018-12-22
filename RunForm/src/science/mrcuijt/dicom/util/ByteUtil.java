/**
 * 
 * 
 * 创建时间：2018-12-21 上午11:11:06
 * @author：mrcuijt
 */
package science.mrcuijt.dicom.util;

/**
 * 
 * 
 * 创建时间：2018-12-21 上午11:11:06
 * @author mrcuijt
 * 
 */
public class ByteUtil {

	public static String concatBytes(byte[] bytes) {
		StringBuilder strb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			strb.append(bytes[i]);
			strb.append(",");
		}
		return strb.toString();
	}
}
