/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.controller;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.ActionType;
import org.cubictest.model.IActionElement;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.model.formElement.AbstractTextInput;
import org.cubictest.model.formElement.Button;
import org.cubictest.model.formElement.Checkbox;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Password;
import org.cubictest.model.formElement.RadioButton;
import org.cubictest.model.formElement.Select;
import org.cubictest.model.formElement.TextArea;
import org.cubictest.model.formElement.TextField;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.cubictest.ui.gef.view.MidpointOffsetLocator;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.gef.EditPart;


public class UserInteractionsTransitionEditPart extends TransitionEditPart{
	
	private Figure figure;
	/**
	 * Constructionr for the <code>UserInteractionsTransitionEditPart</code>.
	 * @param transition
	 */	
	public UserInteractionsTransitionEditPart(UserInteractionsTransition userInteractionsTransition){
		super(userInteractionsTransition);
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		refreshVisuals();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	@Override
	protected void refreshVisuals() {
		super.refreshVisuals();
		PolylineConnection conn = (PolylineConnection) getFigure();
		List<Figure> delete = new ArrayList<Figure>();
		for(Object o : conn.getChildren()){
			if(o instanceof Figure){
				Figure f = (Figure)o;
				if(!(f instanceof PolygonDecoration) && ColorConstants.menuBackground.equals(f.getBackgroundColor()))
					delete.add(f);
			}
		}
		for(Figure f : delete)
			conn.remove(f);
		
		if (getModel() instanceof UserInteractionsTransition && ((UserInteractionsTransition)getModel()).hasUserInteractions()){
			figure = new Figure();
			ToolbarLayout layout = new ToolbarLayout();
			layout.setVertical(true);
			layout.setStretchMinorAxis(true);
			figure.setLayoutManager(layout);
			figure.setBackgroundColor(ColorConstants.menuBackground);
			figure.setOpaque(true);
			//figure.setLabelText(((UserInteractionsTransition)getModel()).getText());
			figure.setToolTip(new Label("User interaction on the page/state"));
			conn.setToolTip(new Label("User interaction on the page/state"));
			
			List<UserInteraction> inputs = ((UserInteractionsTransition)getModel()).getUserInteractions();
			Label inputLabel;
			
			for(UserInteraction input : inputs){
				IActionElement element = input.getElement();
				String inputLabelText = ((element != null)?element.getDescription() + ": " : "") + input.getActionType().getText();
				
				if (ActionType.ENTER_TEXT.equals(input.getActionType()) 
						&& element instanceof AbstractTextInput) {
					inputLabelText =  inputLabelText + ": "
						+ input.getTextualInput();
				}
				if (ActionType.ENTER_PARAMETER_TEXT.equals(input.getActionType())
						&& element instanceof AbstractTextInput) {
					inputLabelText =  inputLabelText + ": "
						+ input.getTextualInput();
				}
				inputLabel = new Label(inputLabelText);
				if (element instanceof Button){
					inputLabel.setIcon(CubicTestImageRegistry.get(
							CubicTestImageRegistry.BUTTON_IMAGE));
				}
				else if (element instanceof Password){
					inputLabel.setIcon(CubicTestImageRegistry.get(
							CubicTestImageRegistry.PASSWORD_IMAGE));
				}	
				else if (element instanceof Select){
					inputLabel.setIcon(CubicTestImageRegistry.get(
							CubicTestImageRegistry.SELECT_IMAGE));
				}
				else if (element instanceof Checkbox){
					if (input.getActionType().equals(ActionType.UNCHECK)) {
						inputLabel.setIcon(CubicTestImageRegistry.get(
							CubicTestImageRegistry.CHECKBOX_UNCHECKED_IMAGE));
					}
					else {
						inputLabel.setIcon(CubicTestImageRegistry.get(
								CubicTestImageRegistry.CHECKBOX_CHECKED_IMAGE));
					}
				}
				else if (element instanceof RadioButton){
					if (input.getActionType().equals(ActionType.UNCHECK)) {
						inputLabel.setIcon(CubicTestImageRegistry.get(
								CubicTestImageRegistry.RADIO_BUTTON_UNCHECKED_IMAGE));
					}
					else {
						inputLabel.setIcon(CubicTestImageRegistry.get(
								CubicTestImageRegistry.RADIO_BUTTON_CHECKED_IMAGE));
					}
				}
				else if (element instanceof TextField){
					inputLabel.setIcon(CubicTestImageRegistry.get(CubicTestImageRegistry.TEXT_FIELD_IMAGE));
				}
				else if (element instanceof TextArea){
					inputLabel.setIcon(CubicTestImageRegistry.get(
							CubicTestImageRegistry.TEXT_AREA_IMAGE));
				}
				else if (element instanceof Option){
					inputLabel.setIcon(CubicTestImageRegistry.get(
							CubicTestImageRegistry.OPTION_IMAGE));
				}
				else {
					inputLabel = new Label(inputLabelText);
				}
				inputLabel.setLabelAlignment(PositionConstants.LEFT);
				figure.add(inputLabel);
			}
			
			MidpointOffsetLocator locator = new MidpointOffsetLocator(conn);
			conn.add(figure,locator);
		}
	}
	
	/**
	 * Sets the width of the line when selected
	 */
	public void setSelected(int value) {
		super.setSelected(value);
		if (value != EditPart.SELECTED_NONE)
			((PolylineConnection) getFigure()).setLineWidth(2);
		else
			((PolylineConnection) getFigure()).setLineWidth(1);
	}
}