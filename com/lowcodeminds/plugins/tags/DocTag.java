package com.lowcodeminds.plugins.tags;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.appiancorp.suiteapi.process.exceptions.SmartServiceException;
import com.lowcodeminds.plugins.template.utils.PluginContext;
import com.lowcodeminds.plugins.template.utils.TemplateServices;

public class DocTag extends Tag {
	
	
	private static final Log LOG = LogFactory.getLog(DocTag.class);
	
	public DocTag(com.aspose.words.Document doc,PluginContext context,String tagName){
		super(doc, context,tagName);
	}

	@Override
	public void applyTemplate() throws SmartServiceException {
		 try {
			LOG.debug("Start Processing Doc tags");
		    doc.getMailMerge().execute(fieldNames, fieldValues);
		 }catch(Exception e) {
			 context.setErrorOccured(true);
			 context.setErrorMessage("Error when processing DocTag ");
			 LOG.error("Exception in Doc tag processing " ,e);
			 throw TemplateServices.createException(e, getClass());
		 }
		 
	}
	
	

}
