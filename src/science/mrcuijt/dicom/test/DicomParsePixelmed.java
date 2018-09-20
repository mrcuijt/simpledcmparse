/**    
 * @Title: DicomParsePixelmed2.java  
 * @Package science.mrcuijt.dicom.test  
 * @Description: TODO  
 * @author mrcuijt
 * @date 2018年9月12日 下午2:42:31  
 * @version     
 */
package science.mrcuijt.dicom.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomDictionary;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.SequenceAttribute;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.ValueRepresentation;

/**  
 * @ClassName: DicomParsePixelmed2  
 * @Description: TODO  
 * @author mrcuijt
 * @date 2018年9月12日 下午2:42:31  
 *    
 */
public class DicomParsePixelmed {

	private static String dicomPath = "";
	
	private static DicomDictionary dicomDictionary = new DicomDictionary();
	
	private static StringBuffer strbWrite = new StringBuffer();

	private static boolean isWrite = false;
	
	public static void main(String[] args) {
		
		dicomPath = "D:/workspace/dcm4che2/doc/DICOMDIR/MR/1.3.46.670589.11.30293.5.20.1.1.9456.2017070110290515000_muti_frame_MR.dcm";
		dicomPath = "D:/workspace/dcm4che2/doc/evidence.dcm";
		isWrite = true;
		dcmParser();
	}

	public static void dcmParser() {
		
		String split = "|";
		
		AttributeList attrList = new AttributeList();
		
		try {
			
			attrList.read(new File(dicomPath));
			
			strbWrite.append(split);
			strbWrite.append("DICOM Object\t");
			strbWrite.append(attrList.keySet().size());
			strbWrite.append("\r\n");
				
			printAttributeList(attrList,split);
			
			if (isWrite) {
				
				
				
				FileWriter fw = null;
				BufferedWriter bw = null;
				try {
					String sopInstanceUID = null;
					Attribute attr = attrList.get(TagFromName.SOPInstanceUID);
					if(attr != null)
						sopInstanceUID = attrList.get(TagFromName.SOPInstanceUID).getSingleStringValueOrNull();
					File file = new File("./" + sopInstanceUID + "_pixelmed.txt");
					fw = new FileWriter(file);
					bw = new BufferedWriter(fw);
					bw.write(strbWrite.toString());
					System.out.println(file.getAbsolutePath());
				} catch (Exception e) {
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
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DicomException e) {
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
		StringBuffer strb = new StringBuffer();
		strb.append(split);
		strb.append(attr.getTag());
		strb.append("\t");
		strb.append(ValueRepresentation.getAsString(dicomDictionary.getValueRepresentationFromTag(attr.getTag())));
		strb.append("\t");
		strb.append(dicomDictionary.getFullNameFromTag(attr.getTag()));
		strb.append("\t");
		strb.append(attr.getSingleStringValueOrNull());
		strbWrite.append(strb.toString());
		strbWrite.append("\r\n");
		System.out.println(strb.toString());
	}
}
