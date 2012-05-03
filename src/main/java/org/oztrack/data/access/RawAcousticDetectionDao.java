package org.oztrack.data.access;

import java.util.List;

import org.oztrack.data.model.RawAcousticDetection;

import au.edu.uq.itee.maenad.dataaccess.Dao;

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