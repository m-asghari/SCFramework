package edu.usc.infolab.sc.DataSetGenerators;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class InputReader {
	String filePath;
	
	public InputReader(String fp) {
		this.filePath = fp;
	}
	
	public void ReadXML() {
		Document dom = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.parse(this.filePath);		
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		Element dataSpec = dom.getDocumentElement();
		
		Element workers = (Element) dataSpec.getElementsByTagName("Workers").item(0);
		WorkerGenerator wg = new WorkerGenerator();
		wg.Parse(workers);
		
		//Element tasks = (Element) dataSpec.getElementsByTagName("Tasks").item(0);
		//TaskGenerator.Parse(tasks);		
	}

}
