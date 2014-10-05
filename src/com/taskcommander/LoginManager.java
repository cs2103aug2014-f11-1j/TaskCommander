package com.taskcommander;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

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
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.TasksScopes;

//@author A0112828H
public class LoginManager {
	private static final String CLIENT_ID = "1009064713944-qqeb136ojidkjv4usaog806gcafu5dmn.apps.googleusercontent.com";
	private static final String CLIENT_SECRET = "9ILpkbnlGwVMQiqh10za3exf";
	private static final String APPLICATION_NAME = "Task Commander";

	private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

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

	private boolean loggedIn = false;

	/**
	 * Returns a LoginManager instance.
	 */
	public LoginManager() {
		httpTransport = new NetHttpTransport();
		jsonFactory = new JacksonFactory();
	}

	/**
	 * Connects to Google and initialises Tasks service.
	 * Requests can be sent once this method is successfully
	 * executed.
	 */
	public Tasks createTasksService(){
		if (isLoggedIn()) {
			return new Tasks.Builder(httpTransport, jsonFactory, credential)
			.setApplicationName(APPLICATION_NAME).build();
		} else {
			return null;
		}
	}

	/**
	 * Connects to Google and initialises Calendar service.
	 * Requests can be sent once this method is successfully
	 * executed.
	 */
	public Calendar createCalendarService(){
		if (isLoggedIn()) {
			return new Calendar.Builder(httpTransport, jsonFactory, credential)
			.setApplicationName(APPLICATION_NAME).build();
		} else {
			return null;
		}
	}
	
	/**
	 * Checks if logged in, else tries to login and get
	 * credentials.
	 */
	private boolean isLoggedIn() {
		if (credential == null) {
			credential = getCredential();
		}
		return true;
	}

	/**
	 * Gets a GoogleCredential for use in Google API requests,
	 * either from storage or by sending a request to Google.
	 * @return           Credential
	 */
	private GoogleCredential getCredential() {
		GoogleCredential newCredential = buildCredential();
		if(hasStoredCredential()) {
			setTokensFromStoredCredential(newCredential);
		} else {
			setTokensFromLogin(newCredential);
		}
		saveCredential(newCredential);
		return newCredential;
	}

	/**
	 * Requests the user to login and requests authorisation
	 * tokens. Then sets the tokens in the given credential.
	 * @param credential
	 */
	private void setTokensFromLogin(GoogleCredential credential) {
		credential.setFromTokenResponse(requestAuthorisation());
	}

	/**
	 * Gets stored credential from data store and sets the tokens 
	 * in the given credential.
	 * @param credential
	 */
	private void setTokensFromStoredCredential(GoogleCredential newCredential) {
		StoredCredential storedCredential;
		try {
			storedCredential = dataStore.get(USERNAME);
			newCredential.setAccessToken(storedCredential.getAccessToken());
			newCredential.setRefreshToken(storedCredential.getRefreshToken());
		} catch (IOException e) {
			System.out.println(Global.MESSAGE_EXCEPTION_IO);
		}
	}

	/**
	 * Checks if a credential with the given username has been stored in the 
	 * data store directory.
	 * @return If stored credential exists.
	 */
	private boolean hasStoredCredential() {
		try {
			File dataStoreFile = new File(DATA_STORE_DIR);
			dataStoreFactory = new FileDataStoreFactory(dataStoreFile);
			dataStore = dataStoreFactory.getDataStore(DATA_STORE_NAME);

			return !dataStore.isEmpty() && dataStore.containsKey(USERNAME);
		} catch (IOException e) {
			System.out.println(Global.MESSAGE_EXCEPTION_IO);
			return false;
		}
	}

	/**
	 * Builds a GoogleCredential.
	 * @return
	 */
	private GoogleCredential buildCredential() {
		GoogleCredential credential = new GoogleCredential.Builder()
		.setJsonFactory(jsonFactory)
		.setTransport(httpTransport)
		.setClientSecrets(CLIENT_ID, CLIENT_SECRET)
		.addRefreshListener(new DataStoreCredentialRefreshListener(USERNAME, dataStore))
		.build();
		return credential;
	}

	/**
	 * Saves given credential in the datastore.
	 */
	public void saveCredential(GoogleCredential credential){
		StoredCredential storedCredential = new StoredCredential();
		storedCredential.setAccessToken(credential.getAccessToken());
		storedCredential.setRefreshToken(credential.getRefreshToken());
		try {
			dataStore.set(USERNAME, storedCredential);
		} catch (IOException e) {
			System.out.println(Global.MESSAGE_EXCEPTION_IO);
		}
	}

	/**
	 * Returns a token response after requesting user
	 * login and authorisation.
	 * 
	 * Makes an authorisation request to Google and prints
	 * out a URL. The user has to enter the given URL into 
	 * a browser and login to Google, then paste the returned
	 * authorisation code into command line. 
	 */
	private GoogleTokenResponse requestAuthorisation() {
		try {
			flow = buildAuthorisationCodeFlow(httpTransport, jsonFactory, dataStoreFactory);
		} catch (IOException e) {
			System.out.println(Global.MESSAGE_EXCEPTION_IO);
		}

		askUserForAuthorisationCode(flow);
		String code = getUserInput();

		return getTokenResponse(flow, code);
	}

	/**
	 * Sends a token request to get a GoogleTokenResponse.
	 * If an IOException occurs, returns null.
	 * 
	 * @param flow
	 * @param code
	 * @return      Token response
	 */
	private GoogleTokenResponse getTokenResponse(GoogleAuthorizationCodeFlow flow, String code) {
		try {
			GoogleTokenResponse response = flow.newTokenRequest(code)
					.setRedirectUri(REDIRECT_URI).execute();
			return response;
		} catch (IOException e) {
			System.out.println(Global.MESSAGE_EXCEPTION_IO);
		}
		return null;
	}

	/**
	 * Creates the authorisation code flow needed for the authorisation URL.
	 * 
	 * @param httpTransport
	 * @param jsonFactory
	 * @param fdsf           FileDataStoreFactory
	 * @return               GoogleAuthorizationCodeFlow object
	 * @throws IOException
	 */
	private GoogleAuthorizationCodeFlow buildAuthorisationCodeFlow(
			HttpTransport httpTransport, 
			JsonFactory jsonFactory,
			FileDataStoreFactory fdsf) throws IOException {
		return new GoogleAuthorizationCodeFlow.Builder(
				httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET, Arrays.asList(TasksScopes.TASKS))
		.setAccessType(FLOW_ACCESS_TYPE)
		.setApprovalPrompt(FLOW_APPROVAL_PROMPT)
		.setDataStoreFactory(fdsf).build();
	}

	/**
	 * Creates the authorisation URL, asks the user to open the URL and sign in, then type in the
	 * authorisation code from Google.
	 * @param flow
	 */
	private void askUserForAuthorisationCode(GoogleAuthorizationCodeFlow flow) {
		String url = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
		System.out.println("Please open the following URL in your browser then type the authorization code:");
		System.out.println("  " + url);
	}

	/**
	 * Reads user input and returns it. 
	 * @return      String of user input.
	 */
	private String getUserInput() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input = "";
		try {
			input = br.readLine();
			br.close();
		} catch (IOException e) {
			System.out.println(Global.MESSAGE_EXCEPTION_IO);
		}
		return input;
	}

}
