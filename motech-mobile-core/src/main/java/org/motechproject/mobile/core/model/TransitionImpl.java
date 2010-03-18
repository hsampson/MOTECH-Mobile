package org.motechproject.mobile.core.model;

import java.util.Date;

/**
 * Date :Aug 4, 2009
 * @author Joseph Djomeda (joseph@dreamoval.com)
 */
public class TransitionImpl extends MotechEntityImpl implements Transition {

    private GatewayResponse gatewayResponse;
    private String transactionType;
    private String transactionDescription;
    private Date transactionDate;

    public TransitionImpl() {
    }

    /**
     * @return the responseId
     */
    public GatewayResponse getGatewayResponse() {
        return gatewayResponse;
    }

    /**
     * @param responseId the responseId to set
     */
    public void setGatewayResponse(GatewayResponse responseId) {
        this.gatewayResponse = responseId;
    }

    /**
     * @return the transactionType
     */
    public String getTransactionType() {
        return transactionType;
    }

    /**
     * @param transactionType the transactionType to set
     */
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    /**
     * @return the transactionDescription
     */
    public String getTransactionDescription() {
        return transactionDescription;
    }

    /**
     * @param transactionDescription the transactionDescription to set
     */
    public void setTransactionDescription(String transactionDescription) {
        this.transactionDescription = transactionDescription;
    }

    /**
     * @return the transactionDate
     */
    public Date getTransactionDate() {
        return transactionDate;
    }

    /**
     * @param transactionDate the transactionDate to set
     */
    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }
}