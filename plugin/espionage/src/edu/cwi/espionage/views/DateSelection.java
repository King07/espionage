package edu.cwi.espionage.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import edu.cwi.espionage.interfaces.SelectedDatePickerListener;
import edu.cwi.espionage.util.DateLabelFormatter;

public class DateSelection extends JDatePickerImpl implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<SelectedDatePickerListener> datePickerListeners = new ArrayList<SelectedDatePickerListener>();
	

	public DateSelection() {
		super(getDatePanel(), new DateLabelFormatter());
		this.addActionListener(this);
	    
	}

	private static JDatePanelImpl getDatePanel() {
		UtilDateModel model = new UtilDateModel();
	    Properties p = new Properties();
	    p.put("text.today", "Today");
	    p.put("text.month", "Month");
	    p.put("text.year", "Year");
	    
	    JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
		return datePanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Date selection");
		Date selectedDate = (Date) this.getModel().getValue();
		fireSelectedYDatePicker(selectedDate);
		
	}

	
	
	public void addSelectedDatePickerListener(SelectedDatePickerListener listener) {
		System.out.println("addSelectedDatePickerListener");
		datePickerListeners.add(listener);

	}
	
	private void fireSelectedYDatePicker(Date selectedDate) {
		if (!datePickerListeners.isEmpty()) {
			for (SelectedDatePickerListener datePickerListener : datePickerListeners) {
				datePickerListener.datePickerSelected(selectedDate);
			}
		}
	}
	
}