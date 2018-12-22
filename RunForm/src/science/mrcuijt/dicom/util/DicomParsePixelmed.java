/**    
 * @Title: DicomParsePixelmed2.java  
 * @Package science.mrcuijt.dicom.test  
 * @Description: TODO  
 * @author mrcuijt
 * @date 2018年9月12日 下午2:42:31  
 * @version     
 */
package science.mrcuijt.dicom.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomDictionary;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.SequenceAttribute;
import com.pixelmed.dicom.StringAttribute;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.UnknownAttribute;
import com.pixelmed.dicom.ValueRepresentation;

/**  
 * @ClassName: DicomParsePixelmed2  
 * @Description: TODO  
 * @author mrcuijt
 * @date 2018年9月12日 下午2:42:31  
 *    
 */
public class DicomParsePixelmed {

	private static final Logger LOGGER = LoggerFactory.getLogger(DicomParsePixelmed.class);

	private static String charset = null;
	
	private static String dicomPath = null;
	
	private static DicomDictionary dicomDictionary = new DicomDictionary();
	
	private static StringBuffer strbWrite = null;

	public static boolean isWrite = false;

	public static List<String> messageList = null;

	public static void main(String[] args) {
		dicomPath = "dicom/MR01.dcm";
		isWrite = true;
		messageList = new ArrayList<>();
		dcmParser(dicomPath);
	}

