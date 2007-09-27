/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.exporters.watir.converters.delegates;

import org.apache.commons.lang.StringUtils;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.watir.holders.WatirHolder;
import org.cubictest.exporters.watir.utils.WatirUtils;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.PageElement;
import org.cubictest.model.Text;
import org.cubictest.model.Title;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Select;

/**
 * Page element converter that uses standard Watir without XPath.
 * 
 * @author Christian Schwarz
 */
public class PageElementAsserterPlain {

	public static void handle(WatirHolder stepList, PageElement pe) {
		String idValue = "\"" + StringUtils.replace(pe.getMainIdentifierValue(),"\"", "\\\"") + "\"";
		String idType = WatirUtils.getMainIdType(pe);



		if (pe instanceof Title) {
			stepList.add("if (ie.title() != " + idValue + ")", 2);
			stepList.add("raise " + WatirHolder.TEST_STEP_FAILED, 3);
			stepList.add("end", 2);
		}
		else if (pe instanceof Option) {
			Option option = (Option) pe;
			String selectList = stepList.getPrefix();
			Select select = (Select) option.getParent();
			if (select.getMainIdentifierType().equals(IdentifierType.LABEL)) {
				//If parent select list had label idType, assert that its label target ID was found:
				stepList.add("if (selectListId == nil)", 3);
				stepList.add("raise " + WatirHolder.TEST_STEP_FAILED, 4);
				stepList.add("end", 3);
			}
			
			if (option.getMainIdentifierType().equals(IdentifierType.LABEL)) {
				stepList.add("optionFound = false", 3);
				stepList.add(selectList + ".getAllContents().each do |opt|", 3);
				stepList.add("if(opt == \"" + pe.getMainIdentifierValue() + "\")", 4);
				stepList.add("optionFound = true", 5);
				stepList.add("end", 4);
				stepList.add("end", 3);
				stepList.add("if (!optionFound)", 3);
				stepList.add("raise " + WatirHolder.TEST_STEP_FAILED, 4);
				stepList.add("end", 3);
			}
			else if (option.getMainIdentifierType().equals(IdentifierType.VALUE)) {
				stepList.add("if (" + selectList + ".option(" + WatirUtils.getMainIdType(pe) + ", \"" + pe.getMainIdentifierValue() + "\") == nil)", 2);
				stepList.add("raise " + WatirHolder.TEST_STEP_FAILED, 3);
				stepList.add("end", 2);
			}
			else if (option.getMainIdentifierType().equals(IdentifierType.INDEX)) {
				stepList.add("optionLabel = " + selectList + ".getAllContents()[" + (Integer.parseInt(pe.getMainIdentifierValue()) - 1) + "]", 3);
				stepList.add("if (optionLabel == nil)", 3);
				stepList.add("raise " + WatirHolder.TEST_STEP_FAILED, 4);
				stepList.add("end", 3);
			}			else {
				throw new ExporterException("Only label, value and index are supported identifierts " +
						"for options in select lists in Watir");
			}
		}
		else {
			//handle all other page elements:			

			if (WatirUtils.shouldGetLabelTargetId(pe)) {
				stepList.add(WatirUtils.getLabelTargetId(pe));
				stepList.addSeparator();
				idValue = "labelTargetId";
				idType = ":id";
			}
			
			stepList.add("pass = 0", 3);
			if (pe instanceof Text) {
				String compare = pe.isNot() ? "!=" : "=="; 
				stepList.add("while " + stepList.getPrefix() + ".text.index(" + idValue + ") " + compare + " nil do", 3);
			}
			else {
				String not = pe.isNot() ? "not " : ""; 
				stepList.add("while " + not + "ie." + WatirUtils.getElementType(pe) + "(" + idType + ", " + idValue + ") == nil do", 3);
			}
			stepList.add("if (pass > 20)", 4);
			stepList.add("raise " + WatirHolder.TEST_STEP_FAILED, 5);
			stepList.add("end", 4);
			stepList.add("sleep 0.1", 4);
			stepList.add("pass += 1", 4);
			stepList.add("end", 3);

		}
	}
}
