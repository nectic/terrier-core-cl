package org.terrier.clir;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terrier.applications.batchquerying.QuerySource;
import org.terrier.querying.Request;
import org.terrier.structures.DocumentIndex;
import org.terrier.structures.DocumentIndexEntry;
import org.terrier.structures.Index;
import org.terrier.structures.MetaIndex;
import org.terrier.structures.outputformat.TRECDocnoOutputFormat;
import org.terrier.utility.ApplicationSetup;
import org.terrier.utility.ArrayUtils;

/**
 * 
 */

/**
 * @author zuccong
 *
 */
public class TuneSkipGramCl {


	protected static final Logger logger = LoggerFactory.getLogger(TuneSkipGramCl.class);
	
	/**
	 * Get the query parser that is being used.
	 * 
	 * @return The query parser that is being used.
	 */
	protected static QuerySource getQueryParser() {

		String topicsParser = ApplicationSetup.getProperty("trec.topics.parser", "TRECQuery");

		String[] topicsFiles = null;
		QuerySource rtr = null;
		try {
			Class<? extends QuerySource> queryingClass = Class.forName(
					topicsParser.indexOf('.') > 0 ? topicsParser
							: "org.terrier.structures." + topicsParser)
					.asSubclass(QuerySource.class);

			if ((topicsFiles = ArrayUtils.parseCommaDelimitedString(ApplicationSetup.getProperty("trec.topics", ""))).length > 0) {
				//condensing the following code any further results in warnings
				Class<?>[] types = { String[].class };
				Object[] params = { topicsFiles };
				rtr = queryingClass.getConstructor(types).newInstance(params);
			} else {
				logger.error("Error instantiating topic file.  Please set the topic file(s) using trec.topics property"
						, new IllegalArgumentException());
			}
			// } catch (ClassNotFoundException cnfe) {

		} catch (Exception e) {
			logger.error("Error instantiating topic file QuerySource called " + topicsParser, e);
		}
		return rtr;
	}

	public void processQueries() throws IOException, InterruptedException {
		
		int numtopterms = Integer.valueOf(ApplicationSetup.getProperty("clir.number_of_top_translation_terms","1"));
		String src_we = ApplicationSetup.getProperty("clir.src.we","/Volumes/SDEXT/these/wiki.multi.fr.vec");
		String trg_we = ApplicationSetup.getProperty("clir.trg.we","/Volumes/SDEXT/these/wiki.multi.en.vec");

		Index index = Index.createIndex();

		TranslationLMManager tlm_w2v_skipgram = new TranslationLMManager(index);
		
		System.out.println("Initialise src & trg vectors ");
		tlm_w2v_skipgram.initialiseW2V_cl(src_we,trg_we);;
		System.out.println("src trg vectors initialised");
		
		
		tlm_w2v_skipgram.setTranslation("w2v_cl");
		tlm_w2v_skipgram.setRarethreshold(index.getCollectionStatistics().getNumberOfDocuments()/200);
		tlm_w2v_skipgram.setTopthreshold(index.getCollectionStatistics().getNumberOfDocuments()/2);
		tlm_w2v_skipgram.setNumber_of_top_translation_terms(numtopterms);

		double [ ]  muvalues = { 10.0, 20.0, 40.0, 50.0, 100.0, 200.0, 300.0, 500.0, 1000.0, 2000.0, 2500.0, 3000.0};
		
		//double [ ]  muvalues = {0.75};
		
		for(int i = 0; i<muvalues.length;i++) {
			double mu = muvalues[i];
			tlm_w2v_skipgram.setDirMu(mu);
			TRECDocnoOutputFormat TRECoutput_w2v_skipgram = new TRECDocnoOutputFormat(index);
			PrintWriter pt_w2v_skipgram = new PrintWriter(new File("var/results/res_dir_w2v_cl_mu_" + String.valueOf(mu) + ".res"));
			QuerySource querySource = getQueryParser();
			// iterating through the queries
			while (querySource.hasNext()) {
				String query = querySource.next();
				String qid = querySource.getQueryId();
				tlm_w2v_skipgram.setQid(qid);
				qid = qid.substring(1,qid.length());
				System.out.println("Scoring with Dir TLM with w2v (skipgram)");
				//scoring with LM dir w2v
				Request rq_w2v = new Request();
				rq_w2v.setOriginalQuery(query);
				rq_w2v.setQueryID(qid);
				rq_w2v = tlm_w2v_skipgram.runMatching(rq_w2v, "w2v_cl", "dir");
				TRECoutput_w2v_skipgram.printResults(pt_w2v_skipgram, rq_w2v, "dir_w2v_cl_skipgram", "Q0", 1000);
			}
			pt_w2v_skipgram.flush();
			pt_w2v_skipgram.close();
		}
	}

}
