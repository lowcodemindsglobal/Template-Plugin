package com.lowcodeminds.plugins.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.appiancorp.suiteapi.process.exceptions.SmartServiceException;
import com.lowcodeminds.plugins.template.utils.PluginContext;

public abstract class TemplateTasks {

	com.aspose.words.Document doc;
	PluginContext context;
	
	private static final Log LOG = LogFactory.getLog(TemplateTasks.class);

	TemplateTasks(com.aspose.words.Document doc, PluginContext context) {
		this.doc = doc;
		this.context = context;

	}

	public void apply() throws SmartServiceException {

		if (context.isErrorOccured()) {
			return;
		}
		applyTemplate();

	}

	public abstract void applyTemplate() throws SmartServiceException;

}
