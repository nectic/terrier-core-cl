package org.terrier.clir.dico;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;

public class ReadXMLFile {
	
		
	public static void main(final String[] args) {
		
		HashMap<String, TreeMultimap<Double, String> > w2v_inverted_translation = new HashMap<String, TreeMultimap<Double, String> >();
		
		/*
		 * Etape 1 : récupération d'une instance de la classe "DocumentBuilderFactory"
		 */
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			/*
			 * Etape 2 : création d'un parseur
			 */
			final DocumentBuilder builder = factory.newDocumentBuilder();

			/*
			 * Etape 3 : création d'un Document
			 */
			final Document document= builder.parse(new File("French_English_Online_Dictiona.xml"));

			/*
			//Affichage du prologue
			System.out.println("*************PROLOGUE************");
			System.out.println("version : " + document.getXmlVersion());
			System.out.println("encodage : " + document.getXmlEncoding());		
			System.out.println("standalone : " + document.getXmlStandalone());
			*/

			/*
			 * Etape 4 : récupération de l'Element racine
			 */
			final Element racine = document.getDocumentElement();

			//Affichage de l'élément racine
			System.out.println("\n*************RACINE************");
			System.out.println(racine.getNodeName());

			/*
			 * Etape 5 : récupération des personnes
			 */
			final NodeList racineNoeuds = racine.getChildNodes();
			final int nbRacineNoeuds = racineNoeuds.getLength();
			
			System.out.println("nbRacineNoeuds = " + nbRacineNoeuds);
			
			
			
			for (int i = 0; i<nbRacineNoeuds; i++) {
				if(racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
					final Element dicoEntry = (Element) racineNoeuds.item(i);

					//Affichage d'un mot du dico
					//System.out.println("\n*************************");
					//System.out.println("mot : " + dicoEntry.getAttribute("d:title"));
					
					TreeMultimap<Double, String> inverted_translation_w = TreeMultimap.create(Ordering.natural().reverse(), Ordering.natural());

					final Element src = (Element) dicoEntry.getElementsByTagName("h1").item(0);
					final Element trg = (Element) dicoEntry.getElementsByTagName("p").item(0);
					
					String w = src.getTextContent();
					
					String[] u_tab = trg.getTextContent().split(",");
					
					for (int j = 0; j < u_tab.length; j++) {
						String u = u_tab[j];
						double p_dico_w_u = (double)1/(double)u_tab.length;
						inverted_translation_w.put(p_dico_w_u, u);
						System.out.println("("+w+","+u+") = "+p_dico_w_u);
					}
					
					w2v_inverted_translation.put(w, inverted_translation_w);
					
				
					//System.out.println(src.getTextContent() + ":" + trg.getTextContent());
					
					/*
					final NodeList telephones = personne.getElementsByTagName("telephone");
					final int nbTelephonesElements = telephones.getLength();

					for(int j = 0; j<nbTelephonesElements; j++) {
						final Element telephone = (Element) telephones.item(j);

						//Affichage du téléphone
						System.out.println(telephone.getAttribute("type") + " : " + telephone.getTextContent());
					}
					*/
				}				
			}			
		}
		catch (final ParserConfigurationException e) {
			e.printStackTrace();
		}
		catch (final SAXException e) {
			e.printStackTrace();
		}
		catch (final IOException e) {
			e.printStackTrace();
		}		
	}
}
