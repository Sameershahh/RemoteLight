/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Lars O.
 * ===
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * <===license-end
 */

package de.lars.remotelightclient.ui.panels.output.outputComps;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightcore.devices.arduino.RgbOrder;
import de.lars.remotelightcore.devices.artnet.Artnet;
import de.lars.remotelightcore.lang.i18n;
import de.lars.remotelightcore.out.OutputManager;

public class ArtnetSettingsPanel extends DeviceSettingsPanel {
	private static final long serialVersionUID = 1248292009488744767L;
	
	private Artnet artnet;
	private JTextField fieldId;
	private JSpinner spinnerPixels;
	private JComboBox<RgbOrder> comboOrder;
	private Dimension size;
	private JTextField fieldIpAddress;
	private JCheckBox chckbxBroadcast;
	private JLabel lblEndUniverse;
	private JSpinner spinnerSubnet;
	private JSpinner spinnerStartUniverse;
	private JSpinner spinnerUniverseSize;
	private JSpinner spinnerShift;
	private JSpinner spinnerClone;
	private JCheckBox checkboxCloneMirrored;

	public ArtnetSettingsPanel(Artnet artnet, boolean setup) {
		super(artnet, setup);
		this.artnet = artnet;
		
		size = new Dimension(800, 40);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JPanel panelId = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelId.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panelId.setPreferredSize(size);
		panelId.setMaximumSize(size);
		panelId.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(panelId);
		
		JLabel lblNameId = new JLabel(i18n.getString("OutputPanel.NameID")); //$NON-NLS-1$
		panelId.add(lblNameId);
		
		fieldId = new JTextField();
		panelId.add(fieldId);
		fieldId.setColumns(10);
		
		JPanel panelIpAddress = new JPanel();
		FlowLayout fl_panelIpAddress = (FlowLayout) panelIpAddress.getLayout();
		fl_panelIpAddress.setAlignment(FlowLayout.LEFT);
		panelIpAddress.setPreferredSize(size);
		panelIpAddress.setMaximumSize(size);
		panelIpAddress.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(panelIpAddress);
		
		JLabel lblIpAddress = new JLabel("IP Address:"); //$NON-NLS-1$
		panelIpAddress.add(lblIpAddress);
		
		fieldIpAddress = new JTextField();
		fieldIpAddress.setText("");
		panelIpAddress.add(fieldIpAddress);
		fieldIpAddress.setColumns(10);
		
		chckbxBroadcast = new JCheckBox(i18n.getString("ArtnetSettingsPanel.chckbxBroadcast.text")); //$NON-NLS-1$
		chckbxBroadcast.addChangeListener(broadcastCheckboxListener);
		panelIpAddress.add(chckbxBroadcast);
		
		JPanel panelSubnet = new JPanel();
		FlowLayout fl_panelSubnet = (FlowLayout) panelSubnet.getLayout();
		fl_panelSubnet.setAlignment(FlowLayout.LEFT);
		panelSubnet.setPreferredSize(size);
		panelSubnet.setMaximumSize(size);
		panelSubnet.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(panelSubnet);
		
		JLabel lblSubnet = new JLabel(i18n.getString("ArtnetSettingsPanel.lblSubnet.text")); //$NON-NLS-1$
		panelSubnet.add(lblSubnet);
		
		spinnerSubnet = new JSpinner();
		spinnerSubnet.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		UiUtils.configureSpinner(spinnerSubnet);
		panelSubnet.add(spinnerSubnet);
		
		JPanel panelUniverse = new JPanel();
		FlowLayout fl_panelUniverse = (FlowLayout) panelUniverse.getLayout();
		fl_panelUniverse.setAlignment(FlowLayout.LEFT);
		panelUniverse.setPreferredSize(size);
		panelUniverse.setMaximumSize(size);
		panelUniverse.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(panelUniverse);
		
		JLabel lblStartUniverse = new JLabel(i18n.getString("ArtnetSettingsPanel.lblStartUniverse.text")); //$NON-NLS-1$
		panelUniverse.add(lblStartUniverse);
		
		spinnerStartUniverse = new JSpinner();
		spinnerStartUniverse.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		UiUtils.configureSpinner(spinnerStartUniverse);
		spinnerStartUniverse.addChangeListener(universeUpdateListener);
		panelUniverse.add(spinnerStartUniverse);
		
		JLabel lblEndUniverseText = new JLabel(i18n.getString("ArtnetSettingsPanel.lblEndUniverse.text")); //$NON-NLS-1$
		panelUniverse.add(lblEndUniverseText);
		
		lblEndUniverse = new JLabel("0");
		panelUniverse.add(lblEndUniverse);
		
		JPanel panelUniverseSize = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panelUniverseSize.setPreferredSize(size);
		panelUniverseSize.setMaximumSize(size);
		panelUniverseSize.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(panelUniverseSize);
		
		JLabel lblUniverseSize = new JLabel("Universe size:");
		panelUniverseSize.add(lblUniverseSize);
		
		spinnerUniverseSize = new JSpinner();
		spinnerUniverseSize.setModel(new SpinnerNumberModel(artnet.getUniverseSize(), 3, Artnet.MAX_UNIVERSE_SIZE, 1));
		UiUtils.configureSpinner(spinnerUniverseSize);
		spinnerUniverseSize.addChangeListener(universeUpdateListener);
		spinnerStartUniverse.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelUniverseSize.add(spinnerUniverseSize);
		
		JPanel panelPixels = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) panelPixels.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		panelPixels.setPreferredSize(size);
		panelPixels.setMaximumSize(size);
		panelPixels.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(panelPixels);
		
