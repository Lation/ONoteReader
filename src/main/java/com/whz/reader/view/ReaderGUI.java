package com.whz.reader.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Locale;
import java.util.logging.Logger;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.whz.reader.controller.ReaderController;
import com.whz.reader.util.DirectoryFilter;
import com.whz.reader.util.I18N;
import com.whz.reader.util.ImageIconReader;
import com.whz.reader.util.InputValidator;

/**
 * The Graphical User Interface of the project which contains all the necessary
 * objects and components for the user to interact with to automatically
 * generate java code for a project with the given JSON file.
 * 
 * The user has to specify the directory of the project and select the JSON file
 * from with the Java code will be generated.
 * 
 * @author Timon Schwalbe
 */
public class ReaderGUI extends JPanel implements ActionListener {

	private static final Logger log = Logger.getLogger(ReaderGUI.class.getName());

	private static final String APPLICATION_NAME = "oNote Reader";
	private static final String PROJECT_ICON = "images/ONoteReader_icon.png";
	private static final String FOLDER_ICON = "images/folder_icon.png";

	private final ReaderController controller;

	private static JFrame frame;
	private JPanel panel;

	private final String[] languageOptions = { "EN", "DE" };
	private JComboBox<String> languageComboBox;

	private JLabel headlineLabel;
	private JLabel descriptionLabel;

	private int previousIndex;
	private JLabel projectChooseLabel;
	private String[] projectOptions = { I18N.resourceBundle.getString("general.projectOptions.index0"),
			I18N.resourceBundle.getString("general.projectOptions.index1") };
	private JComboBox<String> projectComboBox;
	private JLabel projectPathLabelNew;
	private JLabel projectPathLabelExisting;
	private JTextField projectPathTextField;
	private JButton projectPathButton;

	private JLabel jsonPathLabel;
	private JTextField jsonPathTextField;
	private JButton jsonPathButton;

	private JLabel namespaceChooseLabel;
	private String[] namespaceOptions = { I18N.resourceBundle.getString("general.namespaceOptions.index0"),
			I18N.resourceBundle.getString("general.namespaceOptions.index1"),
			I18N.resourceBundle.getString("general.namespaceOptions.index2") };
	private JComboBox<String> namespaceComboBox;
	private JLabel namespaceDeclareLabel;
	private JTextField namespaceTextField;

	private JButton codeGenButton;

	private final int windowWidth = 600;
	private final int windowHeight = 380;
	private final int labelWidth = 156;
	private final int scrollPaneHeight = 40;
	private final int pathButtonWidth = 38;
	private final int pathButtonHeight = 32;
	private final int comboBoxHeight = 26;
	private final int comboBoxWidth = 54;
	private final int textFieldHeight = 24;
	private final int buttonWidth = 160;

