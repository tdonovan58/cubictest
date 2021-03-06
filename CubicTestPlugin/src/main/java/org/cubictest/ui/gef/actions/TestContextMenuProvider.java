/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.ui.gef.actions;

import org.cubictest.model.PageElement;
import org.cubictest.ui.gef.view.AddElementContextMenuList;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.actions.ActionFactory;

/**
 * Provides the "right-click" menu found in the GraphicalTestEditor.
 * 
 * @author SK Skytteren
 * @author chr_schwarz 
 */
public class TestContextMenuProvider extends ContextMenuProvider{

	private ActionRegistry actionRegistry;

	/**
	 * Constructs the context menu for the GraphicalTestEditors graphical viewer.
	 * @param viewer the graphical viewer
	 * @param registry the actionregistry
	 */
	public TestContextMenuProvider(EditPartViewer viewer, ActionRegistry registry){
		super(viewer);
		actionRegistry = registry;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.ContextMenuProvider#buildContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void buildContextMenu(IMenuManager menu){
		GEFActionConstants.addStandardActionGroups(menu);
		
		IAction action;

		MenuManager addElementMenu = new MenuManager("Add page element", "cubicTestPlugin.action.addPageElement");
		for (Class<? extends PageElement> elementClass : AddElementContextMenuList.getList()) {
			action = actionRegistry.getAction(AddPageElementAction.getActionId(elementClass));
			if(action.isEnabled()) {
				addElementMenu.add(action);
			}
		}
		if (!addElementMenu.isEmpty()) {
			menu.insertBefore(GEFActionConstants.GROUP_UNDO, addElementMenu);
		}
		
		MenuManager refactorMenu = new MenuManager("Refactor", "cubicTestPlugin.action.refactor");
		action = actionRegistry.getAction(RefactorToSubTestAction.ACTION_ID);
		if(action.isEnabled()) {
			refactorMenu.add(action);
		}
		if (!refactorMenu.isEmpty()) {
			menu.insertBefore(GEFActionConstants.GROUP_REST, refactorMenu);
		}
		
		action = actionRegistry.getAction(AddUserInteractionTransitionAction.ACTION_ID);
		if (action.isEnabled())
			menu.insertBefore(GEFActionConstants.GROUP_UNDO,action); 
		
		action = actionRegistry.getAction(AddExtensionPointAction.ACTION_ID);
		if (action.isEnabled())
			menu.insertBefore(GEFActionConstants.GROUP_UNDO,action); 

		action = actionRegistry.getAction(UpdateTestStartPointAction.ACTION_ID);
		if (action.isEnabled())
			menu.insertBefore(GEFActionConstants.GROUP_REST,action); 
		
		action = actionRegistry.getAction(PresentAction.ACTION_ID);
		if (action.isEnabled())
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
				
		action = actionRegistry.getAction(ActionFactory.CUT.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_COPY, action);

		action = actionRegistry.getAction(ActionFactory.COPY.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_COPY, action);
		
		action = actionRegistry.getAction(ActionFactory.PASTE.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_COPY, action);
				
		action = actionRegistry.getAction(PopulateCommonAction.ID);
		if (action.isEnabled())
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
		
		action = actionRegistry.getAction(ResetTestAction.ACTION_ID);
		menu.appendToGroup(GEFActionConstants.GROUP_REST,action);

		action = actionRegistry.getAction(AutoLayoutAction.ACTION_ID);
		menu.appendToGroup(GEFActionConstants.GROUP_REST,action);


	}
}