		JLabel lblPixels = new JLabel(i18n.getString("OutputPanel.Pixels")); //$NON-NLS-1$
		panelPixels.add(lblPixels);
		
		spinnerPixels = new JSpinner();
		spinnerPixels.setModel(new SpinnerNumberModel(new Integer(OutputManager.MIN_PIXELS), new Integer(OutputManager.MIN_PIXELS), null, new Integer(1)));
		UiUtils.configureSpinner(spinnerPixels);
		spinnerPixels.addChangeListener(universeUpdateListener);
		panelPixels.add(spinnerPixels);
		
		JPanel panelOrder = new JPanel();
		FlowLayout flowLayout_3 = (FlowLayout) panelOrder.getLayout();
		flowLayout_3.setAlignment(FlowLayout.LEFT);
		panelOrder.setPreferredSize(size);
		panelOrder.setMaximumSize(size);
		panelOrder.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(panelOrder);
		
		JLabel lblRgbOrder = new JLabel(i18n.getString("OutputPanel.RgbOrder"));
		panelOrder.add(lblRgbOrder);
		
		comboOrder = new JComboBox<RgbOrder>();
		comboOrder.setModel(new DefaultComboBoxModel<>(RgbOrder.values()));
		panelOrder.add(comboOrder);
		
		JLabel lblOutputPatch = new JLabel(i18n.getString("OutputPanel.OutputPatch"), SwingConstants.LEFT);
		lblOutputPatch.setFont(Style.getFontBold(11));
		lblOutputPatch.setBorder(new EmptyBorder(5, 5, 0, 0));
		add(lblOutputPatch);
		
		JPanel panelShift = new JPanel();
		FlowLayout flowLayout_Order = (FlowLayout) panelShift.getLayout();
		flowLayout_Order.setAlignment(FlowLayout.LEFT);
		panelShift.setPreferredSize(new Dimension(800, 40));
		panelShift.setMaximumSize(new Dimension(800, 40));
		panelShift.setAlignmentX(0.0f);
		add(panelShift);
		
		JLabel lblShift = new JLabel(i18n.getString("OutputPanel.ShiftPixels"));
		panelShift.add(lblShift);
		
