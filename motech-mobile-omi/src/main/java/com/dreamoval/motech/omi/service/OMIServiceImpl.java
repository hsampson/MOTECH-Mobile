package com.dreamoval.motech.omi.service;

import com.dreamoval.motech.core.dao.GatewayRequestDAO;
import com.dreamoval.motech.core.dao.GatewayRequestDetailsDAO;
import com.dreamoval.motech.core.dao.MessageRequestDAO;
import com.dreamoval.motech.core.dao.NotificationTypeDAO;
import com.dreamoval.motech.core.model.MessageRequest;
import com.dreamoval.motech.core.manager.CoreManager;
import com.dreamoval.motech.core.model.GatewayRequestDetails;
import com.dreamoval.motech.core.model.Language;
import com.dreamoval.motech.core.model.MStatus;
import com.dreamoval.motech.core.model.MessageType;
import com.dreamoval.motech.core.model.NotificationType;
import com.dreamoval.motech.core.service.MotechContext;
import com.dreamoval.motech.omi.manager.MessageStoreManager;
import com.dreamoval.motech.omi.manager.StatusHandler;
import com.dreamoval.motech.omp.manager.OMPManager;
import com.dreamoval.motech.omp.service.MessagingService;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.Patient;
import org.motechproject.ws.MessageStatus;

/**
 * An implementation of the OMIService
 *
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 * Date Created: Jul 31, 2009
 *
 */
public class OMIServiceImpl implements OMIService {
    private MessageStoreManager storeManager;
    private OMPManager ompManager;
    private CoreManager coreManager;
    private StatusHandler statHandler;

    private static Logger logger = Logger.getLogger(OMIServiceImpl.class);

    /**String
     *
     * @see OMIService.sendPatientMessage
     */
    public MessageStatus savePatientMessageRequest(Long messageId, String patientName, String patientNumber, ContactNumberType patientNumberType, String langCode, MediaType messageType, Long notificationType, Date startDate, Date endDate){
        logger.info("Constructing MessageRequest object");
        
        MotechContext mc = coreManager.createMotechContext();
        MessageRequest messageRequest = coreManager.createMessageRequest(mc);
        
        NotificationTypeDAO noteTypeDao = coreManager.createNotificationTypeDAO(mc);
        NotificationType noteType = (NotificationType)noteTypeDao.getById(notificationType);
        
        Language langObject = coreManager.createLanguageDAO(mc).getByCode(langCode);
        
        messageRequest.setId(messageId);
        messageRequest.setDateFrom(startDate);
        messageRequest.setDateTo(endDate);
        messageRequest.setRecipientName(patientName);
        messageRequest.setRecipientNumber(patientNumber);
        messageRequest.setNotificationType(noteType);
        messageRequest.setMessageType(MessageType.valueOf(messageType.toString()));
        messageRequest.setLanguage(langObject);
        messageRequest.setStatus(MStatus.QUEUED);

        logger.info("MessageRequest object successfully constructed");
        logger.debug(messageRequest);
        logger.debug(langObject);
        logger.debug(noteType);
        
        logger.info("Saving MessageRequest");        
        MessageRequestDAO msgReqDao = coreManager.createMessageRequestDAO(mc);
        
        Transaction tx = (Transaction)msgReqDao.getDBSession().getTransaction();
        tx.begin();
        msgReqDao.save(messageRequest);
        tx.commit();      
        
        mc.cleanUp();
        
        return MessageStatus.valueOf(messageRequest.getStatus().toString());
    }

    /**
     *logger
     * @see OMIService.sendCHPSMessage
     */
    public MessageStatus saveCHPSMessageRequest(Long messageId, String workerName, String workerNumber, Patient[] patientList, String langCode, MediaType messageType, Long notificationType, Date startDate, Date endDate){
        logger.info("Constructing MessageDetails object");
        
        
        MotechContext mc = coreManager.createMotechContext();
        MessageRequest messageRequest = coreManager.createMessageRequest(mc);
        
        NotificationTypeDAO noteTypeDao = coreManager.createNotificationTypeDAO(mc);
        NotificationType noteType = (NotificationType)noteTypeDao.getById(notificationType);
        
        Language langObject = coreManager.createLanguageDAO(mc).getByCode(langCode);
        
        messageRequest.setId(messageId);
        messageRequest.setDateFrom(startDate);
        messageRequest.setDateTo(endDate);
        messageRequest.setRecipientName(workerName);
        messageRequest.setRecipientNumber(workerNumber);
        messageRequest.setNotificationType(noteType);
        messageRequest.setMessageType(MessageType.valueOf(messageType.toString()));
        messageRequest.setLanguage(langObject);
        messageRequest.setStatus(MStatus.QUEUED);

        logger.info("MessageRequest object successfully constructed");
        logger.debug(messageRequest);
        
        logger.info("Saving MessageRequest");
        MessageRequestDAO msgReqDao = coreManager.createMessageRequestDAO(mc);
        
        Transaction tx = (Transaction)msgReqDao.getDBSession().getTransaction();
        tx.begin();
        msgReqDao.save(messageRequest);
        tx.commit(); 
        
        mc.cleanUp();
        
        return MessageStatus.valueOf(messageRequest.getStatus().toString());
    }
    
