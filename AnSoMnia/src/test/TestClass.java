package test;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.HttpRequestManager;
import utils.HttpRequester;

public class TestClass {
	
	public static HttpRequestManager http_req_manager = HttpRequestManager.getInstance();

	public static void main(String[] args) throws IOException {
		
		/*Map<String, String> data_map = new LinkedHashMap<String, String>();
		data_map.put("client", "p");
		data_map.put("ie", "UTF-8");
		data_map.put("oe", "UTF-8");
		data_map.put("text", "Hallo wie geht es dir?");
		data_map.put("sl", "de");
		data_map.put("tl", "en");
		
		
		String response = Jsoup.connect("http://translate.google.com/translate_a/t")
				.ignoreContentType(true)
		        .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.168 Safari/535.19")  
		        .referrer("http://www.google.com")
		        .timeout(13000)
		        .data(data_map)
		        .method(Connection.Method.POST)
		        .execute()
		        .body();
		
		System.out.println(response);*/
		
		String text = "SYGNIS AG unterzeichnet nicht-exklusive Vertriebsvereinbarung mit der "
				+ "skandinavischen Biotop Oy für Finnland SYGNIS AG unterzeichnet nicht-exklusive"
				+ " Vertriebsvereinbarung mit der ---------------------------------------"
				+ "------------------------------ SYGNIS AG unterzeichnet nicht-exklusive "
				+ "Vertriebsvereinbarung mit der - Biotop ist ein führender skandinavischer Life "
				+ "Science Distributor mit - Vertriebsvereinbarung umfasst SYGNIS komplettes "
				+ "Produktportfolio für die Probenvorbereitung von DNA im Bereich Next Generation "
				+ "Sequencing Madrid, Spanien und Heidelberg, 17. Juni 2015 - Die SYGNIS AG "
				+ "(Frankfurt: LIO1; ISIN: DE000A1RFM03; Prime Standard) gab heute die Unterzeichnung "
				+ "einer nicht-exklusiven Vereinbarung mit Biotop Oy für den Vertrieb ihres Als Teil "
				+ "der Vereinbarung gewährt SYGNIS Biotop das nicht-exklusive Recht zur Vermarktung "
				+ "und zum Verkauf ihrer TruePrime(TM) Produktlinie für die Primer-freie Amplifikation "
				+ "ganzer Genome sowie der SunScript(TM) Produktfamilie thermostabiler rerverser "
				+ "Transkriptasen (RT) für die Übersetzung von RNA-Molekülen in DNA-Molekülen an Kunden "
				+ "im weiten Feld der TruePrime(TM)-Produkte basieren auf der Kombination der "
				+ "unternehmenseigenen DNA-Primase 'TthPrimPol' mit einer Phi29-Polymerase, was die "
				+ "Amplifikation ganzer Genome aus minimalen Probenmengen bis hin zu einzelnen Zellen "
				+ "ohne Die SunScript(TM) Produktlinie von SYGNIS umfasst eine Reihe von Kits, die auf "
				+ "einer unternehmenseigenen reversen Transkriptase basieren, die zu den thermostabilsten "
				+ "und schnellsten heute im Handel erhältlichen Enzymen dieser Art gehört. Reverse "
				+ "Transkriptasen werden standardmäßig in der Molekularbiologie verwendet, um die "
				+ "genetische Information aus RNA in DNA zu übersetzen. Sie ermöglichen damit die "
				+ "Analyse von RNA in einer Reihe von DNA-Analysetechnologien, wie z.B. der Next "
				+ "Generation Sequencing oder der Polymerase-Kettenreaktion (PCR) für die Erkennung von "
				+ "Genexpressionsmustern Biotop ist ein spezialisierter Anbieter eines breiten Portfolios "
				+ "innovativer Produkte und Reagenzien für das weite Feld der Molekularbiologie sowie "
				+ "zellbiologischer Anwendungen wie Next Generation Neben einer weltweiten Verbreitung "
				+ "über ein schnell wachsendes Netz internationaler Distributoren, kann das Produkt auch "
				+ "direkt über den SYGNIS Online-Shop unter http://www.sygnis.com/shop bestellt werden. "
				+ "TruePrime(TM) ist der Markenname einer revolutionären, neuen Multiple Displacement "
				+ "Amplifikationstechnologie und eines der Schlüsselprodukte in SYGNIS' Portfolio. "
				+ "TruePrime(TM) basiert auf der Kombination der unternehmenseigenen DNA-Primase 'TthPrimPol' "
				+ "mit einer hoch prozessiven und akkuraten Phi29-Polymerase zur gleichmäßigen Amplifikation "
				+ "vollständiger Genome aus nur wenigen Zellen oder Einzelzellen für unterschiedlichste "
				+ "Anwendungen wie Next Generation Sequencing (NGS) und die Analyse von Einzelzellen. "
				+ "Die außerordentliche Fähigkeit der Phi29-Polymerase zur Trennung von DNA-Doppelsträngen "
				+ "erlaubt die direkte Bildung neuer Primer durch TthPrimPol auf den gerade freigelegten "
				+ "Strängen. Diese Primer dienen als Anknüpfpunkte für die Phi29-Polymerase, die dann "
				+ "direkt mit der Amplifikation beginnt, was zu einer exponentiellen DNA-Vermehrung unter "
				+ "isothermen Bedingungen führt, ohne den bisher notwendigen Einsatz von SunScript(TM) RT, "
				+ "eine reverse Transkriptase, die für eine hohe Thermostabilität entwickelt wurde, basiert "
				+ "auf dem gut charakterisierten humanen Immundefizienz-Virus RT (HIV-1 RT). Das Enzym ist "
				+ "bis zu Temperaturen von 85 C aktiv. SunScript(TM) reverse Transkriptase liegt in zwei "
				+ "Versionen vor. SunScript(TM) RT RNaseH+ ist für die Synthese von komplementärer DNA "
				+ "(cDNA) als auch für den Einsatz bei RT-PCR und quantitativen PCR-Reaktionen optimiert, "
				+ "während SunScript(TM) RT RNaseH- eine hohe Ausbeute bei der Synthese kompletter cDNA aus "
				+ "langen RNA-Molekülen für die Konstruktion von cDNA-Bibliotheken erlaubt, wie sie zum "
				+ "Beispiel für Next Generation Sequencing Analysen benötigt werden. Für weitere "
				+ "Informationen wenden Sie sich bitte an: Email: pdelahuerta@sygnis.com Email: "
				+ "raimund.gabriel@mc-services.eu Nach der Fusion mit der X-Pol Biotech im Jahr 2012 hat "
				+ "sich SYGNIS auf die Entwicklung und Vermarktung von Produkten für die DNA-Amplifikation "
				+ "und Sequenzierung spezialisiert. Die an der Deutschen Börse notierte SYGNIS AG "
				+ "(Prime Standard, Tick: LIO1; ISIN: E000A1RFM03), hat ein kommerzielles Produkt für "
				+ "die DNA-Amplifikation kompletter Genome, SensiPhi(R), an einen industrieführenden "
				+ "Partner lizenziert und entwickelt derzeit zwei eigene Produktlinien, TruePrime(TM) "
				+ "auf Basis ihrer proprietären TruePrime(TM)-Technologie für die Amplifikation kompletter "
				+ "Genome sowie SunScript(TM) reverse Transkriptase zur Übersetzung genetischer Information "
				+ "von RNA in DNA. Beide Produktlinien adressieren zentrale Herausforderungen in schnell "
				+ "wachsenden Bereichen der Molekularbiologie und Anwendungen im ### Bestimmte in dieser "
				+ "Pressemitteilung enthaltene Aussagen, bei denen es sich weder um ausgewiesene finanzielle "
				+ "Ergebnisse noch um andere historische Daten handelt, sind vorausblickender Natur. "
				+ "Es geht dabei insbesondere um Prognosen künftiger Ereignisse, Trends, Pläne oder Ziele. "
				+ "Solche Aussagen sind nicht als absolut gesichert zu betrachten, da sie naturgemäß "
				+ "bekannten und unbekannten Risiken und Unwägbarkeiten unterliegen und durch andere "
				+ "Faktoren beeinflusst werden können, in deren Folge die tatsächlichen Ergebnisse und "
				+ "die Pläne und Ziele der SYGNIS wesentlich von den getroffenen oder implizierten "
				+ "prognostischen Aussagen abweichen können. SYGNIS verpflichtet sich nicht, diese "
				+ "Aussagen öffentlich zu aktualisieren oder zu revidieren, weder im Lichte neuer "
				+ "Informationen, künftiger -------------------------------------------------------"
				+ "-------------- 17.06.2015 Veröffentlichung einer Corporate News/Finanznachricht, "
				+ "übermittelt durch DGAP - ein Service der EQS Group AG. Für den Inhalt der Mitteilung "
				+ "ist der Emittent / Herausgeber Die DGAP Distributionsservices umfassen gesetzliche "
				+ "Meldepflichten, Corporate News/Finanznachrichten und Pressemitteilungen. "
				+ "Medienarchiv unter http://www.dgap-medientreff.de und ------------------------------"
				+ "--------------------------------------- Börsen: Regulierter Markt in Frankfurt "
				+ "(Prime Standard); Freiverkehr in Berlin, Düsseldorf, Hamburg, München, ------------"
				+ "---------------------------------------------------------";
		
		Map<String, String> data_map = new LinkedHashMap<String, String>();
		data_map.put("client", "p");
		data_map.put("ie", "UTF-8");
		data_map.put("oe", "UTF-8");
		data_map.put("text", text);
		data_map.put("sl", "de");
		data_map.put("tl", "en");
		
		HttpRequester hr = http_req_manager.getCorrespondingHttpRequester("http://translate.google.com/translate_a/t");

		String json = hr.getContentWithProxy("/translate_a/t", data_map, false);
		
		JSONArray arr;
		String tmp;
		StringBuffer result = new StringBuffer();
		JSONObject obj;
		try {
			arr = ((JSONObject)new JSONObject(json)).getJSONArray("sentences");
			for(int i = 0; i < arr.length(); i++) {
				tmp = arr.get(i).toString();
				obj = new JSONObject(tmp);
				result.append(obj.get("trans"));
			}
			
			System.out.println(result.toString());
						
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}


		
		/*Company company_1 = HibernateSupport.readOneObjectByStringId(Company.class, "AT000000STR1");
		Company company_2 = HibernateSupport.readOneObjectByStringId(Company.class, "AT00000AMAG3");
		Company company_3 = HibernateSupport.readOneObjectByStringId(Company.class, "AT00000BENE6");
		Company company_4 = HibernateSupport.readOneObjectByStringId(Company.class, "AT00000FACC2");
		
		

		DateFormat date_format = new SimpleDateFormat("dd.MM.yy");
		Date from;
		Date to;
		try {
			from = date_format.parse("02.08.15");
			to = date_format.parse("20.08.15");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		ArrayList<MarketValue> values_1 = company_3.getMarketValuesBetweenDates(from, to);
		ArrayList<MarketValue> values_2 = company_4.getMarketValuesBetweenDates(from, to);
		
		MarketValueAnalyser mva = new MarketValueAnalyser();
		mva.normalizeArrays(values_1, values_2);
		
		System.out.println(mva.calculateCorrelationCoefficient(values_1, values_2));*/
	}

}
