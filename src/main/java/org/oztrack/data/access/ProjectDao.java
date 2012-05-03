package org.oztrack.data.access;

import org.oztrack.data.model.Project;

import au.edu.uq.itee.maenad.dataaccess.Dao;

/**
 * author: uqpnewm5
 * 29/03/2011
 * 1:36:37 PM
 */


public interface ProjectDao extends Dao<Project> {
    Project getProjectById(Long id);
    //List<PositionFix> getAllPositionFixes(Long projectId);
    //List<Project> getProjectListByUserId(Long id);
}