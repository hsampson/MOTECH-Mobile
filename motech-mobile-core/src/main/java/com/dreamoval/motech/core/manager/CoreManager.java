/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dreamoval.motech.core.manager;

import com.dreamoval.motech.core.dao.MessageDetailsDAO;
import com.dreamoval.motech.core.dao.ResponseDetailsDAO;
import com.dreamoval.motech.core.dao.SessionContainer;
import com.dreamoval.motech.core.model.MessageDetails;
import com.dreamoval.motech.core.model.ResponseDetails;
import com.dreamoval.motech.core.service.MotechContext;
import java.io.Serializable;

/**
 * This interface is responsible for the creation of all objects
 * in the core module.
 *
 * @author Henry Sampson (henry@dreamoval.com)
 * Date Created: Aug 3, 2009
 */
public interface CoreManager extends Serializable{

    /**
     * Creates a new instance of MessageDetails
     * 
     * @return The newly created MessageDetails
     */
    public MessageDetails createMessageDetails(MotechContext motechContext);

    /**
     * Creates a new instance of ResponseDetails
     *
     * @return The newly created ResponseDetails
     */
    public ResponseDetails createResponseDetails(MotechContext motechContext);

    /**
     * Creates a new instance of MessageDetailsDAO
     *
     * @return The newly created instance of MessageDetailsDAO
     */
    public MessageDetailsDAO createMessageDetailsDAO(MotechContext motechContext);

    /**
     * Creates a new instance of ResponseDetailsDAO
     *
     * @return The newly created ResponseDetailsDAO
     */
    public ResponseDetailsDAO createResponseDetailsDAO(MotechContext motechContext);

    /**
     * @return the sessionContainer
     */
    SessionContainer getSessionContainer();

    /**
     * @param sessionContainer the sessionContainer to set
     */
    void setSessionContainer(SessionContainer sessionContainer);

    /**
     * Creates a new instance of MotechContext
     *
     * @return The newly created instance of MotechContext
     */
    MotechContext createMotechContext();
}