		spinnerShift = new JSpinner();
		spinnerShift.setModel(new SpinnerNumberModel(artnet.getOutputPatch().getShift(), -artnet.getPixels(), artnet.getPixels(), 1));
		UiUtils.configureSpinner(spinnerShift);
		spinnerShift.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				int max = (int) spinnerPixels.getValue() - 1;
				spinnerShift.setModel(new SpinnerNumberModel((int) spinnerShift.getValue(), -max, max, 1));
			}
		});
		panelShift.add(spinnerShift);
		
		JLabel lblClone = new JLabel(i18n.getString("OutputPanel.Clone"));
		panelShift.add(lblClone);
		
		spinnerClone = new JSpinner();
		spinnerClone.setModel(new SpinnerNumberModel(artnet.getOutputPatch().getClone(), 0, artnet.getPixels() / 2, 1));
		UiUtils.configureSpinner(spinnerClone);
		spinnerClone.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				spinnerClone.setModel(new SpinnerNumberModel((Number) spinnerClone.getValue(), 0, artnet.getPixels() / 2, 1));
			}
		});
		panelShift.add(spinnerClone);
		
		checkboxCloneMirrored = new JCheckBox(i18n.getString("OutputPanel.Mirror"));
		checkboxCloneMirrored.setSelected(artnet.getOutputPatch().isCloneMirrored());
		panelShift.add(checkboxCloneMirrored);
		
		setValues();
	}
	
	private void setValues() {
		if(artnet.getId() != null) {
			fieldId.setText(artnet.getId());
		}
		
		spinnerPixels.setValue(artnet.getPixels());
		
		chckbxBroadcast.setSelected(artnet.isBroadcast());
		if(artnet.getUnicastAddress() != null) {
			fieldIpAddress.setText(artnet.getUnicastAddress());
		}
		
		spinnerSubnet.setValue(artnet.getSubnet());
		spinnerStartUniverse.setValue(artnet.getStartUniverse());
		spinnerUniverseSize.setValue(artnet.getUniverseSize());
		updateEndUniverseText();
		
		if(artnet.getRgbOrder() == null) {
			artnet.setRgbOrder(RgbOrder.RGB);
		}
		comboOrder.setSelectedItem(artnet.getRgbOrder());
	}
	
	private ChangeListener broadcastCheckboxListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			if(chckbxBroadcast.isSelected()) {
				fieldIpAddress.setEnabled(false);
			} else {
				fieldIpAddress.setEnabled(true);
			}
		}
	};
	
	private ChangeListener universeUpdateListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			updateEndUniverseText();
		}
	};
	
	private void updateEndUniverseText() {
		lblEndUniverse.setText(artnet.getEndUniverse((int) spinnerStartUniverse.getValue(), (int) spinnerUniverseSize.getValue(), (int) spinnerPixels.getValue()) +"");
		lblEndUniverse.revalidate();
		lblEndUniverse.repaint();
	}

	@Override
	public boolean save() {
		if(fieldId.getText().isEmpty()) {
			return false;
		}
		artnet.setId(fieldId.getText());
		artnet.setPixels((int) spinnerPixels.getValue());
		artnet.setRgbOrder((RgbOrder) comboOrder.getSelectedItem());
		artnet.setUnicastAddress(fieldIpAddress.getText());
		artnet.setBroadcast(chckbxBroadcast.isSelected());
		artnet.setSubnet((int) spinnerSubnet.getValue());
		artnet.setStartUniverse((int) spinnerStartUniverse.getValue());
		artnet.setUniverseSize((int) spinnerUniverseSize.getValue());
		artnet.getOutputPatch().setShift((int) spinnerShift.getValue());
		artnet.getOutputPatch().setClone((int) spinnerClone.getValue());
		artnet.getOutputPatch().setCloneMirrored(checkboxCloneMirrored.isSelected());
		return true;
	}

	@Override
	public String getId() {
		return fieldId.getText();
	}

}
