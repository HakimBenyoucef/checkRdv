package com.remote.connection;

import java.util.Date;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class ConnectToUrlUsingBasicAuthentication {

	public static void main(String[] args) {
		try {
			submittingForm();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void submittingForm() throws Exception {
	    final WebClient webClient = new WebClient();

	    Date date = new Date();
	    int mois = date.getMonth();
	    System.out.println("MOIS :"+mois);
	    // Get the first page
	    final HtmlPage page1 = webClient.getPage("https://secure.fr.vente-privee.com/authentication/portal/FR");

	    System.out.println("Connecting ");
	    // Get the form that we are dealing with and within that form, 
	    // find the submit button and the field that we want to change.
	    final HtmlForm form = page1.getFormByName("login_form");

	    final HtmlSubmitInput button = form.getInputByValue("Continuer");
	    final HtmlTextInput textField = form.getInputByName("email");
	    final HtmlPasswordInput motDePasse = form.getInputByName("pwd");

	    // Change the value of the text field
	    textField.setValueAttribute("remondas.houari@yopmail.com");
	    motDePasse.setValueAttribute("123456");

	    // Now submit the form by clicking the button and get back the second page.
	    final HtmlPage page2 = button.click();
	    
	    System.out.println("Titre : "+page2.getTitleText());
            //page2.get(null)
	    DomNodeList<DomElement> listLi = page2.getElementsByTagName("li");
	    for(DomElement element : listLi)
	    {	    	

	    	System.out.println("===========================");
	    	System.out.println("element : "+element.asText());
	    	DomNodeList<DomNode> child1 = element.getChildNodes();
	    	for(DomNode element2 : child1)
		    {
	    		if(element2.getNodeName().equals("a")){

	    			System.out.println("<"+element2.getNodeName()+">");
		    		HtmlAnchor premierRDV = (HtmlAnchor) element2;
		    		if(premierRDV.getHrefAttribute().equals("javascript:void(0)")){

				    	System.out.println("element : "+element2.asText());
				    	
		    		}
			    	//if(element2.get)
	    		}
	    		
		    }
	    }
            final HtmlAnchor premierRDV = page2.getAnchorByHref("javascript:void(0)");

            System.out.println("a : "+premierRDV.asText());
            
            final HtmlPage page3 = premierRDV.click();
            final HtmlForm form2 = page3.getFormByName("ajax_confirm_action");
            final HtmlSubmitInput button2 = form2.getInputByValue("Confirmer");
            
            final HtmlPage page4 = button2.click();
            
            
            
            
	    webClient.close();
	}
	
}
