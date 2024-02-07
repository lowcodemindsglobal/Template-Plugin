package com.lowcodeminds.plugins.tasks;

import org.apache.log4j.Logger;

import com.appiancorp.suiteapi.content.ContentService;
import com.appiancorp.suiteapi.process.exceptions.SmartServiceException;
import com.aspose.words.Document;
import com.lowcodeminds.plugins.template.utils.PluginContext;
import com.lowcodeminds.plugins.template.utils.TemplateServices;
/**
 * This task update/refresh the document after apply templating with all the other tasks
 * This task should be call as the last task when  processing document,
 * 
 * @author ChamindaFernando
 *
 */

public class UpdateDocTask extends TemplateTasks{
	
	com.aspose.words.Document doc;
	PluginContext context;
	public ContentService contentService = null;
	
	private static final Logger LOG = Logger.getLogger(UpdateDocTask.class);

	public UpdateDocTask(Document doc, PluginContext context) {
		super(doc, context);
		this.doc = doc;
		this.context = context;
	}

	@Override
	public void applyTemplate() throws SmartServiceException {
		try {
			doc.updateFields();
		} catch (Exception e) {
			context.setErrorOccured(true);
			context.setErrorMessage("Error when processing updating document  Task");
			LOG.error("Exception in UpdateDocTask processing ", e);
			throw TemplateServices.createException(e, getClass());
		}
		
	}

}