    /**
     * @see OMIService.processMessageRequests
     */
    public void processMessageRequests(){
        logger.info("Building search criteria");
        MotechContext mc = coreManager.createMotechContext();
        
        MessageRequestDAO msgReqDao = coreManager.createMessageRequestDAO(mc);
        MessageRequest sample = coreManager.createMessageRequest(mc);
        sample.setStatus(MStatus.QUEUED);
        logger.debug(sample);
        
        logger.info("Fetching stored MessageRequest objects");
        List<MessageRequest> messages = msgReqDao.findByExample(sample);
        
        logger.info("MessageRequest objects fetched successfully");
        logger.debug(messages);
        
        logger.info("Initializing OMP MessagingService");
        MessagingService msgSvc = ompManager.createMessagingService();
        
        logger.info("Preparing messages:");
        for(MessageRequest message : messages){            
            GatewayRequestDetails gwReqDet = storeManager.constructMessage(message, mc);
            
            logger.info("Scheduling GatewayRequest");
            if(mc.getDBSession() != null){
                ((Session)mc.getDBSession().getSession()).evict(message);
                ((Session)mc.getDBSession().getSession()).evict(gwReqDet);
            }
            msgSvc.scheduleMessage(gwReqDet, mc);
            
            logger.info("Updating MessageRequest");
            message.setStatus(MStatus.SCHEDULED);
            logger.debug(message);
            
            if(mc.getDBSession() != null){
                ((Session)mc.getDBSession().getSession()).evict(gwReqDet);
                ((Session)mc.getDBSession().getSession()).evict(message);
            }
            
            Transaction tx = (Transaction)msgReqDao.getDBSession().getTransaction();
            tx.begin();
            msgReqDao.save(message);
            tx.commit(); 
        }
        mc.cleanUp();
        
        logger.info("Messages processed successfully");
    }
    
    /**
     * @see OMIService.processMessageRetries
     */
    public void processMessageRetries(){
        logger.info("Building search criteria");
        MotechContext mc = coreManager.createMotechContext();
        
        MessageRequestDAO msgReqDao = coreManager.createMessageRequestDAO(mc);
        MessageRequest sample = coreManager.createMessageRequest(mc);
        sample.setStatus(MStatus.RETRY);
        logger.debug(sample);
        
        logger.info("Fetching stored MessageRequest objects");
        List<MessageRequest> messages = msgReqDao.findByExample(sample);
        
        logger.info("MessageRequest objects fetched successfully");
        logger.debug(messages);
        
        logger.info("Initializing GatewayRequestDAO");
        GatewayRequestDetailsDAO gwReqDao = coreManager.createGatewayRequestDetailsDAO(mc);
        
        logger.info("Initializing OMP MessagingService");
        MessagingService msgSvc = ompManager.createMessagingService();
        
        for(MessageRequest message : messages){
            GatewayRequestDetails gwReqDet = (GatewayRequestDetails) gwReqDao.getById(message.getId());
            msgSvc.scheduleMessage(gwReqDet, mc);
            
            message.setStatus(MStatus.SCHEDULED);
            
            Transaction tx = (Transaction)msgReqDao.getDBSession().getTransaction();
            tx.begin();
            msgReqDao.save(message);
            tx.commit(); 
        }
        mc.cleanUp();
        
        logger.info("Messages processed successfully");
    }
    
    /**
     * @see OMIService.getMessageResponses
     */
    public void getMessageResponses(){
        MotechContext mc = coreManager.createMotechContext();
        
        logger.info("Building search criteria");
        MessageRequest sample = coreManager.createMessageRequest(mc);
        sample.setStatus(MStatus.PENDING);
        logger.debug(sample);
        
        logger.info("Initializing MessageRequestDAO and fetching matching message requests");
        List<MessageRequest> messages = coreManager.createMessageRequestDAO(mc).findByExample(sample);
        
        logger.info("MessageRequest objects fetched successfully");
        logger.debug(messages);
        
        logger.info("Initializing GatewayRequestDAO");
        GatewayRequestDAO gatewayDao = coreManager.createGatewayRequestDAO(mc);
        
        for(MessageRequest message : messages){
            //GatewayRequest request = (GatewayRequest) gatewayDao.getById(message.getId());
        }
        mc.cleanUp();
    }

    /**
     * @return the storeManager
     */
    public MessageStoreManager getStoreManager() {
        return storeManager;
    }

    /**
     * @param storeManager the storeManager to set
     */
    public void setStoreManager(MessageStoreManager storeManager) {
        logger.debug("Setting OMIServiceImpl.storeManager");
        logger.debug(storeManager);
        this.storeManager = storeManager;
    }

    /**
     * @return the ompManager
     */
    public OMPManager getOmpManager() {
        return ompManager;
    }

    /**
     * @param ompManager the ompManager to set
     */
    public void setOmpManager(OMPManager ompManager) {
        logger.debug("Setting OMIServiceImpl.ompmanager");
        logger.debug(ompManager);
        this.ompManager = ompManager;
    }

    /**
     * @return the ompManager
     */
    public CoreManager getCoreManager() {
        return coreManager;
    }

    /**
     * @param ompManager the ompManager to set
     */
    public void setCoreManager(CoreManager coreManager) {
        logger.debug("Setting OMIServiceImpl.coreManager");
        logger.debug(coreManager);
        this.coreManager = coreManager;
    }

    public StatusHandler getStatHandler() {
        return statHandler;
    }

    public void setStatHandler(StatusHandler statHandler) {
        logger.debug("Setting OMIServiceImpl.statHandler");
        logger.debug(coreManager);
        this.statHandler = statHandler;
    }
}
