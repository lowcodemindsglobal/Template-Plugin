package com.lowcodeminds.plugins.tasks;

import org.apache.log4j.Logger;

import com.appiancorp.suiteapi.content.ContentService;
import com.appiancorp.suiteapi.process.exceptions.SmartServiceException;
import com.lowcodeminds.plugins.template.utils.PluginContext;

public abstract class TemplateTasks {

	com.aspose.words.Document doc;
	PluginContext context;
	public ContentService contentService = null;
	

	
	private static final Logger LOG = Logger.getLogger(TemplateTasks.class);

	TemplateTasks(com.aspose.words.Document doc, PluginContext context) {
		this.doc = doc;
		this.context = context;

	}
	
	TemplateTasks(com.aspose.words.Document doc, PluginContext context,ContentService contentService) {
		this.doc = doc;
		this.context = context;
		this.contentService = contentService;

	}

	
	
	
	public void apply() throws SmartServiceException {

		if (context.isErrorOccured()) {
			return;
		}
		applyTemplate();

	}

	public abstract void applyTemplate() throws SmartServiceException;

}