	public static void dcmParser(String dicomPath) {
		
		boolean debug = LOGGER.isDebugEnabled();
		
		String split = "|";
		
		AttributeList attrList = new AttributeList();

		try {

			attrList.read(new File(dicomPath));

			if(charset == null){

				if (debug) {
					LOGGER.debug("if charset == null {}", charset == null);
				}

				if (attrList.get(TagFromName.SpecificCharacterSet) != null
						&& attrList.get(TagFromName.SpecificCharacterSet).getSingleStringValueOrNull() != null) {

					if (debug) {
						LOGGER.debug("if attrList.get(TagFromName.SpecificCharacterSet) != null {}", attrList.get(TagFromName.SpecificCharacterSet) != null);
						LOGGER.debug("if attrList.get(TagFromName.SpecificCharacterSet).getSingleStringValueOrNull() != null {}", attrList.get(TagFromName.SpecificCharacterSet).getSingleStringValueOrNull() != null);
					}

					charset = attrList.get(TagFromName.SpecificCharacterSet).getSingleStringValueOrNull();

					switch (charset) {
					case "ISO_IR 100":
						if(debug){
							LOGGER.debug("Charset switch in case {}", charset);
						}
						charset = "GB18030";
						break;
					case "ISO_IR 192":
						if(debug){
							LOGGER.debug("Charset switch in case {}", charset);
						}
						charset = "UTF-8";
						break;
					default:
						if(debug){
							LOGGER.debug("Charset switch in default {}", charset);
						}
						charset = "GB18030";
						break;
					}

				} else {
					if (debug) {
						LOGGER.debug("else attrList.get(TagFromName.SpecificCharacterSet) != null {}", attrList.get(TagFromName.SpecificCharacterSet) != null);
						if (attrList.get(TagFromName.SpecificCharacterSet) != null) {
							LOGGER.debug("else attrList.get(TagFromName.SpecificCharacterSet).getSingleStringValueOrNull() != null {}", attrList.get(TagFromName.SpecificCharacterSet).getSingleStringValueOrNull() != null);
						}
					}
					charset = "GB18030";
					if (debug) {
						LOGGER.debug("use default charset {}", charset);
					}
				}
			}

			strbWrite = new StringBuffer();
			strbWrite.append(split);
			strbWrite.append("DICOM Object\t");
			strbWrite.append(attrList.keySet().size());
			strbWrite.append("\r\n");

			messageList.add(strbWrite.toString());

			printAttributeList(attrList, split);

			if (isWrite) {

				Writer fw = null;
				BufferedWriter bw = null;
				try {
					String sopInstanceUID = null;
					Attribute attr = attrList.get(TagFromName.SOPInstanceUID);
					if(attr != null)
						sopInstanceUID = attrList.get(TagFromName.SOPInstanceUID).getSingleStringValueOrNull();
					File file = new File("./" + sopInstanceUID + "_pixelmed.txt");
					fw = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
					bw = new BufferedWriter(fw);
					bw.write(strbWrite.toString());
					LOGGER.info("File save to {}", file.getAbsolutePath());
					messageList.add(file.getAbsolutePath());
					strbWrite = null;
					LOGGER.info("Read File Encode {}", charset);
					charset = null;
				} catch (Exception e) {
					LOGGER.error("DcmParser has error message: {}", e.getMessage(), e);
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
			LOGGER.error("DcmParser has error message: {}", e.getMessage(), e);
			e.printStackTrace();
		} catch (DicomException e) {
			LOGGER.error("DcmParser has error message: {}", e.getMessage(), e);
			e.printStackTrace();
		}
	}

	public static void printAttributeList(AttributeList attrList, String split) {
		
		if(split == null) split = "";
		
		split += "\t|";
		
		Set<AttributeTag> attrTagSet = attrList.keySet();
		for (AttributeTag attrTag : attrTagSet) {
			Attribute attr = attrList.get(attrTag);
			if (SequenceAttribute.class.isInstance(attr)) {
				SequenceAttribute sequenceAttribute = (SequenceAttribute) attr;
				int itemCounts = sequenceAttribute.getNumberOfItems();
				if(itemCounts == 0){
					printAttr(attr,split);
					continue;
				}
				for (int i = 0; i < itemCounts; i++) {
					printAttr(attr,split);
					printAttributeList(SequenceAttribute.getAttributeListFromSelectedItemWithinSequence(sequenceAttribute, i),split);
				}
			}else{
				printAttr(attr,split);
			}
		}
	}

	public static void printAttr(Attribute attr, String split) {

		boolean debug = LOGGER.isDebugEnabled();

		StringBuffer strb = new StringBuffer();
		strb.append(split);
		strb.append(attr.getTag());
		strb.append("\t");
		strb.append("[");
		strb.append(ValueRepresentation.getAsString(dicomDictionary.getValueRepresentationFromTag(attr.getTag())));
		strb.append("]");
		strb.append("\t");
		strb.append("[");
		strb.append(dicomDictionary.getFullNameFromTag(attr.getTag()));
		strb.append("]");
		strb.append("\t");
		strb.append("[");

		if (charset != null) {
//			if (attr instanceof LongStringAttribute
//					|| attr instanceof LongTextAttribute
//					|| attr instanceof ShortStringAttribute) {
			if(attr instanceof StringAttribute){
				try {

					if (attr.getByteValues() != null) {
						strb.append(new String(attr.getByteValues(), charset));
						if (debug) {
							LOGGER.debug("[{}] {}", new String(attr.getByteValues(), charset), attr.getByteValues());
						}
					} else {
						strb.append((attr.getSingleStringValueOrNull()));
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (DicomException e) {
					e.printStackTrace();
				}
			} else {
				if (attr instanceof UnknownAttribute
						&& dicomDictionary.getFullNameFromTag(attr.getTag()) != null
						&& dicomDictionary.getFullNameFromTag(attr.getTag()).equalsIgnoreCase("Group Length")) {
					try {
						ByteBuffer wrapped = ByteBuffer.wrap(attr.getByteValues());
						wrapped.order(ByteOrder.LITTLE_ENDIAN);
						strb.append(wrapped.getInt());
					} catch (DicomException e) {
						e.printStackTrace();
					}
				} else {
					strb.append((attr.getSingleStringValueOrNull()));
				}
			}
//			strb.append((attr.getClass().getName()));
		} else {
			strb.append((attr.getSingleStringValueOrNull()));
		}

		strb.append("]");

		if (messageList != null)
			messageList.add(strb.toString().replaceAll("\t", "    "));

		try {
//			String sutf = strb.toString();
//			String sutf = new String(strb.toString().getBytes("UTF-8"));
			String sutf = new String(strb.toString().getBytes("UTF-8"),"UTF-8");
			strbWrite.append(sutf);
			strbWrite.append("\r\n");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("PrintAttr has error message: {}", e.getMessage(), e);
			e.printStackTrace();
		}
		LOGGER.trace(strb.toString());
	}
}
