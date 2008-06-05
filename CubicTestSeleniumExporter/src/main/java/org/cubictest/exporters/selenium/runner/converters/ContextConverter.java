/*******************************************************************************
 * Copyright (c) 2005, 2008 Christian Schwarz and Stein K. Skytteren
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz and Stein K. Skytteren - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.selenium.runner.converters;

import org.cubictest.export.converters.IContextConverter;
import org.cubictest.export.converters.PostContextHandle;
import org.cubictest.export.converters.PreContextHandle;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.model.PageElement;
import org.cubictest.model.TestPartStatus;
import org.cubictest.model.context.AbstractContext;
import org.cubictest.model.context.Frame;
import org.cubictest.model.context.IContext;
import org.cubictest.model.formElement.Select;

import com.thoughtworks.selenium.SeleniumException;

/**
 * Converter for contexts.
 * 
 * @author chr_schwarz
 */
public class ContextConverter implements IContextConverter<SeleniumHolder> {

	/**
	 * Handle entry into a new context.
	 */
	public PreContextHandle handlePreContext(SeleniumHolder seleniumHolder, IContext ctx) {
		if(ctx instanceof Frame){
			Frame frame = (Frame) ctx;
			String locator = "xpath=" + seleniumHolder.getFullContextWithAllElements(frame);
			try{
				seleniumHolder.getSelenium().execute("selectFrame", locator);
				seleniumHolder.addResultByIsNot(frame, TestPartStatus.PASS, frame.isNot());
			}catch (Exception e) {
				seleniumHolder.addResultByIsNot(frame, TestPartStatus.FAIL, frame.isNot());
			}
			seleniumHolder.pushFrame(frame);
		}else if (ctx instanceof AbstractContext || ctx instanceof Select ) {

			//assert context present:
			PageElement pe = (PageElement) ctx;
			
			try {
				String locator = "xpath=" + seleniumHolder.getFullContextWithAllElements(pe);
				String text = seleniumHolder.getSelenium().getText(locator);
				if (text == null) {
					seleniumHolder.addResultByIsNot(pe, TestPartStatus.FAIL, pe.isNot());
				}
				else {
					seleniumHolder.addResultByIsNot(pe, TestPartStatus.PASS, pe.isNot());
				}
			}
			catch (SeleniumException e) {
				seleniumHolder.addResultByIsNot(pe, TestPartStatus.FAIL, pe.isNot());
			}
			//save the context:
			seleniumHolder.pushContext(ctx);
		}
		return PreContextHandle.CONTINUE;
	}

	
	/**
	 * Handle exit from context.
	 */
	public PostContextHandle handlePostContext(SeleniumHolder seleniumHolder, IContext ctx) {
		
		if(ctx instanceof Frame){
			seleniumHolder.getSelenium().execute("selectFrame", "relative=up");
			seleniumHolder.popFrame();
		}else if (ctx instanceof AbstractContext || ctx instanceof Select) {
			seleniumHolder.popContext();
		}
		return PostContextHandle.DONE;
	}
}
