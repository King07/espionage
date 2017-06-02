package edu.cwi.espionage.views;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.cwi.espionage.interfaces.SelectedHourListener;
import edu.cwi.espionage.slider.RangeSlider;

public class HourRangeSelection extends RangeSlider implements ChangeListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<SelectedHourListener> hourListeners = new ArrayList<SelectedHourListener>();
	

	public HourRangeSelection() {
		super(0, 24);
		this.setMinorTickSpacing(1);  
		this.setMajorTickSpacing(6);  
		this.setPaintTicks(true);  
		this.setPaintLabels(true);
		this.addChangeListener(this);
		this.setUpperValue(18);
		this.setValue(6);
		
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		System.out.println("Hour selection");
		fireSelectedHour();
		
	}

	
	
	public void addSelectedHourListener(SelectedHourListener listener) {
		hourListeners.add(listener);

	}
	
	private void fireSelectedHour() {
		if (!hourListeners.isEmpty()) {
			for (SelectedHourListener hourListener : hourListeners) {
				hourListener.hourSelected(this);
			}
		}
	}

	public int getUpperValue() {
		return super.getUpperValue();
	}
	
}