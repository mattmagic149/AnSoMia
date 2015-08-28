package analysers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import database.Index;

public class TextBlobAnalyser implements Job
{
	
	public TextBlobAnalyser() {
		System.out.println("TextBlobAnalyser ctor called");
	}
	
	@Override	
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
	}
	
	public static void main(String a[]){
		try{
		 
			/*String prg = "import sys\nprint int(sys.argv[1])+int(sys.argv[2])\n";
			BufferedWriter out = new BufferedWriter(new FileWriter("test1.py"));
			out.write(prg);
			out.close();*/
			/*String argv1 = "This is a test argument 1";
			 
			//ProcessBuilder pb = new ProcessBuilder("python","test1.py",""+number1,""+number2);
			ProcessBuilder pb = new ProcessBuilder("python","AnSoMiaPy/analyser/test1.py", argv1);
			
			Process p = pb.start();
			 
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while((line = in.readLine()) != null) {
				System.out.println(line);
			}*/
			String text = "The titular threat of The Blob has always struck me as the ultimate movie monster: an insatiably hungry, amoeba-like mass able to penetrate virtually any safeguard, capable of--as a doomed doctor chillingly describes it assimilating flesh on contact. Snide comparisons to gelatin be damned, it's a concept with the most devastating of potential consequences, not unlike the grey goo scenario proposed by technological theorists fearful of artificial intelligence run rampant.";

		    

			PythonInterpreter interpreter = new PythonInterpreter();

			//interpreter.set("text", text);

			interpreter.execfile("AnSoMiaPy/analyser/test1.py");

			//interpreter.exec("import sys");
			//interpreter.exec("from textblob import TextBlob");
			//interpreter.set("text", text);			
			//interpreter.exec("blob = TextBlob(text)");
			//interpreter.exec("print blob.noun_phrases");
					
			
			//interpreter.exec("for sentence in blob.sentences:\n\tprint sentence.sentiment.polarity");

			//interpreter.exec("translated_text = blob.translate(to=\"de\")");
			PyObject translated_text = interpreter.get("translated_text");
			//PyObject sentiment = interpreter.get("sentiment");
			interpreter.close();
			
	        System.out.println("translated_text: " + translated_text.toString());

	        //System.out.println("sentiment: " + sentiment);

			// execute a function that takes a string and returns a string
			/*PyObject someFunc = interpreter.get("funcName");
			PyObject result = someFunc.__call__(new PyString("Test!"));
			String realResult = (String) result.__tojava__(String.class);*/
			

			
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
}
