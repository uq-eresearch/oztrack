package org.oztrack.data.access;

import au.edu.uq.itee.maenad.dataaccess.Dao;
import org.oztrack.data.model.RawAcousticDetection;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 13/04/11
 * Time: 1:12 PM
 */
public interface RawAcousticDetectionDao extends Dao<RawAcousticDetection> {

     List <String> getAllAnimalIds();
     List <String> getAllReceiverIds();
//    java.util.Date getMinDetectionDate();
//    java.util.Date getMaxDetectionDate();
//    int [] getAllAnimalIds();
//    int [] getAllReceiverIds();

}