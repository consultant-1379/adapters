package com.ericsson.eniq.etl.bcd;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Fdn {
	private static final long serialVersionUID = -3640770124129258115L;
	String rootMo = null;
	LinkedHashSet<String> _fdn;
	boolean removeRootMoR;
	boolean removeVsData;

	/**
	 * Container for Fully Distinguished Name
	 */
	public Fdn(boolean removeRootMoR, boolean removeVsData) {
		this.removeRootMoR = removeRootMoR;
		this.removeVsData = removeVsData;
		_fdn = new LinkedHashSet<String>();
	}

	/**
	 * Clears the FDN
	 */
	public void reset() {
		_fdn.clear();
	}

	/**
	 * Handles a new ManagedObject
	 * 
	 * @param mo
	 *            ManagedObject name
	 * @param level
	 *            length of the FDN
	 */
	public void handle(String mo, int level) {
		/*
		 * If the VsData should be removed from the FDN, remove it first
		 */
		if (removeVsData) {
			mo = mo.replaceAll("vsData", "");
		}

		if (_fdn.isEmpty() & rootMo == null) {
			/*
			 * Search for the _R from the RootMO. It has to be removed, so that
			 * the FDN matches the one in statistics
			 */
			Pattern pattern = Pattern.compile(".+_R$");
			Matcher matcher = pattern.matcher(mo);
			/*
			 * Do we have the "_R" in the end? Do we want to remove it?
			 */
			if (matcher.matches() && removeRootMoR) {
				/*
				 * Yes we do, removing it
				 */
				rootMo = mo.substring(0, mo.length() - 2);
			} else {
				/*
				 * We dont, so leave it as it is
				 */
				rootMo = mo;
			}
		} else if (_fdn.isEmpty() & rootMo != null) {
			_fdn.add(mo);
		} else {
			StringTokenizer tk = new StringTokenizer(getFdn(), ",");
			_fdn.clear();
			int count = 0;
			while (tk.hasMoreTokens()) {
				String current = tk.nextToken();
				if (!current.equals(rootMo) && count < level - 1) {
					_fdn.add(current);
					count++;
				}
			}
			_fdn.add(mo);
		}
	}

	/**
	 * Returns the FDN
	 * 
	 * @return String The Fully Distinguished Name
	 */
	public String getFdn() {
		StringBuffer sb = new StringBuffer();
		Iterator<String> it = _fdn.iterator();
		while (it.hasNext()) {
			sb.append(it.next());
			if (it.hasNext()) {
				sb.append(",");
			}
		}
		if (sb.toString().length() == 0) {
			return rootMo;
		} else {
			return rootMo + "," + sb.toString();
		}
	}

	/**
	 * Returns the Sender Name
	 * 
	 * @return String The Sender Name Name
	 * 
	 */

	public String getSn() {
		String fdn = getFdn();
		Pattern pattern = Pattern.compile("(.+),ManagedElement=.+");
		Matcher matcher = pattern.matcher(fdn);
		/*
		 * If we have match, return the matching string, else return the FDN
		 */
		if (matcher.matches()) {
			return matcher.group(1);
		} else {
			return fdn;
		}
	}
	/**
	 * Returns the Managed Object Identifier 
	 * 
	 * @return String - The Managed Object Identifier Name
	 * 
	 */
	public String getMoid() {
		String fdn = getFdn();
		Pattern pattern = Pattern.compile(".+,(ManagedElement=.+)");
		Matcher matcher = pattern.matcher(fdn);
		/*
		 * If we have match, return the matching string, else return empty since
		 * we do not have Moid
		 */
		if (matcher.matches()) {
			return matcher.group(1);
		} else {
			return "";
		}
	}
}
