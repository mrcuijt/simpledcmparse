/**
 * 
 * 
 * 创建时间：2018-12-19 下午9:58:55
 * @author：mrcuijt
 */
package science.mrcuijt.dicom.form;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import science.mrcuijt.dicom.util.DicomParsePixelmed;

/**
 * 
 * 
 * 创建时间：2018-12-19 下午9:58:55
 * @author mrcuijt
 * 
 */
public class MainForm {
	private static final Logger LOGGER = LoggerFactory.getLogger(MainForm.class);
	private JFrame frame;
	private JTextField winFromFilePathJText;
	private JList winFromMessageList;
	private JScrollPane scrollPane;
	private Properties prop;

	/**  
	 * @Title: main  
	 * @Description: 
	 * @param args
	 * @return void
	 * @throws  
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainForm window = new MainForm();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainForm() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		prop = new Properties();
		frame = new JFrame();
		frame.setTitle("主窗体");
		frame.setBounds(100, 100, 612, 455);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		winFromFilePathJText = new JTextField();
		winFromFilePathJText.setBounds(36, 38, 399, 21);
		frame.getContentPane().add(winFromFilePathJText);
		winFromFilePathJText.setColumns(10);
		winFromFilePathJText.setEditable(false);

		// JList 不实现直接滚动。要创建一个滚动的列表，请将它作为 JScrollPane 的视口视图
		scrollPane = new JScrollPane();
		scrollPane.setBounds(36, 87, 521, 319);
		
		winFromMessageList = new JList();
		// JList 不实现直接滚动。要创建一个滚动的列表，请将它作为 JScrollPane 的视口视图
		scrollPane.setViewportView(winFromMessageList);
		frame.getContentPane().add(scrollPane);
		
		final JButton winFormSelectBtn = new JButton("请选择一个文件");
		winFormSelectBtn.setToolTipText("浏览");
		winFormSelectBtn.addActionListener(new ActionListener() {
			/**
			 * 按钮的激活事件
			 */
			public void actionPerformed(ActionEvent e) {

				String chooserPath = prop.getProperty("chooserPath");
				if (chooserPath == null) {
					chooserPath = "./";
				}
				JFileChooser fileJFileChooser = new JFileChooser(chooserPath);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("DICOM", "dcm");
				fileJFileChooser.setFileFilter(filter);
				int returnVal = fileJFileChooser.showSaveDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {

					File selectFile = fileJFileChooser.getSelectedFile();
					// System.out.println("You chose to open this file: " +
					// selectFile.getName());
					// JDialog jDialog = new JDialog();
					// jDialog.setBounds(100, 100, 200, 200);
					// jDialog.getContentPane().add(new
					// JTextField(selectFile.getName()));
					// jDialog.setVisible(true);
					LOGGER.info("Select File {}", selectFile.getAbsolutePath());
					winFromFilePathJText.setText(selectFile.getAbsolutePath());
					prop.setProperty("chooserPath", winFromFilePathJText.getText());
				}
			}
		});
		winFormSelectBtn.setBounds(439, 37, 21, 23);
		frame.getContentPane().add(winFormSelectBtn);

		JButton winFromFileLoadBtn = new JButton("加载");
		winFromFileLoadBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (winFromFilePathJText.getText().trim().length() <= 0) {
					winFormSelectBtn.doClick();
				} else {

					String filePath = winFromFilePathJText.getText().trim();
					LOGGER.info("Begin");
					File selectFile = new File(filePath);
					if (!selectFile.exists()) {

						JDialog jDialog = new JDialog();
						jDialog.setBounds(100, 100, 200, 200);
						jDialog.getContentPane().add(new JTextField("文件不存在！"));
						jDialog.setVisible(true);

						return;
					}

					try {
						List<String> messageList = new ArrayList<String>();
						DicomParsePixelmed.isWrite = true;
						DicomParsePixelmed.messageList = messageList;
						DicomParsePixelmed.dcmParser(filePath);
//						DicomParseDcm4che2.isWrite = true;
//						DicomParseDcm4che2.messageList = messageList;
//						DicomParseDcm4che2.dicomParse(filePath);
						String[] arryMsg = new String[messageList.size()];
						messageList.toArray(arryMsg);
						messageList = null;
						if (arryMsg != null && arryMsg.length > 0) {
							winFromMessageList.setListData(arryMsg);
							arryMsg = null;
						} else {

						}
						LOGGER.info("Finished");
					} catch (Exception e1) {
						e1.printStackTrace();
						JDialog jDialog = new JDialog();
						jDialog.setBounds(100, 100, 200, 200);
						jDialog.getContentPane().add(new JTextField("文件解析失败，错误信息为:" + e1.getMessage()));
						jDialog.setVisible(true);
					}
				}
			}
		});
		winFromFileLoadBtn.setBounds(464, 37, 93, 23);
		frame.getContentPane().add(winFromFileLoadBtn);
	}
}
