package org.oztrack.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.xml.sax.SAXException;

public class DataSpaceInterface {
    protected final Log logger = LogFactory.getLog(getClass());
    // TODO: DAO should not appear in this layer.
    private ProjectDao projectDao;
    // TODO: DAO should not appear in this layer.
    private UserDao userDao;
    private String dataSpaceURL;
    private String dataSpaceResponse;

    public DataSpaceInterface(ProjectDao projectDao, UserDao userDao) {
        this.projectDao = projectDao;
        this.userDao = userDao;
        this.dataSpaceURL = OzTrackApplication.getApplicationContext().getDataSpaceURL();
    }

    public void deleteFromDataSpace(Project project) throws DataSpaceInterfaceException {
        User dataSpaceAgent = userDao.getUserById(project.getDataSpaceAgent().getId());
        String agentURI = project.getDataSpaceAgent().getDataSpaceAgentURI();
        String collectionURI = project.getDataSpaceURI();
        boolean deleteAgent = false;

        // do the collection first
        int statusCode = executeDeleteMethod("collections/" + collectionURI, project);
        if ((statusCode != HttpStatus.SC_OK) && (statusCode != HttpStatus.SC_ACCEPTED) && (statusCode != HttpStatus.SC_NO_CONTENT)) {
            logger.info("deleting dataSpace collection failed");
            throw new DataSpaceInterfaceException("deleting dataSpace agent failed.");
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
                logger.info("deleting dataSpace agent failed");
                throw new DataSpaceInterfaceException("deleting dataSpace agent failed.");
            } else {
                dataSpaceAgent.setDataSpaceAgentURI(null);
                dataSpaceAgent.setDataSpaceAgentUpdateDate(new Date());
            }

            userDao.update(dataSpaceAgent);
        }
    }

    public void updateDataSpace(Project project) throws DataSpaceInterfaceException {
        User dataSpaceAgent = userDao.getUserById(project.getDataSpaceAgent().getId());
        String agentURI = project.getDataSpaceAgent().getDataSpaceAgentURI();
        String collectionURI = project.getDataSpaceURI();

        DataSpaceCollection dsi = new DataSpaceCollection(project);



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
                throw new DataSpaceInterfaceException("creating dataSpace agent failed.");
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
                logger.info("updating dataSpace agent failed");
                throw new DataSpaceInterfaceException("updating dataSpace agent failed");
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
                throw new DataSpaceInterfaceException("creating dataSpace collection failed.");
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
                throw new DataSpaceInterfaceException("updating dataSpace collection failed");
            } else {
                project.setDataSpaceUpdateDate(new Date());
            }
        }
        project = projectDao.update(project);

    }

    public URL createURL(String uri) throws DataSpaceInterfaceException {

        URL url;

        try {
            url = new URL(this.dataSpaceURL + uri);
        } catch (MalformedURLException e) {
            throw new DataSpaceInterfaceException(e.getMessage());
        }
        return url;
    }

    public HttpState login() throws DataSpaceInterfaceException {

        HttpClient httpClient = new HttpClient();
        String username = OzTrackApplication.getApplicationContext().getDataSpaceUsername();
        String password = OzTrackApplication.getApplicationContext().getDataSpacePassword();
        URL loginURL = createURL("login");

        PostMethod loginPostMethod = new PostMethod(loginURL.toString());
        loginPostMethod.addParameter("username", username);
        loginPostMethod.addParameter("password", password);

        try {
            logger.info("attempt dataspace login");
            httpClient.executeMethod(loginPostMethod);
            if (loginPostMethod.getStatusCode() != HttpStatus.SC_OK) {
                throw new DataSpaceInterfaceException("dataSpace connection failed: " + loginPostMethod.getStatusLine());
            }
            logger.info("login successful");

        } catch (HttpException e) {
            throw new DataSpaceInterfaceException(e.getMessage());
        } catch (IOException e) {
            throw new DataSpaceInterfaceException(e.getMessage());
        }
        return httpClient.getState();

    }

    public PostMethod buildPostMethod(String uri, String atom) throws DataSpaceInterfaceException {

        final String atomFinal = atom;
        URL url = createURL(uri);
        PostMethod postMethod = new PostMethod(url.toString());
        postMethod.setRequestEntity(new RequestEntity() {
            @Override
            public void writeRequest(OutputStream out) throws IOException {
                out.write(atomFinal.getBytes());
            }

            @Override
            public boolean isRepeatable() {
                return true;
            }

            @Override
            public String getContentType() {
                return "application/atom+xml";
            }

            @Override
            public long getContentLength() {
                return atomFinal.getBytes().length;
            }

        });

        return postMethod;
    }

    public int executePostMethod(String uri, String atom, Project project) throws DataSpaceInterfaceException {

        PostMethod postMethod = buildPostMethod(uri, atom);
        logger.info("POST " + postMethod.getPath());
        HttpState authenticatedState = login();
        HttpClient httpClient = new HttpClient();
        int statusCode;
        try {
            statusCode = httpClient.executeMethod(HostConfiguration.ANY_HOST_CONFIGURATION, postMethod, authenticatedState);
            BufferedReader in = new BufferedReader(new InputStreamReader(postMethod.getResponseBodyAsStream()));
            this.dataSpaceResponse = in.readLine();
            in.close();
            logger.info("dataSpace response: " + this.dataSpaceResponse);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new DataSpaceInterfaceException("dataSpace POST failed");
        } finally {
            postMethod.releaseConnection();
            logout(httpClient, authenticatedState);
        }
        return statusCode;
    }

    public void logout(HttpClient httpClient, HttpState authenticatedState) throws DataSpaceInterfaceException {
        try {
            PostMethod postMethod = new PostMethod(createURL("logout").toString());
            httpClient.executeMethod(HostConfiguration.ANY_HOST_CONFIGURATION, postMethod, authenticatedState);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new DataSpaceInterfaceException("dataSpace logout failed");
        }
        logger.info("logout");
    }

    public PutMethod buildPutMethod(String uri, String atom) throws DataSpaceInterfaceException {

        final String atomFinal = atom;

        URL putURL = createURL(uri);
        PutMethod putMethod = new PutMethod(putURL.toString());
        putMethod.setRequestEntity(new RequestEntity() {
            @Override
            public void writeRequest(OutputStream out) throws IOException {
                out.write(atomFinal.getBytes());
            }

            @Override
            public boolean isRepeatable() {
                return true;
            }

            @Override
            public String getContentType() {
                return "application/atom+xml";
            }

            @Override
            public long getContentLength() {
                return atomFinal.getBytes().length;
            }

        });

        return putMethod;
    }

    public int executePutMethod(String uri, String atom, Project project) throws DataSpaceInterfaceException {

        PutMethod putMethod = buildPutMethod(uri, atom);
        logger.info("PUT " + putMethod.getPath().toString());
        HttpClient httpClient = new HttpClient();
        HttpState authenticatedState = login();
        int statusCode;
        try {
            statusCode = httpClient.executeMethod(HostConfiguration.ANY_HOST_CONFIGURATION, putMethod, authenticatedState);
            BufferedReader in = new BufferedReader(new InputStreamReader(putMethod.getResponseBodyAsStream()));
            this.dataSpaceResponse = in.readLine();
            in.close();
            logger.info("dataSpace response: " + this.dataSpaceResponse);
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new DataSpaceInterfaceException(e.getMessage());
        } finally {
            putMethod.releaseConnection();
            logout(httpClient, authenticatedState);
        }
        return statusCode;
    }

    public int executeDeleteMethod(String uri, Project project) throws DataSpaceInterfaceException {

        URL url = createURL(uri);
        DeleteMethod deleteMethod = new DeleteMethod(url.toString());

        logger.info("DELETE " + deleteMethod.getPath().toString());
        HttpClient httpClient = new HttpClient();
        HttpState authenticatedState = login();
        int statusCode;
        try {
            statusCode = httpClient.executeMethod(HostConfiguration.ANY_HOST_CONFIGURATION, deleteMethod, authenticatedState);
            BufferedReader in = new BufferedReader(new InputStreamReader(deleteMethod.getResponseBodyAsStream()));
            this.dataSpaceResponse = in.readLine();
            in.close();
            logger.info("dataSpace response: " + this.dataSpaceResponse);
        } catch (Exception e) {
            logger.info("delete failed");
            throw new DataSpaceInterfaceException(e.getMessage());
        } finally {
            deleteMethod.releaseConnection();
            logout(httpClient, authenticatedState);
        }
        return statusCode;
    }


    public String getUriFromResponse(String responseType) throws DataSpaceInterfaceException {

        DocumentBuilder db;
        Document doc;
        String href = "";
        String uri = "";
        String url = this.dataSpaceURL + responseType + "/";

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

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (!href.equals("")) {
            uri = href.replace(url, "");
        }

        return uri;

    }
}