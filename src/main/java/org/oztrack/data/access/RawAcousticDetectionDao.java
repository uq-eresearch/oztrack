package org.oztrack.data.access;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface RawAcousticDetectionDao {
     List <String> getAllAnimalIds();
     List <String> getAllReceiverIds();
}