	/**
	 * Initializes and generated the GUI of the application with all its necessary
	 * components.
	 * 
	 * Initializes the ReaderController via constructor injection. This focuses on
	 * the GRASP pattern where the UI itself is responsible for action listeners
	 * since these reside inside the graphical interface (for example a button). Any
	 * logic however is passed onto the controller where the actual processing is
	 * done.
	 */
	public ReaderGUI(ReaderController controller) {
		this.controller = controller;

		// basic settings
		frame = new JFrame(APPLICATION_NAME);
		frame.setIconImage(ImageIconReader.readImage(PROJECT_ICON));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(windowWidth, windowHeight);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setBackground(Color.WHITE);

		panel = new JPanel();
		frame.add(panel);
		GroupLayout layout = new GroupLayout(panel);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		panel.setLayout(layout);

		SequentialGroup verticalGroup = layout.createSequentialGroup();
		ParallelGroup horizontalGroup = layout.createParallelGroup();

		// Component group for language, headline and description
		languageComboBox = new JComboBox<>(languageOptions);
		languageComboBox.addActionListener(this);
		languageComboBox.setBackground(frame.getBackground());

		headlineLabel = new JLabel(I18N.resourceBundle.getString("general.headlineLabel"));

		descriptionLabel = new JLabel(I18N.resourceBundle.getString("general.descriptionLabel"));

		// Component group for choosing project path
		projectChooseLabel = new JLabel(I18N.resourceBundle.getString("general.projectChooseLabel"));

		previousIndex = 0;
		projectComboBox = new JComboBox<>(projectOptions);
		projectComboBox.setBackground(frame.getBackground());
		projectComboBox.addActionListener(this);

		projectPathLabelNew = new JLabel(I18N.resourceBundle.getString("general.projectPathLabelNew"));
		projectPathLabelExisting = new JLabel(I18N.resourceBundle.getString("general.projectPathLabelExisting"));
		projectPathLabelExisting
				.setToolTipText(I18N.resourceBundle.getString("general.projectPathLabelExisting.tooltip"));
		projectPathLabelExisting.setVisible(false);

		projectPathTextField = new JTextField();
		JScrollPane projectPathScrollPane = new JScrollPane(projectPathTextField, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		projectPathButton = new JButton();
		projectPathButton.setIcon(new ImageIcon(ImageIconReader.readImage(FOLDER_ICON)));
		projectPathButton.setBackground(frame.getBackground());
		projectPathButton.addActionListener(this);

		// Component group for choosing JSON file path
		jsonPathLabel = new JLabel(I18N.resourceBundle.getString("general.jsonPathLabel"));

		jsonPathTextField = new JTextField();
		JScrollPane jsonPathScrollPane = new JScrollPane(jsonPathTextField, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		jsonPathButton = new JButton();
		jsonPathButton.setIcon(new ImageIcon(ImageIconReader.readImage(FOLDER_ICON)));
		jsonPathButton.setBackground(frame.getBackground());
		jsonPathButton.addActionListener(this);

		// component group for choosing namespace
		namespaceChooseLabel = new JLabel(I18N.resourceBundle.getString("general.namespaceChooseLabel"));

		namespaceComboBox = new JComboBox<>(namespaceOptions);
		namespaceComboBox.setBackground(frame.getBackground());
		namespaceComboBox.addActionListener(this);

		namespaceDeclareLabel = new JLabel(I18N.resourceBundle.getString("general.namespaceDeclareLabel"));
		namespaceDeclareLabel.setToolTipText(I18N.resourceBundle.getString("general.namespaceDeclareLabel.tooltip"));
		namespaceTextField = new JTextField();

		// GroupLayout
		verticalGroup.addGroup(layout.createParallelGroup(Alignment.CENTER).addComponent(headlineLabel)
				.addComponent(languageComboBox, comboBoxHeight, comboBoxHeight, comboBoxHeight));
		verticalGroup.addComponent(descriptionLabel).addGap(10, 10, 10);
		verticalGroup.addGroup(layout.createParallelGroup(Alignment.CENTER).addComponent(projectChooseLabel)
				.addComponent(projectComboBox, comboBoxHeight, comboBoxHeight, comboBoxHeight));
		verticalGroup.addGroup(layout.createParallelGroup(Alignment.CENTER).addComponent(projectPathLabelNew)
				.addComponent(projectPathLabelExisting)
				.addComponent(projectPathScrollPane, scrollPaneHeight, scrollPaneHeight, scrollPaneHeight)
				.addComponent(projectPathButton, pathButtonHeight, pathButtonHeight, pathButtonHeight));
		verticalGroup.addGroup(layout.createParallelGroup(Alignment.CENTER).addComponent(jsonPathLabel)
				.addComponent(jsonPathScrollPane, scrollPaneHeight, scrollPaneHeight, scrollPaneHeight)
				.addComponent(jsonPathButton, pathButtonHeight, pathButtonHeight, pathButtonHeight));
		verticalGroup.addGroup(layout.createParallelGroup(Alignment.CENTER).addComponent(namespaceChooseLabel)
				.addComponent(namespaceComboBox, comboBoxHeight, comboBoxHeight, comboBoxHeight));
		verticalGroup.addGroup(layout.createParallelGroup(Alignment.CENTER).addComponent(namespaceDeclareLabel)
				.addComponent(namespaceTextField, textFieldHeight, textFieldHeight, textFieldHeight));

		horizontalGroup.addGroup(layout.createSequentialGroup().addComponent(headlineLabel)
				.addComponent(languageComboBox, comboBoxWidth, comboBoxWidth, comboBoxWidth));
		horizontalGroup.addComponent(descriptionLabel);
		horizontalGroup.addGroup(layout.createSequentialGroup()
				.addComponent(projectChooseLabel, labelWidth, labelWidth, labelWidth).addComponent(projectComboBox));
		horizontalGroup.addGroup(
				layout.createSequentialGroup().addComponent(projectPathLabelNew, labelWidth, labelWidth, labelWidth)
						.addComponent(projectPathLabelExisting, labelWidth, labelWidth, labelWidth)
						.addComponent(projectPathScrollPane)
						.addComponent(projectPathButton, pathButtonWidth, pathButtonWidth, pathButtonWidth));
		horizontalGroup.addGroup(layout.createSequentialGroup()
				.addComponent(jsonPathLabel, labelWidth, labelWidth, labelWidth).addComponent(jsonPathScrollPane)
				.addComponent(jsonPathButton, pathButtonWidth, pathButtonWidth, pathButtonWidth));
		horizontalGroup.addGroup(
				layout.createSequentialGroup().addComponent(namespaceChooseLabel, labelWidth, labelWidth, labelWidth)
						.addComponent(namespaceComboBox));
		horizontalGroup.addGroup(
				layout.createSequentialGroup().addComponent(namespaceDeclareLabel, labelWidth, labelWidth, labelWidth)
						.addComponent(namespaceTextField));

		// Code generation button
		codeGenButton = new JButton(I18N.resourceBundle.getString("general.codeGenButton"));
		codeGenButton.addActionListener(this);
		frame.getRootPane().setDefaultButton(codeGenButton);
		verticalGroup.addGap(0, 0, Short.MAX_VALUE).addComponent(codeGenButton);
		horizontalGroup.addGroup(layout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE)
				.addComponent(codeGenButton, buttonWidth, buttonWidth, buttonWidth));

		layout.setVerticalGroup(verticalGroup);
		layout.setHorizontalGroup(horizontalGroup);

		frame.setVisible(true);
		log.info("Finished initializing GUI");
	}

	/**
	 * Calls the GUI constructor to initialize and generate the GUI of the
	 * application.
	 * 
	 * The static method enables the call and generation of this class from outside
	 * (e.g. the main class) without the need to generate a redundant object
	 * beforehand which would not have any further usage.
	 */
	public static void initAndShow() {
		new ReaderGUI(new ReaderController());
	}

	/**
	 * Inherited method of ActionListener. Performs a specified action based on the
	 * given ActionEvent.
	 * 
	 * Enables the user to choose a directory path for the project, the file path of
	 * the JSON file, the namespace of the new project location and the ability to
	 * press the code generation button to initiate the Java code generation
	 * process.
	 * 
	 * Several checks are in place to guarantee that all necessary selections are
	 * filled in and valid. If an error occurs at this point a warning message will
	 * be displayed with the message stating the current problem.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == languageComboBox) {
			if (languageComboBox.getSelectedIndex() == 0) {
				changeLanguage(new Locale("en", "US"));
			} else if (languageComboBox.getSelectedIndex() == 1) {
				changeLanguage(new Locale("de", "DE"));
			}
		} else if (e.getSource() == projectComboBox) {
			if (projectComboBox.getSelectedIndex() == 0) {
				projectPathLabelNew.setVisible(true);
				projectPathLabelExisting.setVisible(false);
				if (previousIndex != 0) {
					projectPathTextField.setText("");
					previousIndex = 0;
				}
			} else if (projectComboBox.getSelectedIndex() == 1) {
				projectPathLabelNew.setVisible(false);
				projectPathLabelExisting.setVisible(true);
				if (previousIndex != 1) {
					projectPathTextField.setText("");
					previousIndex = 1;
				}
			}
		} else if (e.getSource() == namespaceComboBox) {
			if (namespaceComboBox.getSelectedIndex() == 0) {
				namespaceDeclareLabel.setVisible(true);
				namespaceTextField.setVisible(true);
			} else {
				namespaceDeclareLabel.setVisible(false);
				namespaceTextField.setVisible(false);
			}
		} else if (e.getSource() == projectPathButton) {
			JFileChooser projectPathFC = createDirectoryFileChooser();
			int returnVal = projectPathFC.showOpenDialog(frame);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = projectPathFC.getSelectedFile();
				projectPathTextField.setText(file.getPath());
				log.info("Received project path: " + file.getPath());
			} else {
				log.info("User canceled selection");
			}
		} else if (e.getSource() == jsonPathButton) {
			JFileChooser jsonPathFC = createJSONFileFileChooser();
			int returnVal = jsonPathFC.showOpenDialog(frame);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = jsonPathFC.getSelectedFile();
				jsonPathTextField.setText(file.getPath());
				log.info("Received json file path: " + file.getPath());
			} else {
				log.info("User canceled selection");
			}
		} else if (e.getSource() == codeGenButton) {
			String warningMessage = InputValidator.validateUserInput(projectComboBox.getSelectedIndex(),
					projectPathTextField.getText(), jsonPathTextField.getText());

			if (namespaceComboBox.getSelectedIndex() == 0) {
				String tempWarningMessage = InputValidator.validateNamespace(namespaceTextField.getText());

				if (!tempWarningMessage.isEmpty()) {
					if (warningMessage.isEmpty()) {
						warningMessage += tempWarningMessage;
					} else {
						warningMessage += "\n" + tempWarningMessage;
					}
				}
			}

			if (warningMessage.isEmpty()) {
				log.info("Starting code generation");
				controller.generateJavaCode(jsonPathTextField.getText(), projectComboBox.getSelectedIndex(),
						projectPathTextField.getText(), namespaceComboBox.getSelectedIndex(),
						namespaceTextField.getText());
			} else {
				showWarningDialog(warningMessage);
			}
		}
	}

	/**
	 * Shows a message dialog with a warning message to the user stating the current
	 * problem.
	 * 
	 * @param warningMessage - Warning message to be displayed
	 */
	public static void showWarningDialog(String warningMessage) {
		log.warning(warningMessage);
		JOptionPane.showMessageDialog(frame, warningMessage, I18N.resourceBundle.getString("general.warning"),
				JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * Shows a message dialog with an information message to the user stating a
	 * current event that occurred
	 * 
	 * @param informationMessage - Info message to be displayed
	 */
	public static void showInfoDialog(String informationMessage) {
		JOptionPane.showMessageDialog(frame, informationMessage, I18N.resourceBundle.getString("general.information"),
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Creates a JFileChooser that can search through directories but only allows to
	 * select directories.
	 * 
	 * @return JFileChooser - File chooser that searches for directories only.
	 */
	private JFileChooser createDirectoryFileChooser() {
		JFileChooser projectPathFC = new JFileChooser();
		projectPathFC.setDialogTitle(I18N.resourceBundle.getString("general.projectPathFC"));
		projectPathFC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		projectPathFC.setFileFilter(new DirectoryFilter());
		return projectPathFC;
	}

	/**
	 * Creates a JFileChooser that can search through directories but only allows to
	 * select JSON files.
	 * 
	 * @return JFileChooser - File chooser that searches for JSON files only.
	 */
	private JFileChooser createJSONFileFileChooser() {
		JFileChooser jsonPathFC = new JFileChooser();
		jsonPathFC.setDialogTitle(I18N.resourceBundle.getString("general.jsonPathFC"));
		jsonPathFC.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jsonPathFC
				.setFileFilter(new FileNameExtensionFilter(I18N.resourceBundle.getString("general.JSONFiles"), "json"));
		return jsonPathFC;
	}

	/**
	 * Changes the language based on a given Locale containing the region and
	 * language to change into. First switches the language context for the
	 * FileChooser labels and then changes the text of all components inside the GUI
	 * that display any form of text.
	 * 
	 * @param locale - The region and language to change into
	 */
	private void changeLanguage(Locale locale) {
		I18N.changeLocale(locale);

		headlineLabel.setText(I18N.resourceBundle.getString("general.headlineLabel"));
		descriptionLabel.setText(I18N.resourceBundle.getString("general.descriptionLabel"));

		projectChooseLabel.setText(I18N.resourceBundle.getString("general.projectChooseLabel"));
		projectOptions[0] = I18N.resourceBundle.getString("general.projectOptions.index0");
		projectOptions[1] = I18N.resourceBundle.getString("general.projectOptions.index1");
		int tempProjectIndex = projectComboBox.getSelectedIndex();
		projectComboBox.setModel(new JComboBox<String>(projectOptions).getModel());
		projectComboBox.setSelectedIndex(tempProjectIndex);

		projectPathLabelNew.setText(I18N.resourceBundle.getString("general.projectPathLabelNew"));
		projectPathLabelExisting.setText(I18N.resourceBundle.getString("general.projectPathLabelExisting"));
		projectPathLabelExisting
				.setToolTipText(I18N.resourceBundle.getString("general.projectPathLabelExisting.tooltip"));

		jsonPathLabel.setText(I18N.resourceBundle.getString("general.jsonPathLabel"));

		namespaceChooseLabel.setText(I18N.resourceBundle.getString("general.namespaceChooseLabel"));
		namespaceOptions[0] = I18N.resourceBundle.getString("general.namespaceOptions.index0");
		namespaceOptions[1] = I18N.resourceBundle.getString("general.namespaceOptions.index1");
		namespaceOptions[2] = I18N.resourceBundle.getString("general.namespaceOptions.index2");
		int tempNamespaceIndex = namespaceComboBox.getSelectedIndex();
		namespaceComboBox.setModel(new JComboBox<String>(namespaceOptions).getModel());
		namespaceComboBox.setSelectedIndex(tempNamespaceIndex);

		namespaceDeclareLabel.setText(I18N.resourceBundle.getString("general.namespaceDeclareLabel"));
		namespaceDeclareLabel.setToolTipText(I18N.resourceBundle.getString("general.namespaceDeclareLabel.tooltip"));

		codeGenButton.setText(I18N.resourceBundle.getString("general.codeGenButton"));

		log.info("Changed language to: '" + locale + "'");
	}

}
