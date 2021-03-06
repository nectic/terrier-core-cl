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
public class TuneSkipgramSelfCl {


	protected static final Logger logger = LoggerFactory.getLogger(TuneSkipgramSelfCl.class);

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {

		System.out.println("Usage: ");
		System.out.println("args[0]: path to skipgram vectors");
		System.out.println("args[1]: path to cbow vectors");
		System.out.println("args[2]: path to src vectors");
		System.out.println("args[3]: path to trg vectors");
		System.out.println("args[4]: number of top translation terms");
		System.out.println("args[5]: value of mu");

		int numtopterms = Integer.parseInt(args[4]);
		Index index = Index.createIndex();

		TranslationLMManager tlm_w2v_skipgram = new TranslationLMManager(index);
		
		System.out.println("Initialise src vectors ");
		tlm_w2v_skipgram.initialiseW2V_atquerytime_src(args[2]);
		System.out.println("src vectors initialised");
		
		
		System.out.println("Initialise trg vectors ");
		tlm_w2v_skipgram.initialiseW2V_atquerytime_trg(args[3]);
		System.out.println("trg vectors initialised");
		
		tlm_w2v_skipgram.setTranslation("w2v_cl");
		tlm_w2v_skipgram.setRarethreshold(index.getCollectionStatistics().getNumberOfDocuments()/200);
		tlm_w2v_skipgram.setTopthreshold(index.getCollectionStatistics().getNumberOfDocuments()/2);
		tlm_w2v_skipgram.setNumber_of_top_translation_terms(numtopterms);

		double [ ]  muvalues = { 10.0, 20.0, 40.0, 50.0, 100.0, 200.0, 300.0, 500.0, 1000.0, 2000.0, 2500.0, 3000.0};
		for(int i = 0; i<muvalues.length;i++) {

			double mu = muvalues[i];
			tlm_w2v_skipgram.setDirMu(mu);
			TRECDocnoOutputFormat TRECoutput_w2v_skipgram = new TRECDocnoOutputFormat(index);
			PrintWriter pt_w2v_skipgram = new PrintWriter(new File("var/results/res_dir_w2v_skipgram_self_cl" + String.valueOf(mu) + ".res"));
			QuerySource querySource = getQueryParser();
			// iterating through the queries
			while (querySource.hasNext()) {
				String query = querySource.next();
				String qid = querySource.getQueryId();
				qid = qid.substring(1,qid.length());
				System.out.println("Scoring with Dir TLM with w2v (skipgram)");
				//scoring with LM dir w2v
				Request rq_w2v = new Request();
				rq_w2v.setOriginalQuery(query);
				rq_w2v.setQueryID(qid);
				rq_w2v = tlm_w2v_skipgram.runMatching(rq_w2v, "w2v_self_cl", "dir");
				TRECoutput_w2v_skipgram.printResults(pt_w2v_skipgram, rq_w2v, "dir_w2v_skipgram_self_cl", "Q0", 1000);

			}
			pt_w2v_skipgram.flush();
			pt_w2v_skipgram.close();
		}
	}

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

}
