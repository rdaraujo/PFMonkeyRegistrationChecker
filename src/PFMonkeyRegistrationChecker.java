import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Checks PFMonkey NZB registration site to see whether registration is already open for new members.
 * @author rda
 *
 */
public class PFMonkeyRegistrationChecker {
	
	private static String URL = "http://pfmonkey.com/register";
	private static String INVITE_ONLY_TEXT = "Registrations are currently invite only";
	private static String SEND_EMAIL_COMMAND =
			"/usr/local/bin/sendEmail.pl -f <fromEmail> -t <toEmail> -s <smtpAddr> -o tls=<tls> -xu <user> -xp <password> -u <subject> -m <messageBody>";

	public static void main(String[] args) {
		try {
			boolean regOpen = true;
			
			Document doc = Jsoup.connect(URL).get();
			Elements divsError = doc.select("div.error");
			
			for (Element div : divsError) {
				String divText = div.hasText() ? div.ownText() : "";
				if (divText.contains(INVITE_ONLY_TEXT)) {
					regOpen = false;
				}
			}
			
			if (regOpen) {
				System.out.println("Registration is now open!");
				System.out.println("Sending email now..");
				sendEmail("from@mail.com", "to@mail.com", "smtp.mail.com", true, "smtp@mail.com", "password", "*** PFMonkey Registration is now open ***", "Go to http://pfmonkey.com/register and try to register an account while registration is still opened.");
			} else {
				System.out.println("It seems registration is still closed! :(");
			}
			
		} catch (IOException e) {
			System.err.println("Some error has occurred: " + e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.err.println("Some error has occurred: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void sendEmail(String fromEmail, String toEmail, String smtpAddr, boolean tls, String user, String password, String subject, String messageBody)
			throws IOException, InterruptedException {
		
		String emailCmd = SEND_EMAIL_COMMAND.replace("<fromEmail>", fromEmail)
				.replace("<toEmail>", toEmail)
				.replace("<smtpAddr>", smtpAddr)
				.replace("<tls>", tls ? "yes" : "no")
				.replace("<user>", user)
				.replace("<password>", password)
				.replace("<subject>", subject)
				.replace("<messageBody>", messageBody);

		Process proc = Runtime.getRuntime().exec(emailCmd);

		int exitVal = proc.waitFor();
		System.exit(exitVal);
	}
}
