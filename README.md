#clir terrier-core


bin/trec_terrier.sh --initScore --srcWE=/Volumes/SDEXT/these/vectors_ap8889_skipgram_s1000_w10_neg20_hs0_sam1e-4_iter5.txt --trgWE=/Volumes/SDEXT/these/vectors_ap8889_skipgram_s1000_w10_neg20_hs0_sam1e-4_iter5.txt -Dtrec.topics=share/vaswani_npl/query-text.trec

bin/trec_terrier.sh --initScore --srcWE=/Volumes/SDEXT/these/vectors_ap8889_skipgram_s1000_w10_neg20_hs0_sam1e-4_iter5.txt --trgWE=/Volumes/SDEXT/these/vectors_ap8889_skipgram_s1000_w10_neg20_hs0_sam1e-4_iter5.txt -Dtrec.topics=share/clef/query_title_en.trec -Dclir.score.file=/Volumes/SDEXT/these/score_en_en_vectors_ap8889_skipgram.ser



bin/trec_terrier.sh --initScore --srcWE=/home/mrim/doumbise/data/wiki.multi.fr.vec --trgWE=/home/mrim/doumbise/data/wiki.multi.en.vec -Dtrec.topics=share/clef/query_title_fr.trec -Dclir.score.file=/home/mrim/doumbise/data/score_fr_en_eeb1.ser

bin/trec_terrier.sh -r -Dtrec.topics=share/clef/query_title_en.trec -Dclir.score.file=/home/mrim/doumbise/data/score.ser

bin/trec_terrier.sh -r -Dtrec.model=BM25 -Dtrec.topics=share/clef/query_title_fr.trec -Dclir.method=WeCLIR -Dclir.score.file=/home/mrim/doumbise/data/score_fr_en_eeb2.ser -Dclir.number_of_top_translation_terms=1

bin/trec_terrier.sh -r -c 500.0 -Dtrec.model=DirichletLM -Dtrec.topics=share/clef/query_title_fr.trec -Dclir.method=WeCLIRTLM -Dclir.score.file=/home/mrim/doumbise/data/score_fr_en_eeb1.ser -Dclir.number_of_top_translation_terms=1

bin/trec_terrier.sh -r -c 500.0 -Dtrec.model=DirichletLM -Dtrec.topics=share/clef/query_title_fr.trec -Dclir.method=WeCLIRTLM2 -Dclir.score.file=/home/mrim/doumbise/scores/score_fr_en_eeb1.ser -Dclir.number_of_top_translation_terms=1

-Dclir.src.we=/Volumes/SDEXT/these/
-Dclir.trg.we=/Volumes/SDEXT/these/
-Dclir.score.file=/Volumes/SDEXT/these/score_fr_en_EEB1_2.ser


bin/trec_terrier.sh -i

bin/trec_terrier.sh --printstats

bin/trec_terrier.sh -r -Dtrec.topics=share/

bin/trec_terrier.sh -r -Dtrec.model=BM25 -c 0.4 -Dtrec.topics=share/

bin/trec_terrier.sh -r -Dtrec.model=BM25 -c 0.4 -Dtrec.topics=share/clef/query_title_en.trec

share/clef/query_title_en.trec

bin/trec_terrier.sh -r -Dtrec.model=Hiemstra_LM -c 0.4 -Dtrec.topics=share/clef/clef2003_query_en.txt

bin/trec_terrier.sh -r -Dtrec.model=TF_IDF -c 0.4 -Dtrec.topics=share/clef/clef2003_query_en.txt

bin/trec_terrier.sh -e -Dtrec.qrels=share/clef/qrels


LOAD DATA LOCAL INFILE '/Users/seydoudoumbia/these/expe/terrier-core/we_english_mono_vectors_ap8889_skipgram_s1000_w10_neg20_hs0_sam1e-4_iter5.txt' INTO TABLE vectors_en_mono_ap8889_skipgram FIELDS TERMINATED BY ";" LINES TERMINATED BY "\n";

LOAD DATA LOCAL INFILE '/Volumes/SDEXT/these/wiki.fr.mapped.vec.txt' INTO TABLE clwe_fr_artexte_2018_2  FIELDS TERMINATED BY ";" LINES TERMINATED BY "\n";

LOAD DATA LOCAL INFILE '/Volumes/SDEXT/these/wiki.en.mapped.vec.txt' INTO TABLE clwe_en_artexte_2018_2 FIELDS TERMINATED BY ";" LINES TERMINATED BY "\n";

LOAD DATA LOCAL INFILE '/Volumes/SDEXT/these/load_data_1.txt' INTO TABLE clwe_en_artexte_2018_2 CHARACTER SET latin1 FIELDS TERMINATED BY "|||" LINES TERMINATED BY "\n";

LOAD DATA LOCAL INFILE '/Users/seydoudoumbia/these/expe/word_embedding/wiki.en.mapped.vec.txt' INTO TABLE clwe_en_artexte_2018_2 FIELDS TERMINATED BY ";" LINES TERMINATED BY "\n";


LOAD DATA LOCAL INFILE '/Users/seydoudoumbia/these/expe/terrier-core/terms_scores.txt' INTO TABLE  terms_similarity_mono_ap8889 FIELDS TERMINATED BY " " LINES TERMINATED BY "\n";



