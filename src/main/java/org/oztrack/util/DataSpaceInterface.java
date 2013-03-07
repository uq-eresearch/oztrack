package org.oztrack.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.Range;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.User;
import org.oztrack.error.DataSpaceInterfaceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.vividsolutions.jts.geom.Polygon;

public class DataSpaceInterface {
    protected final Log logger = LogFactory.getLog(getClass());
    private ProjectDao projectDao;
    private UserDao userDao;
    private String dataSpaceUrl;
    private String dataSpaceResponse;
    private HttpClient httpClient;

    public DataSpaceInterface(ProjectDao projectDao, UserDao userDao) {
        this.projectDao = projectDao;
        this.userDao = userDao;
        this.dataSpaceUrl = OzTrackApplication.getApplicationContext().getDataSpaceUrl();
        this.httpClient = new DefaultHttpClient();
    }

    public void deleteFromDataSpace(Project project) throws DataSpaceInterfaceException {
        User dataSpaceAgent = userDao.getById(project.getDataSpaceAgent().getId());
        String agentURI = project.getDataSpaceAgent().getDataSpaceAgentURI();
        String collectionURI = project.getDataSpaceURI();
        boolean deleteAgent = false;

        // do the collection first
        int statusCode = executeDeleteMethod("collections/" + collectionURI, project);
        if ((statusCode != HttpStatus.SC_OK) && (statusCode != HttpStatus.SC_ACCEPTED) && (statusCode != HttpStatus.SC_NO_CONTENT)) {
            logger.info("deleting DataSpace collection failed");
            throw new DataSpaceInterfaceException("deleting DataSpace agent failed.");
        } else {
            // don't delete agents, other records might point to them
            // deleteAgent = true;
            deleteAgent = false;
            project.setDataSpaceURI(null);
            project.setDataSpaceUpdateDate(new Date());
        }

        project = projectDao.update(project);

        if (deleteAgent) {

            statusCode = executeDeleteMethod("agents/" + agentURI, project);

            if ((statusCode != HttpStatus.SC_OK) && (statusCode != HttpStatus.SC_ACCEPTED) && (statusCode != HttpStatus.SC_NO_CONTENT)) {
                logger.info("deleting DataSpace agent failed");
                throw new DataSpaceInterfaceException("deleting DataSpace agent failed.");
            } else {
                dataSpaceAgent.setDataSpaceAgentURI(null);
                dataSpaceAgent.setDataSpaceAgentUpdateDate(new Date());
            }

            userDao.update(dataSpaceAgent);
        }
    }

    public void updateDataSpace(Project project) throws DataSpaceInterfaceException {
        User dataSpaceAgent = userDao.getById(project.getDataSpaceAgent().getId());
        String agentURI = project.getDataSpaceAgent().getDataSpaceAgentURI();
        String collectionURI = project.getDataSpaceURI();

        Range<Date> dateRange = projectDao.getDetectionDateRange(project, false);
        Polygon boundingBox = projectDao.getBoundingBox(project);
        DataSpaceCollection dsi = new DataSpaceCollection(project, dateRange, boundingBox);

        boolean doAgentPost = false;
        boolean doAgentPut = false;
        boolean doCollectionPost = false;
        boolean doCollectionPut = false;

        // if OzTrack user does not have a DataSpace URI, attempt to create an agent
        if ((agentURI == null) || (agentURI.isEmpty())) {
            doAgentPost = true;
        } else {
            // don't update agents - other systems may have created them
            // doAgentPut = true;
            doAgentPut = false;
        }


        final String agentAtom = dsi.agentToAtom();
        logger.info("**********************************************");
        logger.info(agentAtom);

        if (doAgentPost) {

            int statusCode = executePostMethod("agents", agentAtom, project);

            if (statusCode == HttpStatus.SC_CONFLICT) {
                // save the URI and do a put.
                String agentURL = createURL("agents").toString();
                agentURI = this.dataSpaceResponse.replace(" already exists", "").replace(agentURL + "/", "");
                dataSpaceAgent.setDataSpaceAgentURI(agentURI);
                logger.info("agent already exists: " + agentURI);
                // don't update agents - other systems may have created them
                // doAgentPut = true;
                doAgentPut = false;
            } else if (statusCode != HttpStatus.SC_CREATED) {
                logger.info("non conflict error statusCode" + statusCode);
                doCollectionPost = false;
                doCollectionPut = false;
                throw new DataSpaceInterfaceException("creating DataSpace agent failed.");
            } else {
                agentURI = this.getUriFromResponse("agents");
                dataSpaceAgent.setDataSpaceAgentURI(agentURI);
                dataSpaceAgent.setDataSpaceAgentUpdateDate(new Date());
            }

            dataSpaceAgent = userDao.update(dataSpaceAgent);
        }

        if (doAgentPut) {

            int statusCode = executePutMethod("agents/" + agentURI, agentAtom, project);

            if (statusCode != HttpStatus.SC_OK) {
                doCollectionPost = false;
                doCollectionPut = false;
                logger.info("updating DataSpace agent failed");
                throw new DataSpaceInterfaceException("updating DataSpace agent failed");
            } else {
                dataSpaceAgent.setDataSpaceAgentUpdateDate(new Date());
            }

            dataSpaceAgent = userDao.update(dataSpaceAgent);
        }

        // create or update the collection
        if ((collectionURI == null) || (collectionURI.isEmpty())) {
            doCollectionPost = true;
        } else {
            doCollectionPut = true;
        }

        final String collectionAtom = dsi.collectionToAtom();
        logger.info("**********************************************");
        logger.info(collectionAtom);

        if (doCollectionPost) {
            int statusCode = executePostMethod("collections", collectionAtom, project);
            if (statusCode == HttpStatus.SC_CONFLICT) {
                // save the URI and do a put.
                String collectionURL = createURL("collections").toString();
                collectionURI = this.dataSpaceResponse.replace(" already exists", "").replace(collectionURL + "/", "");
                project.setDataSpaceURI(collectionURI);
                //doCollectionPut = true;
            } else if (statusCode != HttpStatus.SC_CREATED) {
                logger.info("post error statusCode " + statusCode);
                throw new DataSpaceInterfaceException("creating DataSpace collection failed.");
            } else {
                collectionURI = this.getUriFromResponse("collections");
                project.setDataSpaceURI(collectionURI);
                project.setDataSpaceUpdateDate(new Date());
            }
        }

        if (doCollectionPut) {
            int statusCode = executePutMethod("collections/" + collectionURI, collectionAtom, project);
            if (statusCode != HttpStatus.SC_OK) {
                logger.info("updating dataSpace collection failed");
                throw new DataSpaceInterfaceException("updating DataSpace collection failed");
            } else {
                project.setDataSpaceUpdateDate(new Date());
            }
        }
        project = projectDao.update(project);

    }

