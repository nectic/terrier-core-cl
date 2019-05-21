package org.terrier.clir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ExtractQrel {

	public static void main(String[] args) throws IOException {
		
		String doc = "GH";
		
		PrintWriter writer = new PrintWriter("qrels_"+doc, "UTF-8");
		
		File file = new File("share/clef/qrels"); 
		BufferedReader br = new BufferedReader(new FileReader(file)); 
		String st; 
		while ((st = br.readLine()) != null) {
			if(st.contains(doc)) {
				System.out.println(st);
				writer.println(st);
			}
		}
		br.close();
		writer.close();

	}

}
