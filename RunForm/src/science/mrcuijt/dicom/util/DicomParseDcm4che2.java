/**
 * 
 * 
 * 创建时间：2018-9-8 下午7:34:08
 * @author：mrcuijt
 */
package science.mrcuijt.dicom.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.VR;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.util.TagUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.org.apache.xml.internal.resolver.helpers.Debug;

/**
 * 
 * 
 * 创建时间：2018-9-8 下午7:34:08
 * @author mrcuijt
 * 
 */
public class DicomParseDcm4che2 {

	private static final Logger LOGGER = LoggerFactory.getLogger(DicomParsePixelmed.class);

	private static String filePath = null;

	private static String encoding = "GB18030";

	private static StringBuffer strbWrite = null;

	public static boolean isWrite = false;
	
	public static List<String> messageList = null;

	/**
	 * 入口函数值
	 *
	 * 开发时间：2018-9-8 下午7:34:08
	 * @author：mrcuijt
	 * @param：args-传入的参数
	 * @return void
	 * @param args
	 */
	public static void main(String[] args) {
		filePath = "dicom/MR01.dcm";
		isWrite = true;
		messageList = new ArrayList<>();
		dicomParse(filePath);
	}

	/**
	 * 
	 * 关于 DICOM 文件的字符集
	 * 
	 * 标签为：(0008,0005)Specific Character Set 的数据元素值应该为： GB18030 或 ISO_IR 192 或 ISO_IR 100
	 * 
	 * GB18030 表示：汉子字符集
	 * 
	 * ISO_IR 192 表示：UNICODE 字符集 UTF-8
	 * 
	 * ISO_IR 100 表示：表示拉丁字符集
	 * 
	 * 而拉丁字符表只有(26)个
	 * Aa Bb Cc Dd Ee Ff Gg Hh Ii Jj Kk Ll Mm Nn Oo Pp Qq Rr Ss Tt Uu Vv Ww Xx Yy Zz
	 * 不能存储中文。故所有字符集都可以读取拉丁字符。
	 * 
	 * 所以说，DICOM 文件的字符编码在读取时，只有两种就足够了。分别是， GB18030 & ISO_IR 192
	 * 由于一般情况下，ISO_IR 192(UTF-8) 字符集标明的只能用 UTF-8 字符集集读取，
	 * 所以 ，在 Specific Character Set 非 ISO_IR 192 情况下剩下的编码一般都为 GB18030 格式读取。
	 * 
	 * 注：由于不规范原因，即使 Specific Character Set 是 ISO_IR 192 ，也要先进行解析验证一下。
	 * 
	 * 开发时间：2018-9-9 下午9:43:00
	 * @param filePath 
	 * @author：mrcuijt
	 */
	public static void dicomParse(String filePath){

		String split = "|";

		try {

			DicomInputStream dis = new DicomInputStream(new File(filePath));
			DicomObject dcmObj = dis.readDicomObject();
			// 由于 DicomElement getString 函数没有效果
			// 默认 DicomObject 使用的 getString 方式获取数据是的编码格式为 ISO-8859-1 
			// 故在读取非欧盟的数值时存在问题
			// 所以暂时解决方法为先获取到 DICOM 文件的 SpecificCharacterSet 解析字符集
			// 然后通过 DicomObject 的 getByte 方式获取到数据的字节，然后通过 new String 方式对数据进行重新构建
			// 先尝试所有文件都使用 UTF-8 字符集读取
			// String str = dcmElem.getString(scs, true);
			String specificCharacterSet = dcmObj.getString(Tag.SpecificCharacterSet);
			if (specificCharacterSet != null) {
				switch (specificCharacterSet) {
				case "ISO_IR 100":
				case "GB18030":
					encoding = "GBK";
					break;
				case "ISO_IR 192":
					encoding = "UTF-8";
					break;
				default:
					encoding = "GBK";
					break;
				}
			}

			strbWrite = new StringBuffer();
			printFormat(dcmObj, split);
			dicomParse(dcmObj, encoding, split);

			if (isWrite) {
				Writer fw = null;
				BufferedWriter bw = null;
				try {
					String sopInstanceUID = dcmObj.getString(Tag.SOPInstanceUID);
					File file = new File("./" + sopInstanceUID + "_dcm4che2.txt");
					fw = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
					bw = new BufferedWriter(fw);
					bw.write(strbWrite.toString());
					LOGGER.info("File save to {}", file.getAbsolutePath());
					messageList.add(file.getAbsolutePath());
					strbWrite = null;
					LOGGER.info("Read File Encode {}", encoding);
				} catch (Exception e) {
					LOGGER.error("DicomParse has error message: {}", e.getMessage(), e);
					e.printStackTrace();
				} finally {
					if (bw != null && fw != null) {
						bw.flush();
						fw.flush();
						bw.close();
						fw.close();
					}
				}
			}

			messageList = null;

		} catch (IOException e) {
			LOGGER.error("DicomParse has error message: {}", e.getMessage(), e);
			e.printStackTrace();
		}
	}

