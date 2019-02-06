package com.remote.connection;

import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHeading2;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

public class ConnectToPrefectureWebSite {

	private static String adresseRenouvellementEtudiant = "http://www.essonne.gouv.fr/booking/create/14056/1";
	private static String adresseTIR = "http://www.essonne.gouv.fr/booking/create/18220/1";

	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	private static int nbr_retry = 1;

	private static final WebClient webClient = new WebClient();

	private static final String sender = "mail@gmail.com"; // To be changed
	private static final String password = "PassworD"; // To be changed
	private static final String receiver = "mail@gmail.com"; // To be changed
	
	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ConnectToPrefectureWebSite.class.getName());

	public static void main(String[] args) {
		final Runnable rdfFinder = new Runnable() 
		{
			public void run() {
				findRdvTIR();
			}
		};

		scheduler.scheduleWithFixedDelay(rdfFinder, 0, 180, TimeUnit.SECONDS);

		webClient.close();

	}

	private static void findRdvTIR() {
 
		System.out.println("#########################");
		System.out.printf ("####  %d retry       #####\n",nbr_retry++);
		System.out.println("#########################");

		try {
			// --------- STEP 1: Connect to the web page
			HtmlPage page1 = webClient.getPage(adresseTIR);
			HtmlElement node = page1.getHtmlElementById("inner_Booking");
			HtmlHeading2 h2 = (HtmlHeading2) node.getByXPath("//h2").get(0);
			LOG.info("Titre de la première page: {}", h2.getTextContent());

			final HtmlForm form1 = page1.getFormByName("create");
			final HtmlSubmitInput button = form1.getInputByName("nextButton");
			// Choix de la nature du rendez-vous (pour l'essai, choisir étudiant)
			final HtmlRadioButtonInput natureRdvA = page1.getHtmlElementById("planning18221");
			natureRdvA.setChecked(true);

			// // --------- STEP 1: See the result of the second page
			final HtmlPage page2 = button.click();
			Thread.sleep(3000);

			node = page2.getHtmlElementById("inner_Booking");
			h2 = (HtmlHeading2) node.getByXPath("//h2").get(0);

			LOG.info("Titre de la 2eme page: {}", h2.getTextContent());

			if (h2.getTextContent().contains("Description de la nature"))
			{
				// Rdv trouvé
				LOG.info("Rdv trouvé. Envoyer un email");
				sendMail();
			}
			else
			{

				LOG.info("Aucun rdv trouvé");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void findRdvRenouvellemenEtudiantt() {

		System.out.println("#########################");
		System.out.printf("#### %d retry       #####\n", nbr_retry++);
		System.out.println("#########################");

		try {
			// --------- STEP 1: Connect to the web page

			HtmlPage page1 = webClient.getPage(adresseRenouvellementEtudiant);
			HtmlElement node = page1.getHtmlElementById("inner_Booking");

			HtmlHeading2 h2 = (HtmlHeading2) node.getByXPath("//h2").get(0);

			System.out.println("LOG -- INFO: Titre de la première page: " + h2.getTextContent());

			final HtmlForm form1 = page1.getFormByName("create");
			final HtmlSubmitInput button = form1.getInputByName("nextButton");
			// Choix de la nature du rendez-vous (pour l'essai, choisir étudiant)
			final HtmlRadioButtonInput natureRdvA = page1.getHtmlElementById("planning21029");
			natureRdvA.setChecked(true);

			// // --------- STEP 1: See the result of the second page
			final HtmlPage page2 = button.click();
			Thread.sleep(3000);

			node = page2.getHtmlElementById("inner_Booking");
			h2 = (HtmlHeading2) node.getByXPath("//h2").get(0);
			System.out.println("LOG -- INFO: Titre de la 2eme page: " + h2.getTextContent());

			if (h2.getTextContent().contains("Description de la nature")) {
				// Rdv trouvé
				System.out.println("LOG -- INFO: Rdv trouvé. Envoyer un email");
				sendMail();
			}
			else
			{
				System.out.println("LOG -- INFO: Aucun rdv trouvé");
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void sendMail() {

		/*
		 * If you have any issue with sending mail using this app, please follow these
		 * steps: 1- Make sure Less secure apps is TURNED ON
		 * https://www.google.com/settings/security/lesssecureapps 2- Allow each app to
		 * send email Go to https://accounts.google.com/b/0/DisplayUnlockCaptcha and
		 * click on Continue 3- This time: you can use your app to send email and all
		 * operations are allowed.
		 * 
		 * More links: https://support.google.com/accounts/answer/6010255
		 * https://productforums.google.com/forum/#!topic/gmail/9KCgzXY4G_c
		 */

		Properties props = new Properties();
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(sender, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(sender));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
			message.setSubject("VIIIITE RDV LIBRE !!!!");
			message.setText(
					"il y a une plage horaire qui s'est libérée dans le site de la préfecture.\n " + "dépèche toi !!");

			Transport.send(message);

			System.out.println("Sent message successfully....");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

}
