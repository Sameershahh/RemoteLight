package de.lars.remotelightclient.ui.panels.colors;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.types.SettingObject;
import de.lars.remotelightclient.ui.MenuPanel;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.panels.controlbars.DefaultControlBar;
import de.lars.remotelightclient.utils.PixelColorUtils;
import de.lars.remotelightclient.utils.UiUtils;
import de.lars.remotelightclient.utils.WrapLayout;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;

public class ColorsPanel extends MenuPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2572544853394733969L;
	private Color[] defaultColors = {Color.ORANGE, Color.RED, Color.MAGENTA, Color.GREEN, Color.BLUE, Color.CYAN, Color.WHITE, Color.BLACK};
	private List<Color> colors;
	private List<CustomColorPanel> ccp;
	private int selSizeFactor = 10;
	private SettingsManager sm = Main.getInstance().getSettingsManager();
	private final int STEP_SIZE = 10;
	private JPanel bgrColors;
	private JPanel bgrContentArea;
	private JLabel lblCurrentSize;
	private JButton btnMinus, btnPlus;
	private JButton btnAdd;
	private JButton btnRemove;
	private JButton btnReset;

	/**
	 * Create the panel.
	 */
	public ColorsPanel() {
		Main.getInstance().getMainFrame().showControlBar(true);
		Main.getInstance().getMainFrame().setControlBarPanel(new DefaultControlBar());
		colors = new ArrayList<>();
		ccp = new ArrayList<>();
		sm.addSetting(new SettingObject("colorspanel.colors", null, defaultColors)); //register setting if not already registered
		sm.addSetting(new SettingObject("colorspanel.panelsize", null, CustomColorPanel.getPanelSize()));
		sm.addSetting(new SettingObject("colorspanel.panelsizelbl", null, 50+""));
		colors = new LinkedList<>(Arrays.asList((Color[]) sm.getSettingObject("colorspanel.colors").getValue()));
		CustomColorPanel.resetPanelSize();
		CustomColorPanel.setPanelSize((Dimension) sm.getSettingObject("colorspanel.panelsize").getValue());
		setBackground(Style.panelBackground);
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		add(scrollPane, BorderLayout.CENTER);
		
		bgrContentArea = new JPanel();
		scrollPane.setViewportView(bgrContentArea);
		bgrContentArea.setLayout(new BorderLayout(0, 0));
		bgrContentArea.setBackground(Style.panelBackground);
		
		bgrColors = new JPanel();
		WrapLayout wlayout = new WrapLayout(FlowLayout.LEFT);
		bgrColors.setLayout(wlayout);
		bgrColors.setBackground(Style.panelBackground);
		bgrContentArea.add(bgrColors);
		
		JPanel bgrMenu = new JPanel();
		bgrMenu.setBackground(Style.panelDarkBackground);
		add(bgrMenu, BorderLayout.SOUTH);
		bgrMenu.setLayout(new BorderLayout(0, 0));
		
		JPanel panelButtons = new JPanel();
		panelButtons.setBackground(Style.panelDarkBackground);
		bgrMenu.add(panelButtons, BorderLayout.WEST);
		
		btnReset = new JButton("");
		UiUtils.configureButton(btnReset);
		btnReset.setName("reset");
		btnReset.setIcon(Style.getUiIcon("reset.png"));
		btnReset.addActionListener(btnListener);
		panelButtons.add(btnReset);
		
		btnRemove = new JButton("");
		UiUtils.configureButton(btnRemove);
		btnRemove.setName("remove");
		btnRemove.setIcon(Style.getUiIcon("remove.png"));
		btnRemove.addActionListener(btnListener);
		panelButtons.add(btnRemove);
		
		btnAdd = new JButton("");
		UiUtils.configureButton(btnAdd);
		btnAdd.setName("add");
		btnAdd.setIcon(Style.getUiIcon("add.png"));
		btnAdd.addActionListener(btnListener);
		panelButtons.add(btnAdd);
		
		JPanel panelSize = new JPanel();
		panelSize.setBackground(Style.panelDarkBackground);
		bgrMenu.add(panelSize, BorderLayout.EAST);
		
		btnMinus = new JButton("-");
		btnMinus.setName("minus");
		btnMinus.addActionListener(btnListener);
		UiUtils.configureButton(btnMinus);
		panelSize.add(btnMinus);
		
		int curSize = Integer.parseInt((String) sm.getSettingObject("colorspanel.panelsizelbl").getValue());
		lblCurrentSize = new JLabel(curSize + "%");
		lblCurrentSize.setName(curSize +"");
		lblCurrentSize.setForeground(Style.textColor);
		panelSize.add(lblCurrentSize);
		
		btnPlus = new JButton("+");
		btnPlus.setName("plus");
		btnPlus.addActionListener(btnListener);
		UiUtils.configureButton(btnPlus);
		panelSize.add(btnPlus);
		
		addColorPanels();
	}
	
	public void addColorPanels() {
		bgrColors.removeAll();
		ccp.clear();
		for(Color c : colors) {
			CustomColorPanel cpanel = new CustomColorPanel(c);
			cpanel.addMouseListener(ccpMouseListener);
			bgrColors.add(cpanel);
			ccp.add(cpanel);
		}
		this.updateUI();
	}
	
	private MouseListener ccpMouseListener = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			Main.getInstance().getEffectManager().stopAll();
			CustomColorPanel cpanel = (CustomColorPanel) e.getSource();
			
			Dimension size = CustomColorPanel.getPanelSize();
			for(CustomColorPanel panel : ccp) {
				panel.setPreferredSize(size);
				panel.setMaximumSize(size);
			}
			Dimension selSize = new Dimension(size.width + selSizeFactor, size.height + selSizeFactor);
			cpanel.setPreferredSize(selSize);
			cpanel.setMaximumSize(selSize);
			cpanel.updateUI();
			CustomColorPanel.setSelectedPanel(cpanel);
			
			Color c = cpanel.getBackground();
			OutputManager.addToOutput(PixelColorUtils.colorAllPixels(c, Main.getLedNum()));
		}
	};
	
	@Override
	public void onEnd(MenuPanel newPanel) {
		sm.getSettingObject("colorspanel.colors").setValue(colors.toArray(new Color[colors.size()]));
		sm.getSettingObject("colorspanel.panelsize").setValue(CustomColorPanel.getPanelSize());
		sm.getSettingObject("colorspanel.panelsizelbl").setValue(lblCurrentSize.getName());
		super.onEnd(newPanel);
	}
	
	private ActionListener btnListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton btn = (JButton) e.getSource();
			
			switch (btn.getName()) {
			case "minus":
				changePanelSize(-STEP_SIZE);
				break;
			case "plus":
				changePanelSize(STEP_SIZE);
				break;
			case "add":
				showColorChooser();
				break;
			case "remove":
				colors.clear();
				ccp.remove(CustomColorPanel.getSelectedPanel());
				for(CustomColorPanel panel : ccp) {
					colors.add(panel.getBackground());
				}
				ccp.clear();
				addColorPanels();
				break;
			case "reset":
				ccp.clear();
				colors = new LinkedList<>(Arrays.asList(defaultColors));
				addColorPanels();
				break;
			}
		}
	};
	
	
	private void changePanelSize(int factor) {
		Dimension curSize = CustomColorPanel.getPanelSize();
		int label = Integer.parseInt(lblCurrentSize.getName());
		
		if(label >= 0 && label <= 100) {
			int newValue = label + factor;
			if(newValue >= 0 && newValue <= 100) {
				CustomColorPanel.setPanelSize(new Dimension(curSize.width + factor, curSize.height + factor));
				addColorPanels();
				
				lblCurrentSize.setText(String.valueOf(newValue) + "%");
				lblCurrentSize.setName(String.valueOf(newValue));
			}
		}
	}
	
	
	private void showColorChooser() {
		bgrContentArea.removeAll();
		
		JPanel preview = new JPanel();
		FlowLayout flayout = new FlowLayout(FlowLayout.LEFT);
		preview.setLayout(flayout);
		preview.setBackground(Style.panelBackground);
		preview.setBorder(BorderFactory.createLineBorder(Style.panelBackground, 4));
		preview.setPreferredSize(new Dimension(0, 40));
		
		UIManager.put("ColorChooser.background", Style.panelBackground);
		UIManager.put("ColorChooser.swatchesRecentSwatchSize", new Dimension(15, 15));
		UIManager.put("ColorChooser.swatchesSwatchSize", new Dimension(20, 20));
		UIManager.put("TabbedPane.opaque", true);
		UIManager.put("TabbedPane.contentOpaque", true);
		
		JColorChooser cc = new JColorChooser();
		cc.setBorder(BorderFactory.createEmptyBorder());
		cc.removeChooserPanel(cc.getChooserPanels()[4]);
		cc.removeChooserPanel(cc.getChooserPanels()[3]);
		cc.removeChooserPanel(cc.getChooserPanels()[2]);
		for(Component co : cc.getComponents()) {
			co.setBackground(Style.panelBackground);
			if(co instanceof JTabbedPane) {
				JTabbedPane tp = (JTabbedPane) co;
				UiUtils.configureTabbedPane(tp);
			}
		}
		cc.setPreviewPanel(new JPanel());
		cc.getSelectionModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				preview.setBackground(cc.getColor());
			}
		});
		
		Dimension btnSize = new Dimension(80, 20);
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setPreferredSize(btnSize);
		UiUtils.configureButton(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bgrContentArea.removeAll();
				bgrContentArea.add(bgrColors);
				updateUI();
			}
		});
		preview.add(btnCancel);
		
		JButton btnOk = new JButton("Ok");
		btnOk.setPreferredSize(btnSize);
		UiUtils.configureButton(btnOk);
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				colors.add(cc.getColor());
				bgrContentArea.removeAll();
				bgrContentArea.add(bgrColors);
				ccp.clear();
				addColorPanels();
			}
		});
		preview.add(btnOk);
		
		bgrContentArea.add(cc, BorderLayout.CENTER);
		bgrContentArea.add(preview, BorderLayout.SOUTH);
		updateUI();
	}
	

}