    public URL createURL(String uri) throws DataSpaceInterfaceException {
        try {
            return new URL(this.dataSpaceUrl + uri);
        }
        catch (MalformedURLException e) {
            throw new DataSpaceInterfaceException(e.getMessage());
        }
    }

    public void login() throws DataSpaceInterfaceException {
        String username = OzTrackApplication.getApplicationContext().getDataSpaceUsername();
        String password = OzTrackApplication.getApplicationContext().getDataSpacePassword();
        URL loginURL = createURL("login");

        HttpPost loginRequest = new HttpPost(loginURL.toString());
        BasicHttpParams params = new BasicHttpParams();
        params.setParameter("username", username);
        params.setParameter("password", password);
        loginRequest.setParams(params);

        try {
            logger.info("attempt DataSpace login");
            HttpResponse loginResponse = httpClient.execute(loginRequest);
            if (loginResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new DataSpaceInterfaceException("DataSpace connection failed: " + loginResponse.getStatusLine());
            }
            logger.info("login successful");
        }
        catch (Exception e) {
            throw new DataSpaceInterfaceException(e.getMessage());
        }
    }

    public int executePostMethod(String uri, String atom, Project project) throws DataSpaceInterfaceException {
        HttpPost postRequest = new HttpPost(uri);
        logger.info("POST " + postRequest.getURI());
        try {
            postRequest.setEntity(new StringEntity(atom, "application/atom+xml"));
            login();
            HttpResponse postResponse = httpClient.execute(postRequest);
            int statusCode = postResponse.getStatusLine().getStatusCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(postResponse.getEntity().getContent()));
            this.dataSpaceResponse = in.readLine();
            in.close();
            logger.info("DataSpace response: " + this.dataSpaceResponse);
            return statusCode;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new DataSpaceInterfaceException("DataSpace POST failed");
        } finally {
            postRequest.releaseConnection();
            logout();
        }
    }

    public void logout() throws DataSpaceInterfaceException {
        try {
            HttpPost postRequest = new HttpPost(createURL("logout").toString());
            httpClient.execute(postRequest);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new DataSpaceInterfaceException("DataSpace logout failed");
        }
        logger.info("logout");
    }

    public int executePutMethod(String uri, String atom, Project project) throws DataSpaceInterfaceException {
        HttpPut putRequest = new HttpPut(uri);
        logger.info("PUT " + putRequest.getURI().toString());
        try {
            putRequest.setEntity(new StringEntity(atom, "application/atom+xml"));
            login();
            HttpResponse putResponse = httpClient.execute(putRequest);
            int statusCode = putResponse.getStatusLine().getStatusCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(putResponse.getEntity().getContent()));
            this.dataSpaceResponse = in.readLine();
            in.close();
            logger.info("DataSpace response: " + this.dataSpaceResponse);
            return statusCode;
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new DataSpaceInterfaceException(e.getMessage());
        } finally {
            putRequest.releaseConnection();
            logout();
        }
    }

    public int executeDeleteMethod(String uri, Project project) throws DataSpaceInterfaceException {
        HttpDelete deleteRequest = new HttpDelete(createURL(uri).toString());
        logger.info("DELETE " + deleteRequest.getURI().toString());
        try {
            login();
            HttpResponse deleteResponse = httpClient.execute(deleteRequest);
            int statusCode = deleteResponse.getStatusLine().getStatusCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(deleteResponse.getEntity().getContent()));
            this.dataSpaceResponse = in.readLine();
            in.close();
            logger.info("DataSpace response: " + this.dataSpaceResponse);
            return statusCode;
        } catch (Exception e) {
            logger.info("delete failed");
            throw new DataSpaceInterfaceException(e.getMessage());
        } finally {
            deleteRequest.releaseConnection();
            logout();
        }
    }

    public String getUriFromResponse(String responseType) throws DataSpaceInterfaceException {
        DocumentBuilder db;
        Document doc;
        String href = "";
        String uri = "";
        String url = this.dataSpaceUrl + responseType + "/";
        try {
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(dataSpaceResponse));
            doc = db.parse(is);
            NodeList nodes = doc.getElementsByTagName("link");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                String rel = element.getAttribute("rel");
                if ((rel != null) && rel.equals("edit")) {
                    href = element.getAttribute("href");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (!href.equals("")) {
            uri = href.replace(url, "");
        }
        return uri;
    }
}