package com.taskcommander;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.client.auth.oauth2.DataStoreCredentialRefreshListener;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.TasksScopes;

//@author A0112828H
public class LoginManager implements Observer {
	private static final String CLIENT_ID = "1009064713944-qqeb136ojidkjv4usaog806gcafu5dmn.apps.googleusercontent.com";
	private static final String CLIENT_SECRET = "9ILpkbnlGwVMQiqh10za3exf";
	private static final String APPLICATION_NAME = "Task Commander";

	private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:auto";

	private static final String DATA_STORE_DIR = "credentials";
	private static final String DATA_STORE_NAME = "credentialDataStore";

	// Option to request access type for application. Can be "online" or "offline".
	private static final String FLOW_ACCESS_TYPE = "offline";
	// Option to request approval prompt type for application. Can be "force" or "auto".
	private static final String FLOW_APPROVAL_PROMPT = "auto";

	private static final String USERNAME = "User";

	private static FileDataStoreFactory dataStoreFactory;

	private HttpTransport httpTransport;
	private JsonFactory jsonFactory;
	private GoogleAuthorizationCodeFlow flow;
	private DataStore<StoredCredential> dataStore;
	private GoogleCredential credential;
	
	private static Logger logger = Logger.getLogger(LoginManager.class.getName());
	
	/**
	 * This method returns a LoginManager
	 * To be called by GoogleAPIConnector
	 * @return
	 */
	public static LoginManager getInstanceOf() {
		LoginManager manager = new LoginManager();
		return manager;
	}
	
	/**
	 * Returns a LoginManager instance and attempts to login.
	 */
	private LoginManager() {
		httpTransport = new NetHttpTransport();
		jsonFactory = new JacksonFactory();
		File dataStoreFile = new File(DATA_STORE_DIR);
		try {
			logger.log(Level.INFO,"Retrieving DataStore");
			dataStoreFactory = new FileDataStoreFactory(dataStoreFile);
			dataStore = dataStoreFactory.getDataStore(DATA_STORE_NAME);
		} catch (IOException e) {
			logger.log(Level.WARNING,"IOException: Unable to retrieve DataStore", e);
		}
		login();
	}

	/**
	 * Connects to Google and builds a new Tasks service.
	 * Requests can be sent once this method is successfully
	 * executed.
	 */
	public Tasks getTasksService(){
		if (isLoggedIn()) {
			return new Tasks.Builder(httpTransport, jsonFactory, credential)
			.setApplicationName(APPLICATION_NAME).build();
		} else {
			return null;
		}
	}

	/**
	 * Connects to Google and builds a new Calendar service.
	 * Requests can be sent once this method is successfully
	 * executed.
	 */
	public Calendar getCalendarService(){
		if (isLoggedIn()) {
			return new Calendar.Builder(httpTransport, jsonFactory, credential)
			.setApplicationName(APPLICATION_NAME).build();
		} else {
			return null;
		}
	}
	
	/**
	 * Gets the datastore factory used
	 * @return			dataStoreFactory
	 */
	public static DataStoreFactory getDataStoreFactory() {
		return dataStoreFactory;
	}
	
	/**
	 * Attempts to login.
	 */
	private void login() {
		if (credential == null) {
			credential = getCredential();
		}
	}
	
	/**
	 * Checks if logged in.
	 */
	private boolean isLoggedIn() {
		return credential == null;
	}

	/**
	 * Gets a GoogleCredential for use in Google API requests,
	 * either from storage or by sending a request to Google.
	 * @return           Credential
	 */
	private GoogleCredential getCredential() {
		logger.log(Level.INFO,"Attempting to get credentials.");
		credential = buildCredential();
		if(hasStoredCredential()) {
			logger.log(Level.INFO,"Using stored credential.");
			setTokensFromStoredCredential();
		} else {
			logger.log(Level.INFO,"Getting new credential from login.");
			setTokensFromLogin();
		}
		logger.log(Level.INFO,"Saving credential...");
		saveCredential();
		return credential;
	}

