package com.ericsson.eniq.etl.asn1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * 
 * @author ejarsav
 * 
 */
public class ASN1RuleHandler {

	final private Map ruleSetMap = new HashMap();

	final private List ruleSetStack = new ArrayList();

	private boolean first = true;

	private static final String PRIMITIVE = "PRIMITIVE";

	private static final String SEQUENCE = "SEQUENCE";

	private static final String CHOICE = "CHOICE";

	private static final String ELEMENT = "ELEMENT";

	private static final String MAIN = "MAIN";

	final static private boolean verbose = false;

	private ASN1RuleSet peekRuleSet() {
		if (ruleSetStack.isEmpty()) {
			return null;
		} else {
			return (ASN1RuleSet) ruleSetStack.get(ruleSetStack.size() - 1);
		}
	}

	private ASN1RuleSet popRuleSet() {
		if (ruleSetStack.isEmpty()) {

			return null;

		} else {

			final ASN1RuleSet rSet = (ASN1RuleSet) ruleSetStack
					.remove(ruleSetStack.size() - 1);
			rSet.count--;
			return rSet;

		}
	}

	private void pushRuleSet(ASN1RuleSet rs) {
		rs.count++;
		ruleSetStack.add(rs);
	}

	private ASN1RuleSet getRuleSet(final ASN1Rule rule) {
		final ASN1RuleSet rSet = (ASN1RuleSet) ruleSetMap
				.get("key" + rule.type);
		if (rSet == null) {
			return null;
		}
		rSet.callerRuleType = rule.internalType;
		return rSet;
	}

	private ASN1Rule handlePrimitive(final ASN1RuleSet rSet) {
		final ASN1Rule rule = rSet.rule();
		rSet.nextRule();
		return rule;
	}

	private ASN1Rule handlePrimitive2(final ASN1RuleSet rSet) {
		ASN1Rule rule = rSet.rule();

		// TODO this works only if all following rule(set)s are primitive.
		ASN1RuleSet nrSet;
		while ((nrSet = getRuleSet(rule)) != null) {
			nrSet.nextRule();
			rule = nrSet.rule();
		}
		rSet.nextRule();

		final ASN1Rule rRule = rule.copy();

		return rRule;
	}

	private ASN1Rule handleChoice(final int tagID, final ASN1RuleSet rSet) {
		final ASN1Rule result = new ASN1Rule();
		result.name = "";
		result.isChoice = true;
		final Iterator iter = rSet.rules.iterator();
		boolean first = true;
		while (iter.hasNext()) {
			final ASN1Rule rule = (ASN1Rule) iter.next();
			result.choices.add(rule);
			if (first) {
				result.name += rule.name;
			}
			first = false;

		}
		result.name = ((ASN1Rule) result.choices.get(tagID)).name;
		result.type = ((ASN1Rule) result.choices.get(tagID)).type;
		return result;
	}

	private String handleElement(final ASN1RuleSet rSet) {

		final ASN1RuleSet nrSet = getRuleSet(rSet.rule());
		if (nrSet.type.equalsIgnoreCase(SEQUENCE)) {

			pushRuleSet(nrSet);
			nrSet.nextRule();
			rSet.nextRule();
			return nrSet.name;

		} else if (nrSet.type.equalsIgnoreCase(PRIMITIVE)) {

			nrSet.nextRule();
			rSet.nextRule();
			return nrSet.name;

		} else {

		}

		return null;

	}

	private String handleSequence(final ASN1RuleSet rSet) {

		final ASN1RuleSet nrSet = getRuleSet(rSet.rule());
		if (nrSet.type.equalsIgnoreCase(SEQUENCE)) {

			pushRuleSet(nrSet);
			rSet.nextRule();
			return nrSet.name;

		} else if (nrSet.type.equalsIgnoreCase(PRIMITIVE)) {

			pushRuleSet(nrSet);
			rSet.nextRule();
			nrSet.nextRule();
			return nrSet.name;

		} else if (nrSet.type.equalsIgnoreCase(CHOICE)) {

			pushRuleSet(nrSet);
			rSet.nextRule();
			nrSet.nextRule();
			return nrSet.name;

		} else {

		}

		return null;

	}

