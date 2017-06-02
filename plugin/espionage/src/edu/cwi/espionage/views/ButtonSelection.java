package edu.cwi.espionage.views;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JRadioButton;

import edu.cwi.espionage.interfaces.SelectedYValueListener;

public class ButtonSelection extends JComponent implements ItemListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JRadioButton btn;
	private List<SelectedYValueListener> radioBtnListeners = new ArrayList<SelectedYValueListener>();
	

	public ButtonSelection(String name, Boolean isSelected) {
		super();
		this.setBtn(new JRadioButton(name, isSelected));
		this.btn.setText(name);
		this.btn.addItemListener(this);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		System.out.println("ButtonSelection");
		JRadioButton button = (JRadioButton) e.getSource();
		if(button.isSelected()){
			fireSelectedYValue();
		}
		
	}

	public JRadioButton getBtn() {
		return btn;
	}

	public void setBtn(JRadioButton btn) {
		this.btn = btn;
	}
	
	public void addSelectedYValueListener(SelectedYValueListener listener) {
		radioBtnListeners.add(listener);

	}
	
	private void fireSelectedYValue() {
		if (!radioBtnListeners.isEmpty()) {
			for (SelectedYValueListener yValueListener : radioBtnListeners) {
				yValueListener.yValueSelected(this);
			}
		}
	}
	
}