	private static void dicomParse(DicomObject dcmObj, String encoding, String split) {

		try {
			if (dcmObj == null)
				return;

			if (encoding == null)
				encoding = DicomParseDcm4che2.encoding;

			if (split == null)
				split = "";
			
//			printFormat(dcmObj, split);
			split += "\t|";
			
			Iterator<DicomElement> itr = dcmObj.datasetIterator();

			while (itr.hasNext()) {

				DicomElement dcmElem = itr.next();

				// 暂时跳过 DICOM 序列 Sequence 格式数据
				if (dcmElem.hasItems() || dcmElem.hasDicomObjects()) {
					int items = dcmElem.countItems();
//					printFormatForDicomELementSequence(dcmObj, dcmElem.tag(), encoding, split);
					if(items == 0){
						printFormatForDicomELementSequence(dcmObj, dcmElem.tag(), encoding, split);
						continue;
					}
					for (int i = 0; i < items; i++) {
						printFormatForDicomELementSequence(dcmElem.getDicomObject(i), dcmElem.tag(), encoding, split);
						dicomParse(dcmElem.getDicomObject(i), encoding, split);	
					}
					continue;
				}

				if (dcmElem.tag() == Tag.PixelData // 影像 DICOM 像素数据过滤解析
						|| dcmElem.tag() == Tag.LUTData // 影像 DICOM 像素数据过滤解析
						|| dcmElem.tag() == Tag.WaveformData) { // 心电 DICOM 波形数据过滤解析
					// 对像素数据以及波形数据不进行解析
					continue;
				}

				VR vr = dcmElem.vr();

				if (vr == VR.US || vr == VR.DS || vr == VR.UL) {// 由于这三种数据类型在使用 getByte 是会出异常故使用 getString 进行读取

					printFormatForDicomEementString(dcmObj, dcmElem.tag(), encoding, split);
					
					continue;
				}

				printFormatDicomEement(dcmObj,dcmElem.tag(),encoding,split);
			}
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("DicomParse has error message: {}", e.getMessage(), e);
			e.printStackTrace();
		}
	}

	// 对默认的 DicomEement 元素输出使用
	private static void printFormatDicomEement(DicomObject dcmObj,int dcmTag,String encoding,String split) throws UnsupportedEncodingException{
		printFormat(false, dcmObj, dcmTag, true, encoding, split);
	}

	// 针对特殊 DicomELement ，不能通过 byte[] 方式取数据时使用
	private static void printFormatForDicomEementString(DicomObject dcmObj,int dcmTag,String encoding,String split) throws UnsupportedEncodingException{
		printFormat(false, dcmObj, dcmTag, true, encoding, split);
	}

	// 对 DicomELementSequence 输出时使用
	private static void printFormatForDicomELementSequence(DicomObject dcmObj,int dcmTag,String encoding,String split) throws UnsupportedEncodingException{
		printFormat(true, dcmObj, dcmTag, true, encoding, split);
	}

	// 输出 DicomElement
	private static void printFormat(boolean isSequence,DicomObject dcmObj,int dcmTag,boolean useByte,String encoding,String split) throws UnsupportedEncodingException{

		boolean debug = LOGGER.isDebugEnabled();

		DicomObject parent = dcmObj.getParent();
		DicomElement dcmElem = dcmObj.get(dcmTag);
//		System.out.println(dcmElem);
		if(isSequence) {
			// dcmElem = parent.get(dcmTag);
			if(dcmElem == null){
				dcmElem = parent.get(dcmTag);
			}else if(!dcmObj.isRoot() && dcmElem.countItems() != 0){
				dcmElem = parent.get(dcmTag);
			}
		}
		StringBuffer strb = new StringBuffer();
		strb.append(split);
		strb.append(TagUtils.toString(dcmTag));
		strb.append("\t");
		strb.append("[");
		strb.append(dcmElem.vr());
		strb.append("]");
		strb.append("\t");
		strb.append("[");
		strb.append(dcmObj.nameOf(dcmTag));
		strb.append("]");
		strb.append("\t");
		if (isSequence) {
			strb.append("[");
			strb.append(dcmElem.countItems());
			strb.append("]");
			strb.append("[");
			// strb.append(dcmObj.getItemPosition()); // 这个 dcmObj 表示的是上层的 DICOMObject 不是本层的 DICOMObject
			// 如何获取当前 DicomElement 所在的 dcmObj ？
			strb.append(dcmObj.getItemPosition());
			strb.append("]");
			strb.append("[");
			if(!dcmObj.isRoot() && dcmElem.countItems() != 0){
				strb.append(dcmElem.getDicomObject(dcmObj.getItemPosition() - 1).size());
			}else{
				strb.append(dcmElem.countItems());
			}
			strb.append("]");
		}else{
			strb.append("\t\t");
			strb.append("[");
			if (useByte) {
				if(dcmElem.vr() == VR.US || dcmElem.vr() == VR.UL){
					strb.append(dcmObj.getInt(dcmTag));
				} else {
					strb.append(new String(dcmObj.getBytes(dcmTag,dcmObj.bigEndian()), encoding));
					if (debug) {
						LOGGER.debug("[{}] {}", new String(dcmObj.getBytes(dcmTag,dcmObj.bigEndian()), encoding), dcmObj.getBytes(dcmTag,dcmObj.bigEndian()));
					}
				}
			} else {
				strb.append(dcmObj.getString(dcmTag));
			}
			strb.append("]");

		}
//		String sutf = strb.toString();
//		String sutf = new String(strb.toString().getBytes("UTF-8"));
		String sutf = new String(strb.toString().getBytes("UTF-8"),"UTF-8");
		LOGGER.trace(sutf);
		messageList.add(sutf.replaceAll("\t", "    "));
		if (isWrite) {
			strbWrite.append(sutf);
			strbWrite.append("\r\n");
		}
	}

	private static void printFormat(DicomObject dcmObj,String split){
		StringBuffer strb = new StringBuffer();
		strb.append(split);
		strb.append("DicomObject");
		strb.append("\t");
		strb.append("elemCount[");
		strb.append(dcmObj.size());
		strb.append("]");

		LOGGER.trace(strb.toString());
		messageList.add(strb.toString().replaceAll("\t", "    "));
		if (isWrite) {
			strbWrite.append(strb.toString());
			strbWrite.append("\r\n");
		}
	}
}