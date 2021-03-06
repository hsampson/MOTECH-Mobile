/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010 The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.motechproject.mobile.model.dao.hibernate.imp;

import org.springframework.transaction.annotation.Transactional;
import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.model.dao.imp.IncomingMessageFormDefinitionDAO;
import org.motechproject.mobile.core.model.IncomingMessageFormDefinition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import static org.junit.Assert.*;

/**
 *
 * @author joseph Djomeda (joseph@dreamoval.com)
 * @Date Dec 11, 2009
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/test-core-config.xml"})
@TransactionConfiguration
@Transactional
public class IncomingMessageFormDefinitionDAOImplTest {

    @Autowired
    CoreManager coreManager;
    IncomingMessageFormDefinitionDAO imfDAO;
    @Autowired
    IncomingMessageFormDefinition fdef1;

    String formCode = "TESTFORM";

    public IncomingMessageFormDefinitionDAOImplTest() {
    }

    @Before
    public void setUp() {

        imfDAO = coreManager.createIncomingMessageFormDefinitionDAO();

        fdef1 = coreManager.createIncomingMessageFormDefinition();
        fdef1.setFormCode(formCode);

        imfDAO.save(fdef1);

    }

    @After
    public void tearDown() {
  
        imfDAO.delete(fdef1);

    }
    /**
     * Test of getByCode method, of class IncomingMessageFormDefinitionDAOImpl.
     */
    @Test
    public void testGetByCode() {
        System.out.println("getByCode");

        IncomingMessageFormDefinition result = imfDAO.getByCode(formCode);
        assertNotNull(result);
        assertEquals(result.getFormCode(), formCode);
        System.out.println(result.toString());
    }
}