	public ASN1Rule primitive(final int tagID) {

		final ASN1RuleSet rSet = peekRuleSet();

		if (rSet.type.equalsIgnoreCase(CHOICE)) {
			return handleChoice(tagID, rSet);
		} else if (rSet.type.equalsIgnoreCase(SEQUENCE)) {
			return handlePrimitive2(rSet);
		} else if (rSet.type.equalsIgnoreCase(PRIMITIVE)) {
			return handlePrimitive(rSet);
		} else {
			return null;
		}
	}

	public String startSequence() {

		final ASN1RuleSet rSet = peekRuleSet();
		final ASN1Rule rule = rSet.rule();

		if (rule == null) {

			rSet.nextRule();
			if (!rSet.callerRuleType.equalsIgnoreCase(MAIN)) {
				pushRuleSet(rSet);
			}
			return rSet.name;

		} else {

			if (rule.internalType.equalsIgnoreCase(SEQUENCE)) {
				return (String) handleSequence(rSet);
			}
			if (rule.internalType.equalsIgnoreCase(ELEMENT)) {
				return (String) handleElement(rSet);
			}
			if (rule.internalType.equalsIgnoreCase(PRIMITIVE)) {
				return ((ASN1Rule) handlePrimitive(rSet)).name;
			}
		}

		return "";
	}

	public String endSequence() {

		ASN1RuleSet rSet = peekRuleSet();

		if (rSet.callerRuleType.equalsIgnoreCase(SEQUENCE)) {

			rSet = popRuleSet();
			if (peekRuleSet().callerRuleType.equalsIgnoreCase(SEQUENCE)
					&& rSet.count != 0) {
				peekRuleSet().reset();
			}
			return rSet.name;

		} else if (rSet.callerRuleType.equalsIgnoreCase(ELEMENT)) {

			rSet = popRuleSet();
			rSet.reset();
			return rSet.name;

		} else if (rSet.callerRuleType.equalsIgnoreCase(MAIN)) {
			return MAIN;
			
		} else {
			// should not happen, do the error...
		}

		return "";
	}

	private ASN1RuleSet singleLine(final String line) {

		final String[] ruleStr = line.split("::=");

		final String name = ruleStr[0].trim();
		final String type = ruleStr[1].trim();

		final ASN1RuleSet ruleSet = createRuleSet(name, type);

		return ruleSet;
	}

	private boolean isSequence(final String type) {
		if (type.indexOf("SEQUENCE") != -1) {
			return true;
		}
		return false;
	}

	private boolean isChoice(final String type) {
		if (type.indexOf("CHOICE") != -1) {
			return true;
		}
		return false;
	}

	private boolean isPrimitive(final String type) {

		if (type.indexOf("INTEGER") != -1
				|| type.indexOf("PrintableString") != -1
				|| type.indexOf("BOOLEAN") != -1
				|| type.indexOf("GraphicString") != -1
				|| type.indexOf("REAL") != -1
				|| type.indexOf("GeneralizedTime") != -1
				|| type.indexOf("UTF8String") != -1
				|| type.indexOf("IA5String") != -1
				|| type.indexOf("OBJECT IDENTIFIER") != -1
				|| type.indexOf("OCTET STRING") != -1
				|| type.indexOf("NULL") != -1) {
			return true;
		} else
			return false;

	}

	private ASN1RuleSet createRuleSet(final String name, final String type) {

		final ASN1RuleSet rSet = new ASN1RuleSet();

		final ASN1Rule tmp = createRule(name, type);
		rSet.name = name;
		rSet.rules.add(tmp);
		rSet.type = tmp.internalType;

		return rSet;
	}

