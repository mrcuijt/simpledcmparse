/**
 * 
 * 
 * 创建时间：2018-12-19 下午9:53:14
 * @author：mrcuijt
 */
package science.mrcuijt.dicom.entity;

/**
 * 
 * 
 * 创建时间：2018-12-19 下午9:53:14
 * @author mrcuijt
 * 
 */
public class DicomObject {

	private String elementGroup;

	private String vr;

	private String elementName;

	private String value;

	private boolean dcm4che2;

	private String currentSequenceInfo;

	private boolean sequence;

	/**
	 * @return the elementGroup
	 */
	public String getElementGroup() {
		return elementGroup;
	}

	/**
	 * @param elementGroup the elementGroup to set
	 */
	public void setElementGroup(String elementGroup) {
		this.elementGroup = elementGroup;
	}

	/**
	 * @return the vr
	 */
	public String getVr() {
		return vr;
	}

	/**
	 * @param vr the vr to set
	 */
	public void setVr(String vr) {
		this.vr = vr;
	}

	/**
	 * @return the elementName
	 */
	public String getElementName() {
		return elementName;
	}

	/**
	 * @param elementName the elementName to set
	 */
	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the dcm4che2
	 */
	public boolean isDcm4che2() {
		return dcm4che2;
	}

	/**
	 * @param dcm4che2 the dcm4che2 to set
	 */
	public void setDcm4che2(boolean dcm4che2) {
		this.dcm4che2 = dcm4che2;
	}

	/**
	 * @return the currentSequenceInfo
	 */
	public String getCurrentSequenceInfo() {
		return currentSequenceInfo;
	}

	/**
	 * @param currentSequenceInfo the currentSequenceInfo to set
	 */
	public void setCurrentSequenceInfo(String currentSequenceInfo) {
		this.currentSequenceInfo = currentSequenceInfo;
	}

	/**
	 * @return the sequence
	 */
	public boolean isSequence() {
		return sequence;
	}

	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(boolean sequence) {
		this.sequence = sequence;
	}

}
