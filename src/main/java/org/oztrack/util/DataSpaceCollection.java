package org.oztrack.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.model.Project;
import org.springframework.core.io.ClassPathResource;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;


public class DataSpaceCollection { 
	
    /**
	 */
	private static final long serialVersionUID = 1L;

	/**
    * Logger for this class and subclasses
    */
    protected final Log logger = LogFactory.getLog(getClass());
    
    private static Template atomAgentTemplate;
    private static Template atomCollectionTemplate;

	private Project project;

	private String collectionTitle;
    private String collectionDescription;
    private String collectionURL;
	private String contactGivenName;
    private String contactFamilyName;
	private String contactEmail;
	private String speciesCommonName;
	private String speciesScientificName;
	private String temporalCoverage;
	private String spatialCoverage;
	private String boundingBoxCoordinatesString;
	private String rightsStatement;
	private String accessRights;
	private String dataSpaceUpdateDate;
	
    
    public DataSpaceCollection(Project project) {
    	this.project = project;
    	buildAtomTemplates();
    }
    
    public static synchronized void buildAtomTemplates() {
		
    	if (atomAgentTemplate != null)
			return;
		Reader templateReader;
		try {
			templateReader = new InputStreamReader((
					new ClassPathResource("agent.mustache.atom")
					).getInputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		atomAgentTemplate = Mustache.compiler().compile(templateReader);
		
    	if (atomCollectionTemplate != null)
			return;
		//Reader templateReader;
		try {
			templateReader = new InputStreamReader((
					new ClassPathResource("collection.mustache.atom")
					).getInputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		atomCollectionTemplate = Mustache.compiler().compile(templateReader);
    
    }
    
    public String agentToAtom() {
		return atomAgentTemplate.execute(this);
	}
    
    public String collectionToAtom() {
		return atomCollectionTemplate.execute(this);
	}
    
    public String getCollectionTitle() {
		return project.getTitle();
	}

	public String getCollectionDescription() {
		return project.getDescription();
	}

	public String getCollectionURL() {
		return "http://oztrack.org/projectdescr?id=" + project.getId().toString();
	}

	public String getContactGivenName() {
		return project.getContactGivenName();
	}

	public String getContactFamilyName() {
		return project.getContactFamilyName();
	}

	public String getContactEmail() {
		return project.getContactEmail();
	}

	public String getSpeciesCommonName() {
		return project.getSpeciesCommonName();
	}

	public String getSpeciesScientificName() {
		return project.getSpeciesScientificName();
	}

	public String getTemporalCoverage() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		return "start=" + sdf.format(project.getFirstDetectionDate()) + "; end=" + sdf.format(project.getLastDetectionDate());
	}

	public String getBoundingBoxCoordinatesString() {
		String bb = project.getBoundingBox().toString();
		bb = bb.replace("POLYGON", "");
		bb = bb.replace("(", "");
		bb = bb.replace(")","");
		bb = bb.replace(",","");
		return bb;
	}
	
	public String getSpatialCoverage() {
		return project.getSpatialCoverageDescr();
	}

	public String getRightsStatement() {
		return project.getRightsStatement();
	}

	public String getAccessRights() {
		
		if (project.getIsGlobal()) {
			return "The data in the project is available in OzTrack for the public to use.";
		} else {
			return "The data in this project is only available to users on the OzTrack system whom have " +
						"been granted access. Contact the Collection Manager regarding permission and procedures for accessing the data.";
		}
	}
	
	public String getDataSpaceUpdateDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		return sdf.format(project.getDataSpaceUpdateDate());
	}



 
    
	
}