	private ASN1Rule createRule(final String name, final String rule) {

		final ASN1Rule result = new ASN1Rule();

		String typeName = rule;
		String type = "";
		final String size = "";
		boolean optional = false;
		String defaultValue = "";

		if (isPrimitive(rule)) {

			type = PRIMITIVE;

			if (rule.indexOf("DEFAULT") != -1) {
				optional = true;
				defaultValue = rule.substring(rule.indexOf("DEFAULT"))
						.replaceFirst("DEFAULT", "").trim();
				typeName = typeName.substring(0, rule.indexOf("DEFAULT"))
						.trim();
			}

			if (rule.indexOf("(") != -1) {
				typeName = rule.substring(0, rule.indexOf('(')).trim();
			}

		} else if (isSequence(rule)) {

			typeName = rule.substring(rule.indexOf("SEQUENCE OF") + 11).trim();
			type = SEQUENCE;

		} else {

			// constructed
			type = ELEMENT;
			typeName = rule;

		}

		result.name = name;
		result.internalType = type;
		result.type = typeName;
		result.info = size;
		result.optional = optional;
		result.defaultValue = defaultValue;

		return result;
	}

	private ASN1RuleSet multiLine(String line, final BufferedReader br)
			throws Exception {

		final ASN1RuleSet ruleSet = new ASN1RuleSet();

		String[] ruleStr = line.split("::=");

		final String rSetName = ruleStr[0].trim();
		String rSetType = ruleStr[1].replace('{', ' ').trim();

		if (isPrimitive(rSetType)) {

			ruleSet.type = PRIMITIVE;

		} else if (isSequence(rSetType)) {

			rSetType = SEQUENCE;

		} else if (isChoice(rSetType)) {

			rSetType = CHOICE;

		} else {

			rSetType = ELEMENT;
		}

		ruleSet.name = rSetName;
		ruleSet.typeName = rSetType;
		ruleSet.type = rSetType;

		final StringBuffer ruleSetStr = new StringBuffer();

		// multiline
		while ((line = br.readLine()) != null) {
			// skip empty lines
			if (line.trim().length() > 0) {

				// read until } is found
				if (line.indexOf('}') == -1) {
					ruleSetStr.append(line);
				} else {
					ruleSetStr.append(line.replace('}', ' ').trim());
					break;
				}
			}
		}

		ruleStr = ruleSetStr.toString().split(",");

		for (int i = 0; i < ruleStr.length; i++) {

			final String rule = ruleStr[i];

			final String name = rule.substring(0, rule.indexOf(" ")).trim();
			final String type = rule.substring(rule.indexOf(" ")).trim();

			ruleSet.rules.add(createRule(name, type));
		}

		return ruleSet;
	}

	private void createRules(final BufferedReader br) throws Exception {

		String line = null;

		// find begin tag
		while ((line = br.readLine()) != null) {
			if (line.trim().length() > 0) {
				if (line.equalsIgnoreCase("begin")) {
					break;
				}
			}
		}

		while ((line = br.readLine()) != null) {
			// skip empty lines
			if (line.trim().length() > 0) {

				// rule set starts
				if (line.indexOf("::=") != -1) {

					ASN1RuleSet ruleSet;

					if (line.indexOf("{") == -1) {

						// single line
						ruleSet = singleLine(line);

					} else {
						ruleSet = multiLine(line, br);

					}

					if (first) {
						ruleSet.callerRuleType = MAIN;
						ruleSetStack.add(ruleSet);
						first = false;
					}

					if (verbose) {
						System.out.println(ruleSet.toString());
					}
					ruleSetMap.put("key" + ruleSet.name, ruleSet);
				}
			}
		}

	}

	public void createRules(final String rules) throws Exception {

		final BufferedReader br = new BufferedReader(new StringReader(rules));
		createRules(br);
	}

	public void createRules(final File file) throws Exception {

		final FileReader fr = new FileReader(file);
		final BufferedReader br = new BufferedReader(fr);
		createRules(br);
	}

}
