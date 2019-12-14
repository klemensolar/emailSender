
/**
 *
 * @author Klemen
 */
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class GmailRandomSender {

    private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart. If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_INSERT, GmailScopes.GMAIL_COMPOSE, GmailScopes.GMAIL_SEND, GmailScopes.MAIL_GOOGLE_COM);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GmailRandomSender.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void main(String... args) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Print the labels in the user's account.
        String user = "me";
        /*
        
        ListLabelsResponse listResponse = service.users().labels().list(user).execute();
        List<Label> labels = listResponse.getLabels();
        if (labels.isEmpty()) {
            System.out.println("No labels found.");
        } else {
            System.out.println("Labels:");
            for (Label label : labels) {
                System.out.printf("- %s\n", label.getName());
            }
        }
*/
        Map<String, String> secretSantaMap = createMap();
        //System.out.println("-SecretSantaMAP-BEFORE:" + " " + secretSantaMap);
        printMap(secretSantaMap);

        secretSantaMap = randomize(secretSantaMap);

        printMap(secretSantaMap);
        //System.out.println("-SecretSantaMAP-AFTER: " + secretSantaMap.size() + " " + secretSantaMap);

        //String santa = "Tadeja";
        //String to = "klemen.solar@gmail.com";
        String from = "secret.santa.4.this.year@gmail.com";
        String subject = "Tvoj secret santa se skriva v mailu.";
        //String bodyText = "Tvoj secret santa:" + santa;
        int i = 1;

        for (Map.Entry<String, String> entry : secretSantaMap.entrySet()) {
            String santa = entry.getKey();
            String to = entry.getValue();
            String bodyText = "Tvoj secret santa:" + santa + " Ne pozabi, da je limit 20€. Have fun, Secret santa";
            MimeMessage emailContent;
            try {
                emailContent = SendEmail.createEmail(to, from, subject, bodyText);
                SendEmail.createDraft(service, user, emailContent);
            } catch (MessagingException ex) {
                Logger.getLogger(GmailRandomSender.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static Map<String, String> createMapTest() {
        Map<String, String> map = new HashMap<>();
        map.put("Klemen1", "klemen.solar+1@gmail.com");
        map.put("Klemen2", "klemen.solar+2@gmail.com");
        map.put("Klemen3", "klemen.solar+3@gmail.com");
        map.put("Klemen4", "klemen.solar+4@gmail.com");
        map.put("Klemen5", "klemen.solar+5@gmail.com");
        map.put("Klemen6", "klemen.solar+6@gmail.com");

        return map;
    }

    private static Map<String, String> createMap() {
        Map<String, String> map = new HashMap<>();
        map.put("Klemen", "klemen.solar@gmail.com");
        map.put("Tadeja", "tadeja.marn@gmail.com");
        map.put("Matjaz", "matjaz.vogrin@gmail.com");
        map.put("Damjana", "damjana_zajec@yahoo.com");
        map.put("Miro", "miro.drca1978@gmail.com");
        map.put("Mojca", "mojca.drca@gmail.com");
        map.put("Primoz", "primoz.cerar1@gmail.com");
        map.put("Ann", "ratsamee.cerar@gmail.com");

        return map;
    }

    private static Map<String, String> randomize(Map<String, String> map) {
        // Get a random entry from the HashMap.
        int i = 0;
        while (isNotRandomized(map)) {
            i++;
            Object[] crunchifyKeys = map.keySet().toArray();
            Object oldkey = crunchifyKeys[new Random().nextInt(crunchifyKeys.length)];
            Object newKey = crunchifyKeys[new Random().nextInt(crunchifyKeys.length)];
            Object oldValue = map.get(oldkey);
            Object newValue = map.get(newKey);
            //System.out.println("Random Value    : " + oldkey + " :: " + oldValue);
            //System.out.println("New random value: " + newKey + " :: " + newValue);
            map.replace(oldkey.toString(), oldValue.toString(), newValue.toString());
            map.replace(newKey.toString(), newValue.toString(), oldValue.toString());
        }
        System.out.println("i:" + i);

        return map;
    }

    private static boolean isRandomized(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String k = entry.getKey().toLowerCase();
            String v = entry.getValue().toLowerCase();
            if ("ann".equals(k)) {
                k = "ratsamee";
            }
            if (v.contains(k)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNotRandomized(Map<String, String> map) {
        return !isRandomized(map);
    }

    private static void printMap(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.printf("%-25s -> %s\n", entry.getValue(), entry.getKey());
        }
    }
}
