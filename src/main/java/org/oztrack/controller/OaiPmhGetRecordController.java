package org.oztrack.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.Range;
import org.oztrack.data.access.InstitutionDao;
import org.oztrack.data.access.OaiPmhRecordDao;
import org.oztrack.data.access.PersonDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.impl.OaiPmhInstitutionRecordMapper;
import org.oztrack.data.access.impl.OaiPmhPersonRecordMapper;
import org.oztrack.data.access.impl.OaiPmhProjectRecordMapper;
import org.oztrack.data.model.Institution;
import org.oztrack.data.model.Person;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.types.OaiPmhRecord;
import org.oztrack.util.OaiPmhConstants;
import org.oztrack.util.OaiPmhException;
import org.oztrack.util.OaiPmhMetadataFormat;
import org.oztrack.view.OaiPmhGetRecordView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

import com.vividsolutions.jts.geom.Polygon;

// Implements GetRecord verb request handling
// http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#GetRecord
@Controller
public class OaiPmhGetRecordController extends OaiPmhController {
    @Autowired
    OaiPmhRecordDao recordDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private PersonDao personDao;

    @Autowired
    private InstitutionDao institutionDao;

    @RequestMapping(value="/oai-pmh", method={RequestMethod.GET, RequestMethod.POST}, produces="text/xml", params="verb=GetRecord")
    public View handleRequest(HttpServletRequest request, HttpServletResponse response) throws OaiPmhException {
        super.preHandleRequest(request, response);

        // Return badArgument error code if request includes illegal arguments.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions
        HashSet<String> legalArguments = new HashSet<String>(Arrays.asList("verb", "identifier", "metadataPrefix"));
        if (!legalArguments.containsAll(request.getParameterMap().keySet())) {
            throw new OaiPmhException("badArgument", "Request includes illegal arguments.");
        }

        // Return badArgument error code if request is missing required arguments.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions
        String identifier = request.getParameter("identifier");
        if (identifier == null) {
            throw new OaiPmhException("badArgument", "identifier argument is missing.");
        }
        String metadataPrefix = request.getParameter("metadataPrefix");
        if (metadataPrefix == null) {
            throw new OaiPmhException("badArgument", "metadataPrefix argument is missing.");
        }

        // Return cannotDisseminateFormat error code if metadataPrefix argument not supported by the repository.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListIdentifiers
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListRecords
        OaiPmhMetadataFormat metadataFormat = null;
        for (OaiPmhMetadataFormat supportedMetadataFormat : OaiPmhConstants.supportedMetadataFormats) {
            if (supportedMetadataFormat.nsPrefix.equals(metadataPrefix)) {
                metadataFormat = supportedMetadataFormat;
            }
        }
        if (metadataFormat == null) {
            throw new OaiPmhException("cannotDisseminateFormat", "metadataPrefix argument is not supported by the repository.");
        }

        String oaiPmhRecordIdentifierPrefix = configuration.getOaiPmhConfiguration().getOaiPmhRecordIdentifierPrefix();
        String localIdentifier = identifier.substring(oaiPmhRecordIdentifierPrefix.length());
        OaiPmhRecord record = null;
        if (localIdentifier.equals("service")) {
            record = recordDao.getRepositoryServiceRecord();
        }
        else if (localIdentifier.equals("oai-pmh")) {
            record = recordDao.getOaiPmhServiceRecord();
        }
        else if (localIdentifier.equals("collection")) {
            record = recordDao.getRepositoryCollectionRecord();
        }
        else if (localIdentifier.equals("data-manager")) {
            record = recordDao.getDataManagerPartyRecord();
        }
        else {
            Matcher matcher = Pattern.compile("^([a-z-]+)/([0-9]+)$").matcher(localIdentifier);
            if (matcher.matches()) {
                String recordType = matcher.group(1);
                Long recordId = Long.valueOf(matcher.group(2));
                if (recordType.equals("projects")) {
                    Project project = projectDao.getProjectById(recordId);
                    final Map<Long, Range<Date>> projectDetectionDateRanges = new HashMap<Long, Range<Date>>();
                    projectDetectionDateRanges.put(project.getId(), projectDao.getDetectionDateRange(project, false));
                    final Map<Long, Polygon> projectBoundingBoxes = new HashMap<Long, Polygon>();
                    projectBoundingBoxes.put(project.getId(), projectDao.getBoundingBox(project, false));
                    OaiPmhProjectRecordMapper mapper = new OaiPmhProjectRecordMapper(configuration, projectDetectionDateRanges, projectBoundingBoxes);
                    record = mapper.map(project);
                }
                else if (recordType.equals("people")) {
                    Person person = personDao.getById(recordId);
                    OaiPmhPersonRecordMapper mapper = new OaiPmhPersonRecordMapper(configuration);
                    record = mapper.map(person);
                }
                else if (recordType.equals("institutions")) {
                    Institution institution = institutionDao.getById(recordId);
                    OaiPmhInstitutionRecordMapper mapper = new OaiPmhInstitutionRecordMapper(configuration);
                    record = mapper.map(institution);
                }
            }
        }
        if (record == null) {
            throw new OaiPmhException("idDoesNotExist", "identifier argument unknown or illegal in this repository.");
        }

        return new OaiPmhGetRecordView(metadataFormat, record);
    }
}
