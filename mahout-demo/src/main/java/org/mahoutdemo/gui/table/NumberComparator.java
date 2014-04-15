package org.mahoutdemo.gui.table;

import java.util.Comparator;

public class NumberComparator implements Comparator<Long> {

	public int compare(Long num1, Long num2) {
		return num1.compareTo(num2);
	}

}