/Users/seydoudoumbia/projets/weclir2
/Users/seydoudoumbia/projets/weclir2/var/index
/Users/seydoudoumbia/projets/weclir2/clef/clef2003_query_en.txt
/Users/seydoudoumbia/softwares/terrier-core-4.2/var/results/trec
vectors_ap8889_skipgram_s1000_w10_neg20_hs0_sam1e-4_iter5.txt
vectors_ap8889_cbow_s1000_w10_neg20_hs0_sam1e-4_iter5.txt
10
500


/Users/seydoudoumbia/projets/weclir2
/Users/seydoudoumbia/softwares/terrier-core-4.2/var/index
/Users/seydoudoumbia/projets/weclir2/clef/clef2003_query_en.txt
/Users/seydoudoumbia/softwares/terrier-core-4.2/var/results/trec
wiki.multi.fr.vec
wiki.multi.en.vec
10
500


/Users/seydoudoumbia/these/expe/terrier-core
index
share/clef/query_all_fr.txt
/Users/seydoudoumbia/these/expe/terrier-core/var/results/trec
wiki.multi.fr.vec
wiki.multi.en.vec
1
500
vectors_ap8889_skipgram_s1000_w10_neg20_hs0_sam1e-4_iter5.txt
vectors_ap8889_cbow_s1000_w10_neg20_hs0_sam1e-4_iter5.txt
wiki.multi.fr.vec
wiki.multi.en.vec



-Dterrier.home=/Users/seydoudoumbia/these/expe/workspace_phd_1/terrier-core-4.4
-Dtrec.topics=/Users/seydoudoumbia/these/expe/workspace_phd_1/terrier-core-4.4/share/clef/query_title_fr.trec
-Dtrec.model= BM25



/Volumes/SDEXT/these/vectors_ap8889_skipgram_s1000_w10_neg20_hs0_sam1e-4_iter5.txt
/Volumes/SDEXT/these/vectors_ap8889_cbow_s1000_w10_neg20_hs0_sam1e-4_iter5.txt
/Volumes/SDEXT/these/wiki.multi.fr.vec
/Volumes/SDEXT/these/wiki.multi.en.vec
1
500

tokeniser=UTFTokeniser

-Dterrier.home=/Users/seydoudoumbia/these/expe/workspace_php_test/terrier-core-4.4
-Dtrec.topics=/Users/seydoudoumbia/these/expe/workspace_php_test/terrier-core-4.4/share/clef/query_title_fr.trec



bin/trec_terrier.sh --tunelm -Dtrec.topics=share/clef/query_en.trec -Dclir.src.we=/home/mrim/doumbise/clir/vectors_ap8889_cbow_s1000_w10_neg20_hs0_sam1e-4_iter5.txt -Dclir.number_of_top_translation_terms=1


bin/trec_terrier.sh --tuneseuilcl -Dtrec.topics=share/clef/topics/query_fr.trec -Dclir.src.we=/home/mrim/doumbise/clir/wiki.multi.fr.vec -Dclir.trg.we=/home/mrim/doumbise/clir/wiki.multi.en.vec -Dclir.score.file=/home/mrim/doumbise/clir/score_fr_en_eeb1_1.ser -Dclir.number_of_top_translation_terms=1 -Dclir.score.seuil=0.00002


bin/trec_terrier.sh --tuneskipgramcl -Dtrec.topics=share/clef/topics/query_title_fi.trec -Dclir.src.we=/home/mrim/doumbise/clir/wiki.multi.fi.vec -Dclir.trg.we=/home/mrim/doumbise/clir/wiki.multi.en.vec -Dclir.score.file=/home/mrim/doumbise/clir/score_fr_en_eeb1_1.ser -Dclir.number_of_top_translation_terms=1 -Dclir.score.seuil=0.00002


-Dterrier.home=/Users/seydoudoumbia/projets/these/workspace_these/mai/terrier-core-4.4
-Dtrec.topics=/Users/seydoudoumbia/projets/these/workspace_these/mai/terrier-core-4.4/share/clef/query_title_fr.trec


bin/trec_terrier.sh -r -Dtrec.model=BM25 -c 0.4 -Dtrec.topics=share/clef/query_title_en.trec

bin/trec_terrier.sh -r -Dtrec.model=DirichletLM -Dtrec.topics=share/clef/query_en.trec

bin/trec_terrier.sh -r -Dtrec.model=DirichletLM -Dtrec.topics=share/clef/query_fr.trec

bin/trec_terrier.sh -e -Dtrec.qrels=share/clef/qrels

--tuneskipgramcl
--tuneskipgramnotnormcl
--tuneseuilcl
--tuneseuilnotnormcl
--tunesdicocl
--tuneskipgramfullcl
--tunelm
--aggregation
--aggregationtf




bin/trec_terrier.sh --tuneskipgramcl -Dtrec.topics=share/clef/topics/query_title_fi.trec






git add .
git commit -m "clir"
git push -u origin master

git pull origin master
mvn package -DskipTests

git init
git add README.md
git commit -m "first commit"
git remote add origin https://github.com/nectic/terrier-core-cl.git
git push -u origin master