	/**
	 * Requests the user to login and requests authorisation
	 * tokens. Has to wait for user to login in the UI and 
	 * retrieve token.
	 */
	private void setTokensFromLogin() {
		requestAuthorisation();
	}

	/**
	 * Gets stored credential from data store and sets the tokens 
	 * in the local credential.
	 */
	private void setTokensFromStoredCredential() {
		StoredCredential storedCredential;
		try {
			storedCredential = dataStore.get(USERNAME);
			credential.setAccessToken(storedCredential.getAccessToken());
			credential.setRefreshToken(storedCredential.getRefreshToken());
		} catch (IOException e) {
			logger.log(Level.WARNING,"IOException: Unable to retrieve StoredCredential.", e);
		}
	}

	/**
	 * Checks if a credential with the given username has been stored in the 
	 * data store directory.
	 * @return If stored credential exists.
	 */
	private boolean hasStoredCredential() {
		try {
			return !dataStore.isEmpty() && dataStore.containsKey(USERNAME);
		} catch (IOException e) {
			logger.log(Level.WARNING,"IOException: Unable to check if DataStore contains key.", e);
			return false;
		}
	}

	/**
	 * Builds a GoogleCredential.
	 * @return
	 */
	private GoogleCredential buildCredential() {
		logger.log(Level.INFO,"Building credential.");
		GoogleCredential newCredential = new GoogleCredential.Builder()
		.setJsonFactory(jsonFactory)
		.setTransport(httpTransport)
		.setClientSecrets(CLIENT_ID, CLIENT_SECRET)
		.addRefreshListener(new DataStoreCredentialRefreshListener(USERNAME, dataStore))
		.build();
		return newCredential;
	}

	/**
	 * Saves the local credential in the datastore.
	 */
	private void saveCredential(){
		StoredCredential storedCredential = new StoredCredential();
		storedCredential.setAccessToken(credential.getAccessToken());
		storedCredential.setRefreshToken(credential.getRefreshToken());
		try {
			dataStore.set(USERNAME, storedCredential);
		} catch (IOException e) {
			logger.log(Level.WARNING,"IOException: Unable to store credential in DataStore.", e);
		}
	}

	/**
	 * Makes an authorisation request to Google.
	 */
	private void requestAuthorisation() {
		try {
			flow = buildAuthorisationCodeFlow();
		} catch (IOException e) {
			logger.log(Level.WARNING,"IOException: Unable to build authorisation code flow.", e);
		}
		
		getAuthorisationCode();
	}

	/**
	 * Sends a token request to get a GoogleTokenResponse.
	 * If an IOException occurs, returns null.
	 * 
	 * @param code
	 * @return      Token response
	 */
	private GoogleTokenResponse getTokenResponse(String code) {
		try {
			GoogleTokenResponse response = flow.newTokenRequest(code)
					.setRedirectUri(REDIRECT_URI).execute();
			return response;
		} catch (IOException e) {
			logger.log(Level.WARNING,"IOException: Unable to execute GoogleTokenResponse", e);
		}
		return null;
	}

	/**
	 * Creates the authorisation code flow needed for the authorisation URL.
	 * @return               GoogleAuthorizationCodeFlow object
	 * @throws IOException
	 */
	private GoogleAuthorizationCodeFlow buildAuthorisationCodeFlow() throws IOException {
		return new GoogleAuthorizationCodeFlow.Builder(
				httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET, Arrays.asList(TasksScopes.TASKS, CalendarScopes.CALENDAR))
		.setAccessType(FLOW_ACCESS_TYPE)
		.setApprovalPrompt(FLOW_APPROVAL_PROMPT)
		.setDataStoreFactory(dataStoreFactory).build();
	}

	/**
	 * Creates the authorisation URL and passes it to the UI.
	 */
	private void getAuthorisationCode() {
		String url = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
		TaskCommander.ui.getCodeFromUser(url);
	}

	@Override
	public void update(Observable obs, Object obj) {
		String code = TaskCommander.ui.getCode();
		credential.setFromTokenResponse(getTokenResponse(code));
		saveCredential();
	}

}
