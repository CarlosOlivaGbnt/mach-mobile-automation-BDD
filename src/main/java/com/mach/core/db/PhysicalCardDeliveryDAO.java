package com.mach.core.db;

import com.mach.core.util.EnumCardDeliveryStatus;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mach.core.util.EnumCardDeliveryStatus.*;
import static com.mach.core.util.EnumCardDeliveryStatus.STATUS_002_VALID_ADDRESS;
import static com.mach.core.util.EnumCardDeliveryStatus.STATUS_003_DISPATCHED;
import static com.mach.core.util.EnumCardDeliveryStatus.STATUS_004_DELIVERED;
import static com.mach.core.util.EnumCardDeliveryStatus.STATUS_CARD_MANUFACTURER_RECEIVED;
import static com.mach.core.util.EnumCardDeliveryStatus.STATUS_COURIER_RECEIVED;

public class PhysicalCardDeliveryDAO {

    private static final Logger LOG = LoggerFactory.getLogger(PhysicalCardDeliveryDAO.class);
    private PrepaidCardsServiceDAO prepaidCardsServiceDAO = new PrepaidCardsServiceDAO();
    private PhysicalCardLogisticsServiceDAO physicalCardLogisticsServiceDAO = new PhysicalCardLogisticsServiceDAO();

    public void setEmittedCardDeliveryDelayed(String machId) {
        String emittedCardID = prepaidCardsServiceDAO.getEmittedCardID(machId);
        physicalCardLogisticsServiceDAO.updateStateWithHistory(emittedCardID, STATUS_CARD_MANUFACTURER_RECEIVED);
        physicalCardLogisticsServiceDAO.updateStateWithHistory(emittedCardID, STATUS_002_VALID_ADDRESS);
        physicalCardLogisticsServiceDAO.updateStateWithHistory(emittedCardID, STATUS_COURIER_RECEIVED);
        flagCardDeliveryAsDelayed(emittedCardID);
    }

    public void updateEmittedCardTrackingStateWithHistory(String machId, EnumCardDeliveryStatus status, String stateCause) {
        String emittedCardID = prepaidCardsServiceDAO.getEmittedCardID(machId);
        physicalCardLogisticsServiceDAO.updateStateWithHistory(emittedCardID, status, stateCause);
    }
    public void updateEmittedCardTrackingStateWithHistory(String machId, EnumCardDeliveryStatus status) {
        updateEmittedCardTrackingStateWithHistory(machId,status,null);
    }

    public void setEmittedCardDelivered(String machId) {
        if(machId == null){
            LOG.error("can not set EmittedCardDelivered if the machId is null");
        }
        String emittedCardID = prepaidCardsServiceDAO.getEmittedCardID(machId);
        physicalCardLogisticsServiceDAO.updateStateWithHistory(emittedCardID, STATUS_002_VALID_ADDRESS);
        physicalCardLogisticsServiceDAO.updateStateWithHistory(emittedCardID, STATUS_003_DISPATCHED);
        physicalCardLogisticsServiceDAO.updateStateWithHistory(emittedCardID, STATUS_004_DELIVERED);
    }

    private void flagCardDeliveryAsDelayed(String physicalCardId) {
        Document document = physicalCardLogisticsServiceDAO.getCardTrackingIdDocument(physicalCardId);
        physicalCardLogisticsServiceDAO.collectionDeliveryDelayTrackersInsertOne(document);
        physicalCardLogisticsServiceDAO.collectionDeliveryDelayTrackersDeleteOne(document);
    }
}
