/******************************************************************************
* Title: EventHandler.java
* Author: Mike Schoonover
* Date: 12/30/13
*
* Purpose:
*
* This Interface provides methods used to transfer event handling information
* between objects.
*
*/

//-----------------------------------------------------------------------------

package controller;

import java.awt.event.ActionEvent;

public interface EventHandler {

    public void actionPerformed(ActionEvent e);

}
